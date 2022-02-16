package com.example.RPGPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RPGPlugin extends JavaPlugin {

    @Override
    @SuppressWarnings({"unchecked"})
    public void onEnable() {    //플러그인 활성화시 실행
        SerializeManager.loadFile();
        if (SerializeManager.yml.contains("Plugin.playerClass")) {
            PlayerClassManager.setPlayerClassMap((Map<UUID, PlayerClass>) SerializeManager.yml.get("Plugin.playerClass"));
        }

        Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand());
        Objects.requireNonNull(getCommand("reinforce")).setExecutor(new ReinforceCommand());
        Objects.requireNonNull(getCommand("cl")).setExecutor(new PlayerClassCommand());
        getServer().getPluginManager().registerEvents(new ReinforceInvManager(), this);
        getServer().getPluginManager().registerEvents(new ShopInvManager(), this);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::Save, 20L * 30, 20L * 30);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "---------\nRPGPlugin 활성화 !!\n---------");  //서버의 로그에 출력
    }

    @Override
    public void onDisable() {   //플러그인 비활성화시 실행
        Save();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "---------\nRPGPlugin 비활성화 !!\n---------");  //서버의 로그에 출력
    }

    public void Save() {
        SerializeManager.yml.set("Plugin.playerClass", PlayerClassManager.getPlayerClassMap());
        SerializeManager.saveFile();
    }
}

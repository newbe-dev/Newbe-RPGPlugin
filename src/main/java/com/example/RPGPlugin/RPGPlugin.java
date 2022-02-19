package com.example.RPGPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RPGPlugin extends JavaPlugin {

    @Override
    public void onEnable() {    //플러그인 활성화시 실행
        SerializeManager.loadFile();
        if (SerializeManager.yml.contains("Plugin.playerClass")) {
            List<Map<?, ?>> mapList = SerializeManager.yml.getMapList("Plugin.playerClass");
            PlayerClassManager.setPlayerClassMap((Map<String, String>) mapList.get(0));
        }

        Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand());
        Objects.requireNonNull(getCommand("reinforce")).setExecutor(new ReinforceCommand());
        Objects.requireNonNull(getCommand("class")).setExecutor(new PlayerClassCommand());
        Objects.requireNonNull(getCommand("class")).setTabCompleter(new PlayerClassCommand());
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
        List<Map<String, String>> mapList = Arrays.asList(DefaultMapToStringMap(PlayerClassManager.getPlayerClassMap()));
        SerializeManager.yml.set("Plugin.playerClass", mapList);
        SerializeManager.saveFile();
        Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD + ChatColor.BLUE.toString() + "저장 완료 !");
    }

    public Map<String, String> DefaultMapToStringMap(Map<UUID, PlayerClass> map) {
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<UUID, PlayerClass> entry : map.entrySet()) {
            newMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return newMap;
    }
}

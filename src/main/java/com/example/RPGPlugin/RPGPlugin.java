package com.example.RPGPlugin;

import com.example.RPGPlugin.PlayerClass.PlayerClassCommand;
import com.example.RPGPlugin.PlayerClass.PlayerClassManager;
import com.example.RPGPlugin.PlayerClasses.Rogue;
import com.example.RPGPlugin.Quest.QuestCommand;
import com.example.RPGPlugin.Quest.QuestListener;
import com.example.RPGPlugin.Quest.QuestManager;
import com.example.RPGPlugin.Reinforce.ReinforceCommand;
import com.example.RPGPlugin.Shop.ShopCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RPGPlugin extends JavaPlugin {

    @Override
    public void onEnable() {    //플러그인 활성화시 실행
        SerializeManager.loadFile();
        if (SerializeManager.yml.contains("Plugin.playerClass")) { // 플레이어 직업
            List<Map<?, ?>> mapList = SerializeManager.yml.getMapList("Plugin.playerClass");
            PlayerClassManager.setPlayerClassMap((Map<String, String>) mapList.get(0));
        }

        Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand());
        Objects.requireNonNull(getCommand("shop")).setTabCompleter(new ShopCommand());
        Objects.requireNonNull(getCommand("reinforce")).setExecutor(new ReinforceCommand());
        Objects.requireNonNull(getCommand("reinforce")).setTabCompleter(new ReinforceCommand());
        Objects.requireNonNull(getCommand("class")).setExecutor(new PlayerClassCommand());
        Objects.requireNonNull(getCommand("class")).setTabCompleter(new PlayerClassCommand());
        Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCommand());
        Objects.requireNonNull(getCommand("quest")).setTabCompleter(new QuestCommand());
        getServer().getPluginManager().registerEvents(new ReinforceInvManager(), this);
        getServer().getPluginManager().registerEvents(new ShopInvManager(), this);
        getServer().getPluginManager().registerEvents(new QuestListener(), this);
        getServer().getPluginManager().registerEvents(new Rogue(), this);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::Save, 20L * 30, 20L * 30);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "RPGPlugin 활성화 !!");  //서버의 로그에 출력
    }

    @Override
    public void onDisable() {   //플러그인 비활성화시 실행
        Save();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "RPGPlugin 비활성화 !!");  //서버의 로그에 출력
    }

    public void Save() { // 스텟
        PlayerClassManager.save();
        QuestManager.save();

        SerializeManager.saveFile();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Successfully saved !");
    }
}

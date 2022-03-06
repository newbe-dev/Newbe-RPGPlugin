package com.example.RPGPlugin;

import com.example.RPGPlugin.PlayerClass.PlayerClassCommand;
import com.example.RPGPlugin.PlayerClass.PlayerClassInvManager;
import com.example.RPGPlugin.PlayerClass.SkillInvManager;
import com.example.RPGPlugin.PlayerClasses.*;
import com.example.RPGPlugin.Quest.QuestCommand;
import com.example.RPGPlugin.Quest.QuestInvManager;
import com.example.RPGPlugin.Quest.QuestListener;
import com.example.RPGPlugin.Quest.QuestManager;
import com.example.RPGPlugin.Reinforce.ReinforceCommand;
import com.example.RPGPlugin.Reinforce.ReinforceInvManager;
import com.example.RPGPlugin.Shop.ShopCommand;
import com.example.RPGPlugin.Shop.ShopInvManager;
import com.example.RPGPlugin.Stat.StatCommand;
import com.example.RPGPlugin.Stat.StatInvManager;
import com.example.RPGPlugin.Stat.StatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RPGPlugin extends JavaPlugin {

    @Override
    public void onEnable() {    //플러그인 활성화시 실행
        SerializeManager.loadFile();
        QuestManager.load();

        Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand());
        Objects.requireNonNull(getCommand("shop")).setTabCompleter(new ShopCommand());

        Objects.requireNonNull(getCommand("reinforce")).setExecutor(new ReinforceCommand());
        Objects.requireNonNull(getCommand("reinforce")).setTabCompleter(new ReinforceCommand());

        Objects.requireNonNull(getCommand("class")).setExecutor(new PlayerClassCommand());
        Objects.requireNonNull(getCommand("class")).setTabCompleter(new PlayerClassCommand());

        Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCommand());
        Objects.requireNonNull(getCommand("quest")).setTabCompleter(new QuestCommand());

        Objects.requireNonNull(getCommand("stat")).setExecutor(new StatCommand());

        getServer().getPluginManager().registerEvents(new ReinforceInvManager(), this);
        getServer().getPluginManager().registerEvents(new ShopInvManager(), this);
        getServer().getPluginManager().registerEvents(new QuestListener(), this);
        getServer().getPluginManager().registerEvents(new QuestInvManager(), this);
        getServer().getPluginManager().registerEvents(new StatListener(), this);
        getServer().getPluginManager().registerEvents(new StatInvManager(), this);
        getServer().getPluginManager().registerEvents(new PlayerClassInvManager(), this);
        getServer().getPluginManager().registerEvents(new SkillInvManager(), this);

        getServer().getPluginManager().registerEvents(new Knight(), this);
        getServer().getPluginManager().registerEvents(new Mage(), this);
        getServer().getPluginManager().registerEvents(new Mercenary(), this);
        getServer().getPluginManager().registerEvents(new Priest(), this);
        getServer().getPluginManager().registerEvents(new Warrior(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::save, 20L * 10, 20L * 10);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "RPGPlugin 활성화 !!");  //서버의 로그에 출력
    }

    @Override
    public void onDisable() {   //플러그인 비활성화시 실행
        save();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "RPGPlugin 비활성화 !!");  //서버의 로그에 출력
    }

    public void save() { // 스텟
        QuestManager.save();

        SerializeManager.saveFile();
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "--save");
    }
}

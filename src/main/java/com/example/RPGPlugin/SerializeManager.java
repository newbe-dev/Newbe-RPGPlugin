package com.example.RPGPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class SerializeManager {
    public static YamlConfiguration yml = new YamlConfiguration();
    private static Plugin plugin;
    public static File configFile;

    public static Plugin getPlugin() {
        if(plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugin("RPGPlugin");
        }
        return plugin;
    }

    public static void saveFile() {
        try {
            yml.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFile() {
        if(plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugin("RPGPlugin");
        }
        if (!plugin.getDataFolder().exists()) { // 폴더가 존재하지 않는다면
            try {
                if(plugin.getDataFolder().mkdirs()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Plugin 폴더 생성 중 ...");
                }else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Plugin 폴더 생성 중 문제가 발생하였습니다!");
                }
            } catch (SecurityException e) { // throw
                e.printStackTrace();
            }
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) { // 폴더에 config.yml 파일이 존재하지 않는다면
            try {
                if(configFile.createNewFile()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "config.yml 파일 생성 중 ...");
                }else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "config.yml 파일 생성 중 문제가 발생하였습니다!");
                }
            } catch (IOException e) { // throw
                e.printStackTrace();
            }
        }

        try {
            yml.load(configFile); // 불러오기
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "config.yml 파일을 읽어오던 중 문제가 발생하였습니다!");
            e.printStackTrace();
        }
    }
}

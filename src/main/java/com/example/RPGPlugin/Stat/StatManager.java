package com.example.RPGPlugin.Stat;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class StatManager {
    // Plugin.Stat.%s
    // .level
    // .exp
    // .attackPower
    // .defense
    // .health
    // .skillPower

    public enum STAT {
        attackPower,
        skillPower,
        defense,
        health
    }

    public static void addExp(UUID uuid, double experience) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Stat.%s", uuid));
        double currentExp;
        int level;

        if (node == null) node = SerializeManager.yml.createSection(String.format("Plugin.Stat.%s", uuid));
        currentExp = node.getDouble("exp", 0);
        level = node.getInt("level");

        currentExp += experience;

        double needExpForNextLevel = 100 * Math.pow(level, 2) + 200 * level + 50;
        if (currentExp >= needExpForNextLevel) {

            currentExp -= needExpForNextLevel;
            level += 1;

            int point = node.getInt("point");
            point += 3;
            node.set("point", point);
            Bukkit.getPlayer(uuid).setLevel(level);

            Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid), Sound.ENTITY_PLAYER_LEVELUP, 50, 5);
            Bukkit.getPlayer(uuid).sendMessage(String.format("%s%s 레벨 업 !", ChatColor.BOLD, ChatColor.GREEN));
        }
        node.set("exp", currentExp);
        node.set("level", level);
        Bukkit.getPlayer(uuid).setExp((float) (currentExp / ((100 * Math.pow(level, 2) + 200 * level) + 50)));
    }

    public static void useStatPoint(UUID uuid, STAT statType, int amount) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Stat.%s", uuid));

        if (node == null) {
            node = SerializeManager.yml.createSection(String.format("Plugin.Stat.%s", uuid));
        }

        int point = node.getInt("point");
        point -= amount;
        if (point < 0) {
            return;
        }
        node.set("point", point);

        int currentStat = node.getInt(statType.name());
        currentStat += amount;
        node.set(statType.name(), currentStat);

        switch (statType) {
            case attackPower:
                Bukkit.getPlayer(uuid).sendMessage(String.format("%s%s 공격력 +%d", ChatColor.BOLD, ChatColor.RED, amount));
                break;
            case skillPower:
                Bukkit.getPlayer(uuid).sendMessage(String.format("%s%s 스킬 피해 +%d", ChatColor.BOLD, ChatColor.AQUA, amount));
                break;
            case defense:
                Bukkit.getPlayer(uuid).sendMessage(String.format("%s%s 방어력 +%d", ChatColor.BOLD, ChatColor.GRAY, amount));
                break;
            case health:
                Bukkit.getPlayer(uuid).sendMessage(String.format("%s%s 체력 +%d", ChatColor.BOLD, ChatColor.GREEN, amount));
                break;
        }
        Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid), Sound.BLOCK_NOTE_BLOCK_PLING, 50, 5);
    }
}

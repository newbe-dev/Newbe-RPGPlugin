package com.example.RPGPlugin.Stat;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
        Player player = Bukkit.getPlayer(uuid);

        if (node == null) node = SerializeManager.yml.createSection(String.format("Plugin.Stat.%s", uuid));
        currentExp = node.getDouble("exp", 0);
        level = node.getInt("level");

        currentExp += experience;

        double needExpForNextLevel = 100 * Math.pow(level, 2) + 200 * level + 50;
        while (currentExp >= needExpForNextLevel) {
            currentExp -= needExpForNextLevel;
            level += 1;

            int point = node.getInt("point");
            point += 3;
            node.set("point", point);
            player.setLevel(level);

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 50, 5);
            player.sendMessage(String.format("%s%s 레벨 업 !", ChatColor.BOLD, ChatColor.GREEN));
            needExpForNextLevel = 100 * Math.pow(level, 2) + 200 * level + 50;
        }

        node.set("exp", currentExp);
        node.set("level", level);
        player.setExp((float) (currentExp / ((100 * Math.pow(level, 2) + 200 * level) + 50)));
    }

    public static void useStatPoint(UUID uuid, STAT statType, int amount) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Stat.%s", uuid));
        Player player = Bukkit.getPlayer(uuid);

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
                player.sendMessage(String.format("%s%s 공격력 +%d", ChatColor.BOLD, ChatColor.RED, amount));
                break;
            case skillPower:
                player.sendMessage(String.format("%s%s 스킬 피해 +%d", ChatColor.BOLD, ChatColor.AQUA, amount));
                break;
            case defense:
                player.sendMessage(String.format("%s%s 방어력 +%d", ChatColor.BOLD, ChatColor.GRAY, amount));
                break;
            case health:
                player.sendMessage(String.format("%s%s 체력 +%d", ChatColor.BOLD, ChatColor.GREEN, amount));
                break;
        }

        if (statType == STAT.health) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            double health = 20 + currentStat * 1.333;
            attribute.setBaseValue(health);
        }
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 50, 5);
    }

    public static void removeStatPoint(UUID uuid, STAT statType, int amount) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Stat.%s", uuid));
        Player player = Bukkit.getPlayer(uuid);
        if (node == null) {
            node = SerializeManager.yml.createSection(String.format("Plugin.Stat.%s", uuid));
        }

        int currentStat = node.getInt(statType.name());
        currentStat -= amount;
        if (currentStat < 0) {
            return;
        }
        node.set(statType.name(), currentStat);

        int point = node.getInt("point");
        point += amount;
        node.set("point", point);

        if (statType == STAT.health) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            double health = 20 + currentStat * 1.333;
            attribute.setBaseValue(health);
        }
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 50, 2);
    }
}

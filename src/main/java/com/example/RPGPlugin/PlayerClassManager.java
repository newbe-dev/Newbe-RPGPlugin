package com.example.RPGPlugin;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PlayerClassManager {
    private static Map<UUID, PlayerClass> playerClassMap;

    public static void setPlayerClass(Player player, String newPlayerClass) {
        if (playerClassMap.containsKey(player.getUniqueId())) {
            Bukkit.getLogger().info(String.format("%s has already had a class !", player.getDisplayName()));
            return;
        }

        PlayerClass playerClass;
        try {
            playerClass = PlayerClass.valueOf(newPlayerClass);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
        playerClassMap.put(player.getUniqueId(), playerClass);
    }

    public static void removePlayerClass(Player player) {
        playerClassMap.remove(player.getUniqueId());
    }

    public static PlayerClass getPlayerClass(Player player) {
        if (!playerClassMap.containsKey(player.getUniqueId())) return PlayerClass.NONE;
        return playerClassMap.get(player.getUniqueId());
    }

    public static Map<UUID, PlayerClass> getPlayerClassMap() {
        return playerClassMap;
    }

    public static void setPlayerClassMap(Map<UUID, PlayerClass> newMap) {
        playerClassMap = newMap;
    }
}

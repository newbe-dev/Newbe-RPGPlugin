package com.example.RPGPlugin.PlayerClass;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerClassManager {
    private static Map<UUID, PlayerClass> playerClassMap = new HashMap<>();

    public static void setPlayerClass(Player player, String newPlayerClass) {
        if (playerClassMap.containsKey(player.getUniqueId())) {
            player.sendMessage(String.format("%s%s(!) %s이미 직업이 있습니다 !", ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE));
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
        switch (playerClass) {
            case NONE:
                break;
            case KNIGHT:
                player.sendMessage(String.format("%s[기사]%s 를 선택하셨습니다 !", ChatColor.DARK_GRAY + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
            case MAGE:
                player.sendMessage(String.format("%s[마법사]%s 를 선택하셨습니다 !", ChatColor.DARK_BLUE + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
            case WARRIOR:
                player.sendMessage(String.format("%s[전사]%s 를 선택하셨습니다 !", ChatColor.DARK_RED + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
            case ROGUE:
                player.sendMessage(String.format("%s[도적]%s 을 선택하셨습니다 !", ChatColor.GREEN + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
            case ALCHEMIST:
                player.sendMessage(String.format("%s[연금술사]%s 를 선택하셨습니다 !", ChatColor.DARK_GREEN + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
            case PRIEST:
                player.sendMessage(String.format("%s[성직자]%s 를 선택하셨습니다 !", ChatColor.YELLOW + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
            case MERCENARY:
                player.sendMessage(String.format("%s[용병]%s 을 선택하셨습니다 !", ChatColor.GOLD + ChatColor.BOLD.toString(), ChatColor.RESET));
                break;
        }
    }

    public static void removePlayerClass(Player player) {
        player.sendMessage(String.format("%s직업을 초기화하셨습니다 !", ChatColor.YELLOW + ChatColor.BOLD.toString()));
        playerClassMap.remove(player.getUniqueId());
    }

    public static PlayerClass getPlayerClass(Player player) {
        if (!playerClassMap.containsKey(player.getUniqueId())) return PlayerClass.NONE;
        return playerClassMap.get(player.getUniqueId());
    }

    public static Map<UUID, PlayerClass> getPlayerClassMap() {
        return playerClassMap;
    }

    public static void setPlayerClassMap(Map<String, String> map) {
        Map<UUID, PlayerClass> newMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            PlayerClass playerClass = PlayerClass.NONE;
            try {
                PlayerClass.valueOf(entry.getValue());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                continue;
            }
            newMap.put(UUID.fromString(entry.getKey()), playerClass);
        }
        playerClassMap = newMap;
    }

    public static void save() {
        List<Map<String, String>> mapList = Arrays.asList(convertStringMap(PlayerClassManager.getPlayerClassMap())); // 플레이어 직업
        SerializeManager.yml.set("Plugin.playerClass", mapList);
    }

    public static void load() {
        if (SerializeManager.yml.contains("Plugin.playerClass")) { // 플레이어 직업
            List<Map<?, ?>> mapList = SerializeManager.yml.getMapList("Plugin.playerClass");
            PlayerClassManager.setPlayerClassMap((Map<String, String>) mapList.get(0));
        }
    }

    public static Map<String, String> convertStringMap(Map<UUID, PlayerClass> map) {
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<UUID, PlayerClass> entry : map.entrySet()) {
            newMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return newMap;
    }
}

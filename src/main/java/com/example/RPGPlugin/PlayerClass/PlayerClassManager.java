package com.example.RPGPlugin.PlayerClass;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerClassManager {

    public static void setPlayerClass(Player player, String newPlayerClass) {
        String path = String.format("Plugin.PlayerClass.%s", player.getUniqueId());
        if (SerializeManager.yml.get(path) != null) {
            player.sendMessage(String.format("%s%s(!) %s이미 직업이 있습니다 !", ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE));
            return;
        }

        PlayerClass playerClass;
        try {
            playerClass = PlayerClass.valueOf(newPlayerClass);
        } catch (IllegalArgumentException e) {
            player.sendMessage(String.format("%s%s(!) %s존재하지 않는 직업입니다 !", ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE));
            return;
        }
        SerializeManager.yml.set(path, newPlayerClass);

        switch (playerClass) {
            case NONE:
                break;
            case KNIGHT:
                player.sendMessage(String.format("%s[기사]%s 를 선택하셨습니다 !", ChatColor.DARK_GRAY + ChatColor.BOLD.toString(), ChatColor.WHITE));
                break;
            case MAGE:
                player.sendMessage(String.format("%s[마법사]%s 를 선택하셨습니다 !", ChatColor.AQUA + ChatColor.BOLD.toString(), ChatColor.WHITE));
                break;
            case WARRIOR:
                player.sendMessage(String.format("%s[전사]%s 를 선택하셨습니다 !", ChatColor.RED + ChatColor.BOLD.toString(), ChatColor.WHITE));
                break;
            case PRIEST:
                player.sendMessage(String.format("%s[성직자]%s 를 선택하셨습니다 !", ChatColor.YELLOW + ChatColor.BOLD.toString(), ChatColor.WHITE));
                break;
            case MERCENARY:
                player.sendMessage(String.format("%s[용병]%s 을 선택하셨습니다 !", ChatColor.GOLD + ChatColor.BOLD.toString(), ChatColor.WHITE));
                break;
            case ARCHER:
                player.sendMessage(String.format("%s[궁수]%s 를 선택하셨습니다 !", ChatColor.GREEN + ChatColor.BOLD.toString(), ChatColor.WHITE));
                break;
        }
    }

    public static void removePlayerClass(Player player) {
        String path = String.format("Plugin.PlayerClass.%s", player.getUniqueId());
        player.sendMessage(String.format("%s직업을 초기화하셨습니다 !", ChatColor.YELLOW + ChatColor.BOLD.toString()));
        SerializeManager.yml.set(path, null);
    }

    public static PlayerClass getPlayerClass(Player player) {
        PlayerClass playerClass;
        try {
            playerClass = PlayerClass.valueOf(SerializeManager.yml.getString(String.format("Plugin.PlayerClass.%s", player.getUniqueId())));
        } catch (Exception e) {
            return PlayerClass.NONE;
        }
        return playerClass;
    }

}

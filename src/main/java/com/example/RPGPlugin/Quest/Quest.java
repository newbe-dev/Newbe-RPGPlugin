package com.example.RPGPlugin.Quest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Quest {
    public enum QuestType {
        HUNT,
        COLLECT,
        DELIVERY,
        DESTROY,
        LOCATION
    }

    public QuestType questType;
    public int questId;
    public int goal = 0;
    public int progress = 0;
    public String questName;
    public Boolean isMainQuest;

    public String targetEntity;
    public String targetMaterial;
    public Location targetLocation;
    public float radius;

    public List<ItemStack> rewardItemList;
    public int rewardQuest;
    public Location rewardLocation;
    public String rewardCommand;

    public ItemStack conditionItem;
    public int conditionQuest;

    public void progress(Player player, int num) {
        progress += num;
    }

    private Boolean Complete(Player player) {
        if(progress < goal) {
            return false;
        }
        if(rewardItemList != null) {
            int empty = 0;
            for(int i = 0; i < player.getInventory().getSize(); i++) {
                if(player.getInventory().getItem(i) == null) {
                    empty += 1;
                }
            }
            if(empty < rewardItemList.size()) {
                player.sendMessage(String.format("%s%s(!) %s공간이 충분하지 않습니다!", ChatColor.RED, ChatColor.BOLD, ChatColor.DARK_GRAY));
                player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 5, 1);
                return false;
            }
            for(ItemStack itemStack : rewardItemList) {
                player.getInventory().addItem(itemStack);
            }
        }
        player.sendMessage(String.format("%s[ %s ]%s 퀘스트를 완료하셨습니다!", ChatColor.BOLD.toString() + (isMainQuest ? ChatColor.GOLD : ChatColor.GRAY), questName, ChatColor.RESET));
        return true;
    }
}

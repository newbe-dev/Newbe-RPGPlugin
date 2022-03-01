package com.example.RPGPlugin.Quest;

import org.bukkit.Location;
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
    public int goal = -1;
    public int progress = -1;
    public String questName;
    public String questDescription;
    public Boolean isMainQuest;

    public String targetEntity;
    public String targetItemStack;
    public Location targetLocation;
    public float radius;

    public List<ItemStack> rewardItemList;
    public int rewardQuest = -1;
    public Location rewardLocation;
    public String rewardCommand;

    public List<ItemStack> conditionItemList;
    public int conditionQuest = -1;
    public int conditionLevel = -1;

    public Boolean progress(int num) {
        if(progress >= goal) return false;
        progress += num;
        return progress >= goal;
    }

    public Boolean complete() {
        return progress >= goal;
    }
}

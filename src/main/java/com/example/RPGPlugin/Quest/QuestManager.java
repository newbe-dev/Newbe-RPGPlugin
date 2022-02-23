package com.example.RPGPlugin.Quest;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class QuestManager { // Quest.숫자 -> 퀘스트 정보, Quest.uuid -> 플레이어 퀘스트 정보 List<String>

    private static final Map<UUID, Set<Quest>> PlayerQuest = new HashMap<>();

    public static Boolean setNpcQuest(UUID uuid, int questId) {
        if (LoadQuest(questId) == null) return false;
        List<Integer> questList = SerializeManager.yml.getIntegerList(String.format("Plugin.Quest.%s", uuid.toString()));
        if(questList.contains(questId)) return false;
        questList.add(questId);
        SerializeManager.yml.set(String.format("Plugin.Quest.%s", uuid), questList);
        return true;
    }

    public static Boolean removeNpcQuest(UUID uuid, int questId) {
        if (LoadQuest(questId) == null) return false;
        List<Integer> questList = SerializeManager.yml.getIntegerList(String.format("Plugin.Quest.%s", uuid.toString()));
        if(!questList.contains(questId)) return false;
        questList.remove(questId);
        SerializeManager.yml.set(String.format("Plugin.Quest.%s", uuid), questList);
        return true;
    }

    public static List<Integer> getNpcQuest(UUID uuid) {
        return SerializeManager.yml.getIntegerList(String.format("Plugin.Quest.%s", uuid.toString()));
    }

    public static Map<UUID, Set<Quest>> getPlayerQuestMap() {
        return PlayerQuest;
    }

    public static Quest LoadQuest(int questId) {
        if (!SerializeManager.yml.contains(String.format("Plugin.Quest.%d", questId))) return null;
        Quest quest = new Quest();
        ConfigurationSection section = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Quest.%d", questId));
        if (section == null) {
            return null;
        }
        quest.questName = section.getString("questName");
        quest.questType = Quest.QuestType.valueOf(section.getString("questType"));
        quest.goal = section.getInt("goal");
        quest.questId = questId;

        if (section.contains("conditionItem")) {
            quest.conditionItem = section.getItemStack("conditionItem");
        }
        if (section.contains("conditionQuest")) {
            quest.conditionQuest = section.getInt("conditionQuest");
        }
        if (section.contains("rewardItems")) {
            quest.rewardItemList = (List<ItemStack>) section.getList("rewardItems");
        }
        if (section.contains("rewardQuest")) {
            quest.rewardQuest = section.getInt("rewardQuest");
        }
        if (section.contains("rewardLocation")) {
            quest.rewardLocation = section.getLocation("rewardLocation");
        }
        if (section.contains("rewardCommand")) {
            quest.rewardCommand = section.getString("rewardCommand");
        }
        switch (quest.questType) {
            case HUNT:
                quest.targetEntity = section.getString("targetEntity");
                break;
            case COLLECT:
            case DESTROY:
                quest.targetMaterial = section.getString("targetMaterial");
                break;
            case DELIVERY:
                quest.targetEntity = section.getString("targetEntity");
                quest.targetMaterial = section.getString("targetMaterial");
                break;
            case LOCATION:
                quest.targetLocation = section.getLocation("targetLocation");
                quest.radius = section.getInt("radius");
                break;
        }
        return quest;
    }

    public static void save() {
        for (Map.Entry<UUID, Set<Quest>> entry : QuestManager.getPlayerQuestMap().entrySet()) { // 플레이어 퀘스트
            List<String> stringList = SerializeManager.yml.getStringList(String.format("Plugin.Quest.Player.%s", entry.getKey()));
            for(Quest q : entry.getValue()) {
                stringList.add(String.format("%d:%d", q.questId, q.progress));
            }
            Set<String> stringSet = new HashSet<>(stringList); // 중복 제거
            List<String> newList = new ArrayList<>(stringSet); // 중복 제거
            SerializeManager.yml.set(String.format("Plugin.Quest.Player.%s", entry.getKey()), newList);
        }
    }
}

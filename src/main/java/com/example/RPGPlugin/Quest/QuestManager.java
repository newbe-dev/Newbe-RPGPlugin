package com.example.RPGPlugin.Quest;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class QuestManager { // Quest.숫자 -> 퀘스트 정보, Quest.uuid -> 플레이어 퀘스트 정보 List<String>

    private static final Map<UUID, Set<Quest>> PLAYER_QUEST = new HashMap<>();

    public static Boolean setNpcQuest(UUID uuid, int questId) {
        String path = String.format("Plugin.Quest.%s", uuid);

        if(Bukkit.getEntity(uuid) instanceof Player) return false;
        if (loadQuest(questId) == null) return false;
        if (SerializeManager.yml.getConfigurationSection(path) == null) SerializeManager.yml.createSection(path);

        List<Integer> questList = SerializeManager.yml.getIntegerList(path);
        if (questList.contains(questId)) return false;

        questList.add(questId);
        SerializeManager.yml.set(path, questList);
        return true;
    }

    public static Boolean removeNpcQuest(UUID uuid, int questId) {
        String path = String.format("Plugin.Quest.%s", uuid);

        if(Bukkit.getEntity(uuid) instanceof Player) return false;
        if (loadQuest(questId) == null) return false;
        if (SerializeManager.yml.getConfigurationSection(path) == null) SerializeManager.yml.createSection(path);

        List<Integer> questList = SerializeManager.yml.getIntegerList(path);
        if (!questList.contains(questId)) return false;

        questList.remove(questId);
        SerializeManager.yml.set(path, questList);
        return true;
    }

    public static List<Integer> getNpcQuest(UUID uuid) {
        String path = String.format("Plugin.Quest.%s", uuid);
        return SerializeManager.yml.getIntegerList(path);
    }

    public static void setPlayerQuest(UUID uuid, Quest quest) {
        Set<Quest> questSet = PLAYER_QUEST.get(uuid);
        if(questSet == null) questSet = new HashSet<>();
        questSet.add(quest);
        PLAYER_QUEST.put(uuid, questSet);
    }

    public static void removePlayerQuest(UUID uuid, int questId) {
        Set<Quest> questSet = PLAYER_QUEST.get(uuid);
        questSet.removeIf(q -> q.questId == questId);
        PLAYER_QUEST.put(uuid, questSet);

        Map<String, Object> map = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Quest.%s", questId)).getValues(false);
        map.remove(String.valueOf(questId));
        SerializeManager.yml.createSection(String.format("Plugin.Quest.%s", uuid), map);
    }

    public static Map<UUID, Set<Quest>> getPlayerQuestMap() {
        return PLAYER_QUEST;
    }

    @Nullable
    public static Quest loadQuest(int questId) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Quest.%d", questId));
        if (node == null) return null;

        Quest quest = new Quest(); // 반환할 퀘스트 객체

        quest.questName = node.getString("questName");
        quest.questDescription = node.getString("questDescription");
        quest.questType = Quest.QuestType.valueOf(node.getString("questType"));
        quest.isMainQuest = node.getBoolean("isMainQuest");
        quest.goal = node.getInt("goal");
        quest.questId = questId;

        if (node.contains("conditionItemList")) {
            quest.conditionItemList = (List<ItemStack>) node.getList("conditionItemList");
        }
        if (node.contains("conditionQuest")) {
            quest.conditionQuest = node.getInt("conditionQuest", -1);
        }
        if (node.contains("conditionLevel")) {
            quest.conditionLevel = node.getInt("conditionLevel", -1);
        }
        if (node.contains("rewardItemList")) {
            quest.rewardItemList = (List<ItemStack>) node.getList("rewardItemList");
        }
        if (node.contains("rewardQuest")) {
            quest.rewardQuest = node.getInt("rewardQuest", -1);
        }
        if (node.contains("rewardLocation")) {
            quest.rewardLocation = node.getLocation("rewardLocation");
        }
        if (node.contains("rewardCommand")) {
            quest.rewardCommand = node.getString("rewardCommand");
        }

        switch (quest.questType) {
            case HUNT:
                quest.targetEntity = node.getString("targetEntity");
                break;
            case COLLECT:
                break;
            case LOCATION:
                quest.targetLocation = node.getLocation("targetLocation");
                quest.radius = node.getInt("radius");
                break;
        }
        return quest;
    }

    public static void save() {
        for (Map.Entry<UUID, Set<Quest>> entry : QuestManager.getPlayerQuestMap().entrySet()) { // 플레이어 퀘스트
            for (Quest q : entry.getValue()) {
                SerializeManager.yml.set(String.format("Plugin.Quest.%s.%d", entry.getKey().toString(), q.questId), q.progress);
            }
        }
    }

    public static void load() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<Quest> questSet = loadPlayerQuest(player.getUniqueId());
            if (questSet == null) return;

            QuestManager.getPlayerQuestMap().put(player.getUniqueId(), questSet);
            Bukkit.getConsoleSender().sendMessage(String.format("%s%s 님의 퀘스트를 성공적으로 불러왔습니다 !", ChatColor.BOLD, player.getDisplayName()));
        }
    }

    public static Set<Quest> loadPlayerQuest(UUID uuid) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Quest.%s", uuid));
        if (node == null)
            return null;
        if (!(Bukkit.getEntity(uuid) instanceof Player))
            return null;

        Set<Quest> questSet = new HashSet<>();
        Bukkit.getConsoleSender().sendMessage(String.valueOf(node.getValues(false)));
        for (String string : node.getKeys(false)) {
            int questId;
            try {
                questId = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                continue;
            }
            int progress = node.getInt(string);

            Quest quest = QuestManager.loadQuest(questId);

            quest.progress = progress;
            questSet.add(quest);
        }
        return questSet;
    }

    public static int getPlayerProgress(UUID uuid, int questId) {
        Set<Quest> questSet = PLAYER_QUEST.get(uuid);
        for (Quest q : questSet) {
            if (q.questId == questId) {
                return q.progress;
            }
        }
        return -1;
    }
}

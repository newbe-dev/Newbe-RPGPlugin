package com.example.RPGPlugin.Quest;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class QuestListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!SerializeManager.yml.contains(String.format("Plugin.Quest.Player.%s", e.getPlayer().getUniqueId())))
            return;
        List<String> list = SerializeManager.yml.getStringList(String.format("Plugin.Quest.Player.%s", e.getPlayer().getUniqueId()));
        Set<Quest> questSet = new HashSet<>();
        for (String string : list) {
            int questId = Integer.parseInt(string.split(":")[0]);
            int progress = Integer.parseInt(string.split(":")[1]);
            Quest quest = QuestManager.LoadQuest(questId);
            if(quest == null) return;
            quest.progress = progress;
            questSet.add(quest);
        }
        QuestManager.getPlayerQuestMap().put(e.getPlayer().getUniqueId(), questSet);
        Bukkit.getConsoleSender().sendMessage(String.format("%s%s 님의 퀘스트를 성공적으로 불러왔습니다 !", ChatColor.BOLD, e.getPlayer().getDisplayName()));
    }

    @EventHandler
    public void onEntityDeath(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            for (Quest q : QuestManager.getPlayerQuestMap().get(e.getDamager().getUniqueId())) {
                if (q.questType.equals(Quest.QuestType.HUNT)) {
                    if (q.targetEntity.equals(e.getEntityType().toString())) {
                        q.progress((Player) e.getDamager(), 1);
                    }
                }
            }
        }
    }
}

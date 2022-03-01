package com.example.RPGPlugin.Quest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class QuestListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Set<Quest> questSet = QuestManager.LoadPlayerQuest(e.getPlayer().getUniqueId());
        if (questSet == null) return;

        QuestManager.getPlayerQuestMap().put(e.getPlayer().getUniqueId(), questSet);
        Bukkit.getConsoleSender().sendMessage(String.format("%s%s 님의 퀘스트를 성공적으로 불러왔습니다 !", ChatColor.BOLD, e.getPlayer().getDisplayName()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player player = e.getEntity().getKiller();
        if(player == null) return;
        player.sendMessage(e.getEntityType().toString());
        if (QuestManager.getPlayerQuestMap() == null) return;
        if (QuestManager.getPlayerQuestMap().get(player.getUniqueId()) == null || QuestManager.getPlayerQuestMap().get(player.getUniqueId()) == Collections.emptyMap())
            return;
        for (Quest q : QuestManager.getPlayerQuestMap().get(player.getUniqueId())) {
            if (q.questType.equals(Quest.QuestType.HUNT)) {
                if (q.targetEntity.equalsIgnoreCase(e.getEntityType().toString())) {
                    if(q.progress(1)) {
                        player.sendMessage(String.format("%s%s%s 퀘스트의 조건을 만족하셨습니다!",ChatColor.BOLD, ChatColor.DARK_GREEN, q.questName));
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 50, 1);
                    }else {
                        player.sendMessage(String.format("%s%s%s ( %d / %d )", ChatColor.BOLD, ChatColor.GRAY, q.questName, q.progress, q.goal));
                    }
                }
            }
        }

    }
}

package com.example.RPGPlugin;

import com.example.RPGPlugin.Quest.Quest;
import com.example.RPGPlugin.Quest.QuestManager;
import com.example.RPGPlugin.Stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyScoreBoardManager {

    private final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Scoreboard board = manager.getNewScoreboard();
    private final Objective objective = board.registerNewObjective("Information", "dummy", String.format("%s%sMandoo", ChatColor.BOLD, ChatColor.GOLD));

    public MyScoreBoardManager() {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SerializeManager.getPlugin(), () -> {
            List<Player> playerList = new ArrayList(Bukkit.getOnlinePlayers());
            for (Player player : playerList) {
                reloadBoard(player, objective);
            }
        }, 1L, 10L);
    }

    public void reloadBoard(Player player, Objective objective) {
        //board.clearSlot(DisplaySlot.SIDEBAR);
        setScore(objective, String.format("%s%s[퀘스트]", ChatColor.BOLD, ChatColor.WHITE), 50);
        setScore(objective, "  ", 44);
        Set<Quest> questSet = QuestManager.loadPlayerQuest(player.getUniqueId());
        if (questSet != null) {
            int i = 0;
            for (Quest quest : questSet) {
                i++;
                setScore(objective, String.format("%s%s%s ( %d / %d )", ChatColor.BOLD, quest.isMainQuest ? ChatColor.BOLD : ChatColor.WHITE, quest.questName, quest.progress, quest.goal), 98 - i * 2);
            }
        }
        setScore(objective, "   ", 16);
        setScore(objective, String.format("%s%s[스탯]", ChatColor.BOLD, ChatColor.WHITE), 10);
        setScore(objective, "     ", 8);
        setScore(objective, String.format("%s%s공격력: %d", ChatColor.BOLD, ChatColor.RED, StatManager.getFinalStat(player, StatManager.STAT.attackPower)), 6);
        setScore(objective, String.format("%s%s주문력: %d", ChatColor.BOLD, ChatColor.AQUA, StatManager.getFinalStat(player, StatManager.STAT.skillPower)), 4);
        setScore(objective, String.format("%s%s방어력: %d", ChatColor.BOLD, ChatColor.GRAY, StatManager.getFinalStat(player, StatManager.STAT.defense)), 2);
        setScore(objective, String.format("%s%s체력: %d", ChatColor.BOLD, ChatColor.GREEN, StatManager.getFinalStat(player, StatManager.STAT.health)), 0);
        player.setScoreboard(board);
    }

    private void setScore(Objective objective, String entry, int option) {
        Score score = objective.getScore(entry);
        score.setScore(option);
    }
}

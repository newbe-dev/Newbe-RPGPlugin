package com.example.RPGPlugin.Quest;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.Arrays;
import java.util.List;

public class QuestCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {  //명령어 사용자가 플레이어인 경우
            Player player = (Player) sender;
            if (args.length == 0) {
                OpenQuestInventoryEvent event = new OpenQuestInventoryEvent(player, player);
                Bukkit.getPluginManager().callEvent(event);
            }
            else if (args.length == 1) {
                if (args[0].equals("quests")) {
                    List<Entity> entityList = player.getNearbyEntities(3, 3, 3);
                    entityList.removeIf(e -> !(e instanceof Villager));
                    if (entityList.size() == 0) {
                        player.sendMessage("반경 내에 주민이 존재하지 않습니다 !");
                    } else {
                        LivingEntity entity = (LivingEntity) entityList.get(0);
                        player.sendMessage(QuestManager.getNpcQuest(entity.getUniqueId()).toString());
                    }
                    return true;
                }
            } else if (args.length == 2) {
                switch (args[0]) {
                    case "assign": {
                        try {
                            Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        List<Entity> entityList = player.getNearbyEntities(3, 3, 3);
                        entityList.removeIf(e -> !(e instanceof Villager));
                        if (entityList.size() == 0) {
                            player.sendMessage("반경 내에 주민이 존재하지 않습니다 !");
                        } else {
                            LivingEntity entity = (LivingEntity) entityList.get(0);
                            if (QuestManager.setNpcQuest(entity.getUniqueId(), Integer.parseInt(args[1])))
                                player.sendMessage(String.format("성공적으로 퀘스트가 설정되었습니다 ! ( questId: %d )", Integer.parseInt(args[1])));
                            else
                                player.sendMessage(String.format("예기치 못한 오류가 발생했습니다 ! ( questId: %d )", Integer.parseInt(args[1])));
                        }
                        return true;
                    }
                    case "remove": {
                        try {
                            Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        List<Entity> entityList = player.getNearbyEntities(3, 3, 3);
                        entityList.removeIf(e -> !(e instanceof Villager));
                        if (entityList.size() == 0) {
                            player.sendMessage("반경 내에 주민이 존재하지 않습니다 !");
                        } else if (!QuestManager.getNpcQuest(entityList.get(0).getUniqueId()).contains(Integer.parseInt(args[1]))) {
                            player.sendMessage(String.format("대상 주민에게 퀘스트가 존재하지 않습니다 ! ( questId: %s )", args[1]));
                        } else {
                            LivingEntity entity = (LivingEntity) entityList.get(0);
                            if (QuestManager.removeNpcQuest(entity.getUniqueId(), Integer.parseInt(args[1])))
                                player.sendMessage(String.format("성공적으로 퀘스트가 삭제되었습니다 ! ( questId: %s )", args[1]));
                            else
                                player.sendMessage(String.format("예기치 못한 오류가 발생했습니다 ! ( questId: %s )", args[1]));
                        }
                        return true;
                    }
                    case "abandon":
                        try {
                            Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        QuestManager.removePlayerQuest(player.getUniqueId(), Integer.parseInt(args[1]));
                        player.sendMessage(String.format("퀘스트를 포기했습니다. ( questId: %s )", args[1]));
                        return true;
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {   //명령어 사용자가 콘솔인 경우
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;   //false값을 반환하면 명령어가 실패한 것으로 간주
        }
        return false;   //false값을 반환하면 명령어가 실패한 것으로 간주
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return null;
        if (command.getName().equals("q") ||command.getName().equals("quest") || command.getName().equals("퀘스트")) {
            if (args.length == 1) {
                return Arrays.asList("quests", "assign", "remove", "abandon");
            }
        }
        return null;
    }
}
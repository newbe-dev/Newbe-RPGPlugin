package com.example.RPGPlugin.Reinforce;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Arrays;
import java.util.List;

public class ReinforceCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {  //명령어 사용자가 플레이어인 경우
            Player player = (Player) sender;
            if (args.length == 0) {
                OpenReinforceInventoryEvent event = new OpenReinforceInventoryEvent((Player) sender);
                Bukkit.getPluginManager().callEvent(event);
                return true;
            } else if (args.length == 1) {
                if (args[0].equals("assign")) {
                    List<Entity> entityList = player.getNearbyEntities(3, 3, 3);
                    entityList.removeIf(e -> !(e instanceof Villager));
                    if (entityList.size() == 0) {
                        player.sendMessage("반경 내에 주민이 존재하지 않습니다 !");
                    } else {
                        Entity entity = entityList.get(0);
                        player.sendMessage(entity.getUniqueId());
                        SerializeManager.yml.set(String.format("Plugin.Reinforce.Villager.%s", entity.getUniqueId()), 1);
                        player.sendMessage(String.format("성공적으로 주민에게 상점 번호가 부여되었습니다 ! ( reinforceId: %d )", 1));
                        SerializeManager.saveFile();
                    }
                    return true;
                }
            }

        } else if (sender instanceof ConsoleCommandSender) {   //명령어 사용자가 콘솔인 경우
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;   //false 값을 반환하면 명령어가 실패한 것으로 간주
        }
        return false;   //false 값을 반환하면 명령어가 실패한 것으로 간주
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return null;
        if (command.getName().equals("reinforce") || command.getName().equals("rein") || command.getName().equals("강화")) {
            if (args.length == 1) {
                return Arrays.asList("assign");
            }
        }
        return null;
    }
}

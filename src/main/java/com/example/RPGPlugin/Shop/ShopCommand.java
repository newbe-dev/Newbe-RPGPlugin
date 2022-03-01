package com.example.RPGPlugin.Shop;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.Arrays;
import java.util.List;

public class ShopCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {  //명령어 사용자가 플레이어인 경우
            Player player = (Player) sender;
            if (args.length == 0) return false;
            else if (args.length == 1) {
                int shopNo;
                try {
                    shopNo = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    return false;
                }
                OpenShopInventoryEvent event = new OpenShopInventoryEvent((Player) sender, shopNo);
                Bukkit.getPluginManager().callEvent(event);
                return true;
            } else if (args.length == 2) {
                if (args[0].equals("assign")) {
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
                        Entity entity = entityList.get(0);
                        player.sendMessage(entity.getUniqueId());
                        SerializeManager.yml.set(String.format("Plugin.Shop.Villager.%s", entity.getUniqueId()), Integer.parseInt(args[1]));
                        player.sendMessage(String.format("성공적으로 주민에게 상점 번호가 부여되었습니다 ! ( shopId: %d )", Integer.parseInt(args[1])));
                        SerializeManager.saveFile();
                    }
                    return true;
                } else if (args[0].equals("shops")) {
                    List<Entity> entityList = player.getNearbyEntities(3, 3, 3);
                    entityList.removeIf(e -> !(e instanceof Villager));
                    if (entityList.size() == 0) {
                        player.sendMessage("반경 내에 주민이 존재하지 않습니다 !");
                    } else {
                        LivingEntity entity = (LivingEntity) entityList.get(0);
                        player.sendMessage(String.valueOf(SerializeManager.yml.getInt(String.format("Plugin.Shop.Villager.%s", entity.getUniqueId()), -1)));
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
        if (command.getName().equals("shop") || command.getName().equals("상점")) {
            if (args.length == 1) {
                return Arrays.asList("assign", "shops");
            }
        }
        return null;
    }
}
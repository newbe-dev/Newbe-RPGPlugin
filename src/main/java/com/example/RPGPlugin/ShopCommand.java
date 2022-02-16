package com.example.RPGPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {  //명령어 사용자가 플레이어인 경우
            if (args.length == 0) return false;
            if (args.length == 1) {
                int shopNo;
                try {
                    shopNo = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    return false;
                }
                OpenShopInventoryEvent event = new OpenShopInventoryEvent((Player) sender, shopNo);
                Bukkit.getPluginManager().callEvent(event);
                return true;
            }
            if (args.length == 2 && args[0].equals("create") && sender.isOp()) {
                Player player = (Player) sender;
                List<Entity> entityList = player.getNearbyEntities(3, 3, 3);
                entityList.removeIf(e -> !(e instanceof Villager));
                if (entityList.size() == 0) {
                    player.sendMessage("반경 내에 주민이 존재하지 않습니다 !");
                } else {
                    LivingEntity entity = (LivingEntity) entityList.get(0);
                    PotionEffect potionEffect = new PotionEffect(PotionEffectType.GLOWING, 5, 2);
                    potionEffect.apply(entity);
                    for (String tag : entity.getScoreboardTags()) {
                        if (tag.startsWith("CustomShopNum")) {
                            entity.removeScoreboardTag(tag);
                        }
                    }
                    entity.addScoreboardTag("CustomShopNum:" + Integer.parseInt(args[1]));
                    player.sendMessage("주민에게 상점 번호가 등록되었습니다.");
                }
                return true;
            } else {
                return false;
            }
        } else if (sender instanceof ConsoleCommandSender) {   //명령어 사용자가 콘솔인 경우
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;   //false값을 반환하면 명령어가 실패한 것으로 간주
        }
        return false;   //false값을 반환하면 명령어가 실패한 것으로 간주
    }
}
package com.example.RPGPlugin.Stat;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {
            if(args.length == 0) {
                OpenStatInventoryEvent event = new OpenStatInventoryEvent((Player) sender);
                Bukkit.getPluginManager().callEvent(event);
                return true;
            }else if(args.length == 1) {
                StatManager.addExp(((Player) sender).getUniqueId(), 100);
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;
        }
        return false;
    }
}

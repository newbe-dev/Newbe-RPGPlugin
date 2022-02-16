package com.example.RPGPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PlayerClassCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {
            if (args.length == 0) {
                return false;
            } else if (args[0].equals("set")) {
                if (args.length != 2) {
                    return false;
                }
                PlayerClassManager.setPlayerClass((Player) sender, args[1]);
            } else if (args[0].equals("remove")) {
                if (args.length != 2) {
                    return false;
                }
                PlayerClassManager.removePlayerClass((Player) sender);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;
        }
        return false;
    }
}

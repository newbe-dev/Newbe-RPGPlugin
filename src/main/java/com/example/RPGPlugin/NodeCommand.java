package com.example.RPGPlugin;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class NodeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {
            if(args.length == 0) {
                Player player = (Player) sender;
                SerializeManager.yml.set("Test.test", player.getInventory().getItemInMainHand());
            }
            return true;
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;
        }
        return false;
    }
}

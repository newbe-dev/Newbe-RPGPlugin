package com.example.RPGPlugin.Reinforce;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ReinforceCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (sender instanceof Player) {
            OpenReinforceInventoryEvent event = new OpenReinforceInventoryEvent((Player) sender);
            Bukkit.getPluginManager().callEvent(event);
            return true;
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
            return false;
        }
        return false;
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

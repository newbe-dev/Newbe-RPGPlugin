package com.example.RPGPlugin.PlayerClass;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerClassCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  //명령어 실행 시
        if (args.length == 0) {
            Bukkit.getPluginManager().callEvent(new OpenPlayerClassInventoryEvent((Player) sender));
        } else if (args[0].equals("set")) {
            if (args.length == 2) {
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("콘솔에서는 실행할 수 없는 명령어입니다 !");
                    return false;
                }
                PlayerClassManager.setPlayerClass((Player) sender, args[1]);
                return true;
            } else if (args.length == 3) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    PlayerClassManager.setPlayerClass(player, args[2]);
                    return true;
                } else {
                    sender.sendMessage(String.format("%s는 존재하지 않는 유저입니다 !", args[1]));
                    return false;
                }
            } else {
                return false;
            }
        } else if (args[0].equals("rem") || args[0].equals("remove")) {
            if (args.length != 2) {
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                PlayerClassManager.removePlayerClass(player);
                return true;
            } else {
                sender.sendMessage(String.format("%s는 존재하지 않는 유저입니다 !", args[1]));
                return false;
            }
        }
        else if (args[0].equals("get")) {
            if (args.length != 2) {
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                sender.sendMessage(String.format("%s 님의 직업은 [%s] 입니다", player.getDisplayName(), PlayerClassManager.getPlayerClass(player).toString()));
                return true;
            } else {
                sender.sendMessage(String.format("%s는 존재하지 않는 유저입니다 !", args[1]));
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.isOp()) return null;
        if(command.getName().equals("cl") || command.getName().equals("class") || command.getName().equals("직업")) {
            if(args.length == 1) {
                return Arrays.asList("set", "remove", "get");
            }else if(args.length == 2) {
                switch (args[0]) {
                    case "set":
                    case "remove":
                    case "rem":
                    case "get": {
                        List<String> playerList = new ArrayList<>();
                        Bukkit.getOnlinePlayers().forEach(p -> playerList.add(p.getName()));
                        return playerList;
                    }
                }
            }
            else if(args.length == 3) {
                if(args[0].equals("set")) {
                    if(Bukkit.getPlayer(args[1]) != null) {
                        List<String> list = new ArrayList<>();
                        for(PlayerClass pc : PlayerClass.values())
                        {
                            list.add(pc.toString());
                        }
                        return list;
                    }
                }
            }
        }
        return null;
    }
}

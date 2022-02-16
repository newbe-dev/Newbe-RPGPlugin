package com.example.RPGPlugin.PlayerClasses;

import com.example.RPGPlugin.PlayerClass;
import com.example.RPGPlugin.PlayerClassManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Rogue implements Listener {
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(!PlayerClassManager.getPlayerClass(player).equals(PlayerClass.ROGUE)) return;
        e.setMessage("난 도적이다 !!!!!!!!!!");
    }
}

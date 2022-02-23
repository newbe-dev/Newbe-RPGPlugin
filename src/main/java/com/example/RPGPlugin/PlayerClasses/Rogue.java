package com.example.RPGPlugin.PlayerClasses;

import com.example.RPGPlugin.PlayerClass.PlayerClass;
import com.example.RPGPlugin.PlayerClass.PlayerClassManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Rogue implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(!PlayerClassManager.getPlayerClass(player).equals(PlayerClass.ROGUE)) return;
        e.setMessage("난 도적이다 !!!!!!!!!!");
    }
}

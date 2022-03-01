package com.example.RPGPlugin.Stat;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class StatListener implements Listener {

    @EventHandler
    public void onGainExp(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        double damage = e.getFinalDamage();
        if(e.getDamager() instanceof Player) {
            int attackPower = SerializeManager.yml.getInt(String.format("Plugin.Stat.%s.attackPower", e.getDamager().getUniqueId()));
            damage *= (1 + attackPower * 0.04);
        }
        if(e.getEntity() instanceof Player) {
            int defense = SerializeManager.yml.getInt(String.format("Plugin.Stat.%s.defense", e.getDamager().getUniqueId()));
            damage *= (1 - defense * 0.04);
        }
        e.setDamage(damage);
    }
}

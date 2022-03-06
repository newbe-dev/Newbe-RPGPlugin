package com.example.RPGPlugin.Stat;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class StatListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Stat.%s", player.getUniqueId()));

        if (node == null) {
            node = SerializeManager.yml.createSection(String.format("Plugin.Stat.%s", player.getUniqueId()));
        }

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double health = 20 + node.getInt("health") * 1.333;
        attribute.setBaseValue(health);
    }

    @EventHandler
    public void onGainExp(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        double damage = e.getFinalDamage();
        if(e.getDamager() instanceof Player) {
            int attackPower = SerializeManager.yml.getInt(String.format("Plugin.Stat.%s.attackPower", e.getDamager().getUniqueId()));
            damage *= (1 + attackPower * 0.04);
        }
        e.setDamage(damage);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        double damage = e.getFinalDamage();
        if(e.getEntity() instanceof Player) {
            int defense = SerializeManager.yml.getInt(String.format("Plugin.Stat.%s.defense", e.getEntity().getUniqueId()));
            damage /= (1 + defense * 0.04);
        }
        e.setDamage(damage);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(e.getEntity().getKiller() != null) {
            StatManager.addExp(e.getEntity().getKiller().getUniqueId(), 500);
        }
    }
}

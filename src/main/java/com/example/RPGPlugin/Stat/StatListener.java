package com.example.RPGPlugin.Stat;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double health = 20 + StatManager.getFinalStat(player, StatManager.STAT.health) * 1.333;
        attribute.setBaseValue(health);
        player.setHealth(health);
    }

    @EventHandler
    public void onGainExp(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getDamager() instanceof Player) {
            double damage = e.getFinalDamage();
            int attackPower = StatManager.getFinalStat((Player) e.getDamager(), StatManager.STAT.attackPower);
            damage *= 1 + attackPower * 0.03;
            e.getDamager().sendMessage(String.format("%s%s( -%.1f )", ChatColor.RED, ChatColor.BOLD, (float) damage));
            e.setDamage(damage);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            double damage = e.getFinalDamage();
            int defense = StatManager.getFinalStat((Player) e.getEntity(), StatManager.STAT.defense);
            damage /= (1 + defense * 0.03);
            e.setDamage(damage);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            StatManager.addExp(e.getEntity().getKiller().getUniqueId(), 500);
        }
    }
}

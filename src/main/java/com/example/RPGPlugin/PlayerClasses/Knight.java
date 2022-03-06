package com.example.RPGPlugin.PlayerClasses;

import com.example.RPGPlugin.PlayerClass.PlayerClass;
import com.example.RPGPlugin.PlayerClass.PlayerClassManager;
import com.example.RPGPlugin.SerializeManager;
import com.example.RPGPlugin.SkillManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Knight implements Listener {
    //

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player attacker = (Player) e.getDamager();
        if (PlayerClassManager.getPlayerClass(attacker) != PlayerClass.KNIGHT) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (attacker.getInventory().getItemInMainHand().getType().equals(Material.SHIELD)) {
                shieldBlow(attacker);
            }
        }
    }

    private void shieldBlow(Player player) {
        if (SkillManager.canUse(player, "SHIELD_BLOW")) {
            SkillManager.useSkill(player.getUniqueId(), "SHIELD_BLOW");
            Location location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
            List<Entity> entityList = new ArrayList<>(player.getWorld().getNearbyEntities(location, 3, 3, 3, entity -> entity.getType() != EntityType.PLAYER && entity.isValid()));
            int defense = SerializeManager.yml.getInt(String.format("Plugin.Stat.%s.defense", player.getUniqueId()));
            for (Entity entity : entityList) {
                Location facing = new Location(entity.getWorld(), 0, 0, 0, player.getLocation().getYaw(), 0);
                player.getVelocity().add(facing.getDirection().multiply(5));
                ((LivingEntity) entity).damage(5 + defense, player);
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 + defense * 5, defense >= 10 ? 2 : 1, false, true, true));
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 50);
                player.sendMessage(String.format("%s%s-%d ( 5 + %d )", ChatColor.GRAY, ChatColor.BOLD, 5 + defense, defense));
            }
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 50, -1);
            Bukkit.getScheduler().runTaskLater(SerializeManager.plugin, () -> {
                player.sendMessage(String.format("%s[ 방패 찍기 ] 가 준비되었습니다 !", ChatColor.DARK_GRAY));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 50, 1);
            }, 20 * 10);
        }
    }
}

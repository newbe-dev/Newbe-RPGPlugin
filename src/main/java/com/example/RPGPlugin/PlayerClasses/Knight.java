package com.example.RPGPlugin.PlayerClasses;

import com.example.RPGPlugin.PlayerClass.PlayerClass;
import com.example.RPGPlugin.PlayerClass.PlayerClassManager;
import com.example.RPGPlugin.SerializeManager;
import com.example.RPGPlugin.SkillManager;
import com.example.RPGPlugin.Stat.StatManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class Knight implements Listener {
    public static Map<UUID, Boolean> isDashing = new HashMap();

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player attacker = (Player) e.getDamager();

        if (!PlayerClassManager.getPlayerClass(attacker).equals(PlayerClass.KNIGHT)) return;
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;

        if (attacker.getInventory().getItemInMainHand().getType().equals(Material.SHIELD)) {
            shieldBlow(attacker, e);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (player.isSprinting()) {
            dash(player);
        }
    }

    @EventHandler
    public void onPlayerVelocityChange(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getLocation().add(0, -0.2, 0).getBlock().getType() != Material.AIR && isDashing.containsKey(player.getUniqueId()) && isDashing.get(player.getUniqueId())) {
            isDashing.put(player.getUniqueId(), false);
            boolean tech = StatManager.getNode(player.getUniqueId()).getInt("attackPower") > StatManager.getNode(player.getUniqueId()).getInt("defense");
            if (tech) {
                slash(player);
            } else {
                defenseCondition(player);
            }
        }
    }

    private void slash(Player player) {
        List<Entity> entityList = new ArrayList<>(player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5, entity -> entity.getType() != EntityType.PLAYER && entity.isValid()));
        int attack = StatManager.getFinalStat(player, StatManager.STAT.attackPower);
        player.sendMessage("slash");
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 30, 3, 3, 3);
        for (Entity entity : entityList) {
            // Impulse
            Vector dir = entity.getLocation().toVector().subtract(player.getLocation().toVector());
            dir.normalize();
            dir.setY(0.3);
            entity.setVelocity(entity.getVelocity().add(dir.multiply(2)));
            // Damage
            ((Damageable) entity).damage(4 + attack, player);
            EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.CUSTOM, 4 + attack);
            entity.setLastDamageCause(event);
            Bukkit.getServer().getPluginManager().callEvent(event);
            // Effect
            //
            //((LivingEntity) entity).setAI(false);
            //Bukkit.getScheduler().runTaskLater(SerializeManager.getPlugin(), () -> ((LivingEntity) entity).setAI(true), 20 + 3L * attack);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 20, 1);
    }

    private void defenseCondition(Player player) {
        StatManager.tempDef.put(player.getUniqueId(), StatManager.tempDef.getOrDefault(player.getUniqueId(), 0) + 3);
        Bukkit.getScheduler().runTaskLater(SerializeManager.getPlugin(), () -> {
            StatManager.tempDef.put(player.getUniqueId(), StatManager.tempDef.getOrDefault(player.getUniqueId(), 0) - 3);
        }, 20 * 3L);
    }

    private void dash(Player player) {
        final String DASH = "DASH";
        if (!SkillManager.canUse(player, DASH)) return;

        // Impulse
        SkillManager.useSkill(player.getUniqueId(), DASH);
        Location facing = player.getLocation();
        facing.setPitch(-20);
        player.setVelocity(player.getVelocity().add(facing.getDirection().multiply(1)));
        // Effect
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 30, 3, 0, 3);
        player.playSound(player, Sound.BLOCK_HONEY_BLOCK_BREAK, 50, 1);
        isDashing.put(player.getUniqueId(), true);
        Bukkit.getScheduler().runTaskLater(SerializeManager.getPlugin(), () -> {
            player.sendMessage(String.format("%s[ 도약 ] 이 준비되었습니다 !", ChatColor.DARK_GRAY));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 50, 1);
        }, 20 * SkillManager.getSkillCdt(DASH) / 1000);
    }

    private void shieldBlow(Player player, EntityDamageByEntityEvent e) {
        //
        final String SHIELD_BLOW = "SHIELD_BLOW";

        if (SkillManager.canUse(player, SHIELD_BLOW)) {
            e.setCancelled(true);
            SkillManager.useSkill(player.getUniqueId(), SHIELD_BLOW);
            Location location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
            List<Entity> entityList = new ArrayList<>(player.getWorld().getNearbyEntities(location, 3, 3, 3, entity -> entity.getType() != EntityType.PLAYER && entity.isValid()));
            int defense = StatManager.getFinalStat(player, StatManager.STAT.defense);
            for (Entity entity : entityList) {
                // Impulse
                Location facing = player.getLocation();
                facing.setPitch(0);
                entity.setVelocity(entity.getVelocity().add(facing.getDirection().multiply(3)));
                // Damage
                ((Damageable) entity).damage(5 + defense, player);
                EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.CUSTOM, 5 + defense);
                entity.setLastDamageCause(event);
                Bukkit.getServer().getPluginManager().callEvent(event);
                // Effect
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 30, 0.5, 0.5, 0.5);
                player.sendMessage(String.format("%s%s-%d ( 5 + %d )", ChatColor.GRAY, ChatColor.BOLD, 5 + defense, defense));
                //
                ((LivingEntity) entity).setAI(false);
                Bukkit.getScheduler().runTaskLater(SerializeManager.getPlugin(), () -> ((LivingEntity) entity).setAI(true), 20 + 3L * defense);
            }


            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 20, 1);

            Bukkit.getScheduler().runTaskLater(SerializeManager.getPlugin(), () -> {
                player.sendMessage(String.format("%s[ 방패 찍기 ] 가 준비되었습니다 !", ChatColor.DARK_GRAY));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 50, 1);
            }, 20 * SkillManager.getSkillCdt(SHIELD_BLOW) / 1000);
        }
    }
}

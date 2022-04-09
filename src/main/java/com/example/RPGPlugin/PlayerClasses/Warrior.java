package com.example.RPGPlugin.PlayerClasses;

import com.example.RPGPlugin.PlayerClass.PlayerClass;
import com.example.RPGPlugin.PlayerClass.PlayerClassManager;
import com.example.RPGPlugin.SkillManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Warrior implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!PlayerClassManager.getPlayerClass(player).equals(PlayerClass.WARRIOR)) return;
        if (player.getInventory().getItemInMainHand().getType().equals(Material.SHIELD) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            weaponDefense(player);
        }
    }

    private void weaponDefense(Player player) {
        final String WEAPON_DEFENSE = "WEAPON_DEFENSE";
        if (!SkillManager.canUse(player, WEAPON_DEFENSE)) return;
        // Impulse
        SkillManager.useSkill(player.getUniqueId(), WEAPON_DEFENSE);

    }
}

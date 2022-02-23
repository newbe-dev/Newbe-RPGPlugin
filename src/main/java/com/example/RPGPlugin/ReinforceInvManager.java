package com.example.RPGPlugin;

import com.example.RPGPlugin.Reinforce.OpenReinforceInventoryEvent;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ReinforceInvManager implements Listener {
    private final Inventory inv;
    private final ItemStack reinforceAnvil = new ItemStack(Material.ANVIL);
    private final ItemStack pinkPlane = new ItemStack(Material.PINK_STAINED_GLASS_PANE, 1);
    private final ItemStack whitePlane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);

    public ReinforceInvManager() {
        inv = Bukkit.createInventory(null, 54, "강화");
        ItemMeta itemMeta = reinforceAnvil.getItemMeta();
        itemMeta.setDisplayName(String.format("%s%s%s강화", ChatColor.BOLD, ChatColor.ITALIC, ChatColor.DARK_BLUE));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(String.format("%s%s--------------", ChatColor.BOLD, ChatColor.WHITE));
        lore.add(String.format("%s%s%s강화 성공 확률: -", ChatColor.BOLD, ChatColor.ITALIC, ChatColor.GRAY));
        itemMeta.setLore(lore);
        reinforceAnvil.setItemMeta(itemMeta);
        itemMeta = pinkPlane.getItemMeta();
        itemMeta.setDisplayName(" ");
        pinkPlane.setItemMeta(itemMeta);
        itemMeta = whitePlane.getItemMeta();
        itemMeta.setDisplayName(" ");
        whitePlane.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onOpenReinforceInventory(OpenReinforceInventoryEvent e) {
        inv.clear();
        for (int i = 0; i < 54; i++) {
            if (i == 11 || i == 29) { // 재료 1, 2
                inv.setItem(i, new ItemStack(Material.AIR));
            } else if (i == 22) {
                inv.setItem(i, reinforceAnvil);
            } else if (i == 24) { // 결과 아이템
                inv.setItem(i, pinkPlane);
            } else {
                inv.setItem(i, whitePlane);
            }
        }
        e.player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   //인벤토리 클릭 시
        if (e.getInventory() != inv) return;    //이 인벤토리를 클릭한게 아니라면 취소
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) return;
        if (clickedItem.equals(whitePlane) || clickedItem.equals(pinkPlane) || clickedItem.equals(reinforceAnvil))
            e.setCancelled(true);   //위치 변경 취소
        if (clickedItem.getType().equals(Material.AIR)) return;
        // TODO : 인벤토리에 아이템 올려두면 강화하는거 --
    }
}
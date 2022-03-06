package com.example.RPGPlugin.Reinforce;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ReinforceInvManager implements Listener {
    private final ItemStack PINK_PANEL;
    private final ItemStack GRAY_PANEL;

    private ItemStack createItem(Material material, String displayName, List<String> lore, Boolean hideFlags) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        if (hideFlags) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ReinforceInvManager() {
        PINK_PANEL = createItem(Material.PINK_STAINED_GLASS_PANE, "", null, true);
        GRAY_PANEL = createItem(Material.GRAY_STAINED_GLASS_PANE, "", null, true);
    }

    @EventHandler
    public void onOpenReinforceInventory(OpenReinforceInventoryEvent e) {
        e.player.openInventory(createInventory());
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, 45, "강화");
        inventory.clear();

        ItemStack anvil = createItem(Material.ANVIL, String.format("%s강화", ChatColor.BOLD), Arrays.asList("-", String.format("%s-", ChatColor.BLUE), String.format("%s-", ChatColor.GRAY), String.format("%s-", ChatColor.DARK_GRAY)), true);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, GRAY_PANEL);
        }
        inventory.setItem(11, new ItemStack(Material.AIR));
        inventory.setItem(22, anvil);
        inventory.setItem(24, PINK_PANEL);
        inventory.setItem(29, new ItemStack(Material.AIR));

        //reloadPage(inventory);
        // -- // --
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   // 인벤토리 클릭 시
        if (!(e.getInventory().contains(GRAY_PANEL) && e.getView().getTitle().equals("강화")))
            return;    // 이 인벤토리를 클릭한게 아니라면 취소
        ItemStack clickedItem = e.getCurrentItem(); // 클릭된 아이템
        if (clickedItem == null) return;
        if(clickedItem.equals(PINK_PANEL) || clickedItem.equals(GRAY_PANEL) || clickedItem.getType().equals(Material.ANVIL)) {
            e.setCancelled(true);   //위치 변경 취소
        }
        Inventory inventory = e.getInventory();

        if (e.isLeftClick() && clickedItem.getType().equals(Material.ANVIL)) {
            reinforce(inventory);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().contains(GRAY_PANEL) && e.getView().getTitle().equals("강화")) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            if(e.getOldCursor().equals(PINK_PANEL) || e.getOldCursor().equals(GRAY_PANEL) || e.getOldCursor().getType().equals(Material.ANVIL)) {
                e.setCancelled(true);   //위치 변경 취소
            }
            else if(e.getCursor().equals(PINK_PANEL) || e.getCursor().equals(GRAY_PANEL) || e.getCursor().getType().equals(Material.ANVIL)) {
                e.setCancelled(true);   //위치 변경 취소
            }
            reloadPage(e.getInventory());
        }
    }

    private void reloadPage(Inventory inventory) {
        ItemStack i1 = inventory.getItem(11), i2 = inventory.getItem(29);
        if(i1 == null || i2 == null) {
            inventory.setItem(22, createItem(Material.ANVIL, String.format("%s강화", ChatColor.BOLD), Arrays.asList("-", String.format("%s-", ChatColor.BLUE), String.format("%s-", ChatColor.GRAY), String.format("%s-", ChatColor.DARK_GRAY)), true));
        }
        else if(!(i1.getType().equals(Material.IRON_SWORD) || i1.getType().equals(Material.DIAMOND_SWORD))) {
            inventory.setItem(22, createItem(Material.ANVIL, String.format("%s강화", ChatColor.BOLD), Arrays.asList("-", String.format("%s-", ChatColor.BLUE), String.format("%s-", ChatColor.GRAY), String.format("%s-", ChatColor.DARK_GRAY)), true));
        }
        else {
            float success = 0, fail = 0;
            inventory.setItem(22, createItem(Material.ANVIL, String.format("%s강화", ChatColor.BOLD), Arrays.asList("-", String.format("%s성공: %.3f", ChatColor.BLUE, success), String.format("%s강등: %.3f", ChatColor.GRAY, fail), String.format("%s파괴: %.3f", ChatColor.DARK_GRAY, 100 - success - fail)), true));
        }
    }

    private void reinforce(Inventory inventory) {
        reloadPage(inventory);
    }
}
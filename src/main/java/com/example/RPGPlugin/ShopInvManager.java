package com.example.RPGPlugin;

import com.example.RPGPlugin.Shop.OpenShopInventoryEvent;
import com.example.RPGPlugin.Shop.ShopInfo;
import com.example.RPGPlugin.Shop.tradeInfo;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class ShopInvManager implements Listener {
    private static ArrayList<tradeInfo> tradeInfoArrayList;

    private final ItemStack GRAY_PANEL;
    private final ItemStack NONE;

    public ShopInvManager() {
        Bukkit.createInventory(null, 54, "상점");
        GRAY_PANEL = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta = GRAY_PANEL.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + ChatColor.ITALIC.toString() + " ");
        itemMeta.removeItemFlags();
        GRAY_PANEL.setItemMeta(itemMeta);
        NONE = new ItemStack(Material.AIR);
    }

    @EventHandler
    public void onOpenShopInventory(OpenShopInventoryEvent e) {
        if (!SerializeManager.yml.contains("Plugin.shop0")) {
            ShopInfo.InitShopInfo();
            ShopInfo.conductSerializing();
        } else {
            ShopInfo.conductDeserializing();
        }

        if (e.shopNo >= ShopInfo.shopInfoArrayList.size()) {
            e.player.sendMessage("존재하지 않는 상점 번호입니다!");
            return;
        }

        e.player.openInventory(createShopInventory(e.shopNo));
    }

    private Inventory createShopInventory(int shopNo) {
        tradeInfoArrayList = ShopInfo.shopInfoArrayList.get(shopNo);
        Inventory inventory;
        switch (shopNo) {
            case 0: {
                inventory = Bukkit.createInventory(null, 54, String.format("%s%s[ 무기 ]", ChatColor.BOLD, ChatColor.YELLOW));
                break;
            }
            case 1: {
                inventory = Bukkit.createInventory(null, 54, String.format("%s%s[ 방어구 ]", ChatColor.BOLD, ChatColor.DARK_BLUE));
                break;
            }
            default: {
                inventory = Bukkit.createInventory(null, 54, "");
                break;
            }
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, GRAY_PANEL);
        }
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                inventory.setItem((i * 9) + j + 1, NONE);
            }
        }
        for (int i = 0; i < tradeInfoArrayList.size(); i++) {
            inventory.setItem(10 + i*2, tradeInfoArrayList.get(i).buyItemStack);
        }
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   //인벤토리 클릭 시
        if (!e.getInventory().contains(GRAY_PANEL) || e.getInventory().getHolder() != null)
            return;    //이 인벤토리를 클릭한게 아니라면 취소
        e.setCancelled(true);   //위치 변경 취소
        ItemStack clickedItem = e.getCurrentItem(); //클릭된 아이템
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        int itemIndex;
        for (itemIndex = 0; itemIndex < tradeInfoArrayList.size(); itemIndex++) {
            if (tradeInfoArrayList.get(itemIndex).buyItemStack.equals(clickedItem)) {
                break;
            }
        }
        if (itemIndex == tradeInfoArrayList.size()) return;

        Player player = (Player) e.getWhoClicked(); //클릭한 사람에게
        tradeInfo tradeInfo = tradeInfoArrayList.get(itemIndex);
        if (player.getInventory().containsAtLeast(tradeInfo.priceItemStack, tradeInfo.priceItemStack.getAmount())) {
            ItemStack itemStack = clickedItem.clone();
            // 가격 삭제
            ItemMeta temp = itemStack.getItemMeta();
            assert temp != null;
            Objects.requireNonNull(temp.getLore()).remove(Objects.requireNonNull(itemStack.getItemMeta().getLore()).size() - 1);
            player.getInventory().addItem(itemStack);
            player.getInventory().removeItem(tradeInfo.priceItemStack);
            player.sendMessage(String.format("%s거래에 성공했습니다!", ChatColor.GREEN));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 50, 2);
        } else {
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 50, 1);
            player.sendMessage(String.format("%s에메랄드가 부족합니다!", ChatColor.RED));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) { //인벤토리 드래그 시
        if (!e.getInventory().contains(GRAY_PANEL) || e.getInventory().getHolder() != null) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        Player player = e.getPlayer();
        if (SerializeManager.yml.getInt(String.format("Plugin.Shop.Villager.%s", entity.getEntityId()), -1) == -1)
            return;

        OpenShopInventoryEvent event = new OpenShopInventoryEvent(player, SerializeManager.yml.getInt(String.format("Plugin.Shop.Villager.%s", entity.getEntityId())));
        Bukkit.getPluginManager().callEvent(event);
    }
}

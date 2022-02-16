package com.example.RPGPlugin;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
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
    private final Inventory inv;
    private ArrayList<tradeInfo> tradeInfoArrayList;
    private final ShopInfo shopInfo = new ShopInfo();

    public ShopInvManager() {
        inv = Bukkit.createInventory(null, 54, "상점");
    }

    @EventHandler
    public void onOpenShopInventory(OpenShopInventoryEvent e) {
        tradeInfoArrayList = shopInfo.shopInfoArrayList.get(e.shopNo);
        inv.clear();

        for (int i = 0; i < tradeInfoArrayList.size(); i++) {
            inv.setItem(19 + 2 * i, tradeInfoArrayList.get(i).priceItemStack);
        }
        e.player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   //인벤토리 클릭 시
        if (e.getInventory() != inv) return;    //이 인벤토리를 클릭한게 아니라면 취소
        e.setCancelled(true);   //위치 변경 취소
        ItemStack clickedItem = e.getCurrentItem(); //클릭된 아이템
        //만약 클릭된 아이템이 없다면 취소
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
        if (e.getInventory() == inv) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.VILLAGER)) {
            Player player = e.getPlayer();
            for (String tag : e.getRightClicked().getScoreboardTags()) {
                if (tag.startsWith("CustomShopNum")) {
                    player.performCommand("RPGPlugin:shop " + tag.split(":")[1]);
                }
            }
        }
    }
}

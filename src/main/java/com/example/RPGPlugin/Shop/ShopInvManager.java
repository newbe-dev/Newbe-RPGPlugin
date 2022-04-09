package com.example.RPGPlugin.Shop;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopInvManager implements Listener {
    private static ArrayList<tradeInfo> tradeInfoArrayList;

    private final ItemStack BLACK_PANEL;

    public ShopInvManager() {
        Bukkit.createInventory(null, 54, "상점");
        BLACK_PANEL = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
    }

    private ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onOpenShopInventory(OpenShopInventoryEvent e) {
        if (!SerializeManager.yml.contains("Shop.s0")) {
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
        inventory = Bukkit.createInventory(null, 54, "상점");

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_PANEL);
        }
        for (int i = 0; i < tradeInfoArrayList.size(); i++) {
            inventory.setItem(10 + i * 2, tradeInfoArrayList.get(i).buyItemStack);
        }
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   //인벤토리 클릭 시
        if (!(e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("상점")))
            return;    //이 인벤토리를 클릭한게 아니라면 취소
        e.setCancelled(true);   //위치 변경 취소
        ItemStack clickedItem = e.getCurrentItem(); //클릭된 아이템
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR) || !clickedItem.hasItemMeta()) return;

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
            temp.getLore().remove(itemStack.getItemMeta().getLore().size() - 1);
            temp.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(temp);

            player.getInventory().addItem(itemStack);
            player.getInventory().removeItem(tradeInfo.priceItemStack);
            player.sendMessage(String.format("%s%s거래에 성공했습니다 !", ChatColor.GREEN, ChatColor.BOLD));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 50, 2);
        } else {
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 50, 1);
            int i = 0;
            for (int j = 0; j < player.getInventory().getSize(); j++) {
                if(player.getInventory().getItem(j) != null) {
                    if(player.getInventory().getItem(j).getType() == Material.CHARCOAL) {
                        i += player.getInventory().getItem(j).getAmount();
                    }
                }
            }
            player.sendMessage(String.format("%s%s구매할 수 없습니다 ! (부족한 코인: %d)", ChatColor.RED, ChatColor.BOLD, tradeInfo.priceItemStack.getAmount() - i));
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) { //인벤토리 드래그 시
        if (e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("상점")) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        Player player = e.getPlayer();
        if (entity instanceof Player) return;
        if (!SerializeManager.yml.contains(String.format("Plugin.Shop.Villager.%s", entity.getUniqueId())))
            return;
        OpenShopInventoryEvent event = new OpenShopInventoryEvent(player, SerializeManager.yml.getInt(String.format("Plugin.Shop.Villager.%s", entity.getUniqueId())));
        Bukkit.getPluginManager().callEvent(event);
    }
}

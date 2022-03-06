package com.example.RPGPlugin.Stat;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StatInvManager implements Listener {

    private final ItemStack GRAY_PANEL;
    private final ItemStack SWORD;
    private final ItemStack BOOK;
    private final ItemStack CHEST_PLATE;
    private final ItemStack APPLE;
    private final ItemStack PAPER;

    public StatInvManager() {
        GRAY_PANEL = createItem(Material.GRAY_STAINED_GLASS_PANE, "", null);
        SWORD = createItem(Material.IRON_SWORD, "", Arrays.asList(String.format("%s일반 공격과 스킬이 주는 피해가 증가합니다.", ChatColor.GRAY)));
        BOOK = createItem(Material.ENCHANTED_BOOK, "", Arrays.asList(String.format("%s스킬 피해가 증가합니다.", ChatColor.GRAY)));
        CHEST_PLATE = createItem(Material.IRON_CHESTPLATE, "", Arrays.asList(String.format("%s방어력이 증가합니다.", ChatColor.GRAY)));
        APPLE = createItem(Material.GOLDEN_APPLE, "", Arrays.asList(String.format("%s최대 체력이 증가하고 재생 속도가 빨라집니다.", ChatColor.GRAY)));
        PAPER = createItem(Material.PAPER, "", null);
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

    public ItemStack changeNameItem(ItemStack itemStack, String newName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(newName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onOpenStatInventoryEvent(OpenStatInventoryEvent e) {
        e.player.openInventory(createInventory(e.player));
    }

    private Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "스탯");
        reloadPage(inventory, player);
        // -- // --
        return inventory;
    }

    public void reloadPage(Inventory inventory, Player player) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Stat.%s", player.getUniqueId()));
        if (node == null)
            node = SerializeManager.yml.createSection(String.format("Plugin.Stat.%s", player.getUniqueId()));

        int point = node.getInt("point");
        int attackPower = node.getInt("attackPower");
        int skillPower = node.getInt("skillPower");
        int defense = node.getInt("defense");
        int health = node.getInt("health");

        // -- 초기화 --
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, GRAY_PANEL);
        }
        inventory.setItem(10, changeNameItem(SWORD, String.format("%s%s공격력 %s( +%d )", ChatColor.BOLD, ChatColor.RED, ChatColor.WHITE, attackPower)));
        inventory.setItem(12, changeNameItem(BOOK, String.format("%s%s주문력 %s( +%d )", ChatColor.BOLD, ChatColor.AQUA, ChatColor.WHITE, skillPower)));
        inventory.setItem(14, changeNameItem(CHEST_PLATE, String.format("%s%s방어력 %s( +%d )", ChatColor.BOLD, ChatColor.GRAY, ChatColor.WHITE, defense)));
        inventory.setItem(16, changeNameItem(APPLE, String.format("%s%s체력 %s( +%d )", ChatColor.BOLD, ChatColor.GREEN, ChatColor.WHITE, health)));
        inventory.setItem(22, changeNameItem(PAPER, String.format("%s%s사용 가능한 스탯 포인트: %s%d", ChatColor.BOLD, ChatColor.GREEN, ChatColor.WHITE, point)));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   // 인벤토리 클릭 시
        if (!(e.getInventory().contains(GRAY_PANEL) && e.getView().getTitle().equals("스탯")))
            return;    // 이 인벤토리를 클릭한게 아니라면 취소
        e.setCancelled(true);   // 위치 변경 취소
        ItemStack clickedItem = e.getCurrentItem(); // 클릭된 아이템
        Player player = (Player) e.getWhoClicked(); // 플레이어
        if (clickedItem == null) return;

        if (e.isLeftClick()) {
            switch (clickedItem.getType()) {
                case IRON_SWORD:
                    StatManager.useStatPoint(player.getUniqueId(), StatManager.STAT.attackPower, 1);
                    break;
                case ENCHANTED_BOOK:
                    StatManager.useStatPoint(player.getUniqueId(), StatManager.STAT.skillPower, 1);
                    break;
                case IRON_CHESTPLATE:
                    StatManager.useStatPoint(player.getUniqueId(), StatManager.STAT.defense, 1);
                    break;
                case GOLDEN_APPLE:
                    StatManager.useStatPoint(player.getUniqueId(), StatManager.STAT.health, 1);
                    break;
            }
        } else if (e.isRightClick()) {
            switch (clickedItem.getType()) {
                case IRON_SWORD:
                    StatManager.removeStatPoint(player.getUniqueId(), StatManager.STAT.attackPower, 1);
                    break;
                case ENCHANTED_BOOK:
                    StatManager.removeStatPoint(player.getUniqueId(), StatManager.STAT.skillPower, 1);
                    break;
                case IRON_CHESTPLATE:
                    StatManager.removeStatPoint(player.getUniqueId(), StatManager.STAT.defense, 1);
                    break;
                case GOLDEN_APPLE:
                    StatManager.removeStatPoint(player.getUniqueId(), StatManager.STAT.health, 1);
                    break;
            }
        }

        reloadPage(e.getInventory(), player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) { //인벤토리 드래그 시
        if (e.getInventory().contains(GRAY_PANEL) && e.getView().getTitle().equals("스탯")) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }
}

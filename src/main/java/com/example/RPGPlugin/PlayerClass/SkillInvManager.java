package com.example.RPGPlugin.PlayerClass;

import com.example.RPGPlugin.Stat.OpenStatInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

public class SkillInvManager implements Listener {

    private final ItemStack BLACK_PANEL;
    private final ItemStack GRAY_PANEL;

    public SkillInvManager() {
        BLACK_PANEL = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        GRAY_PANEL = createItem(Material.GRAY_STAINED_GLASS_PANE, String.format("%s잠금", ChatColor.GRAY), null);
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
    public void onOpenSkillInventoryEvent(OpenSkillInventoryEvent e) {
        e.player.openInventory(createInventory(e.player));
    }

    private Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, "스킬");

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_PANEL);
        }//19, 12/30, 14/32, 16/34
        PlayerClass playerClass = PlayerClassManager.getPlayerClass(player);
        ItemStack s1 = GRAY_PANEL, s2 = GRAY_PANEL, s3 = GRAY_PANEL, s4 = GRAY_PANEL, s5 = GRAY_PANEL, s6 = GRAY_PANEL, s7 = GRAY_PANEL;
        switch (playerClass) {
            default:
            case NONE:
                break;
            case KNIGHT:
                s1 = createItem(Material.BLAZE_POWDER, String.format("%s방패 찍기 %s(방패 + 좌클릭) (방어력)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                        Arrays.asList(String.format("%s쿨타임: 10초", ChatColor.GRAY),
                                "",
                                String.format("%s전방의 적들에게 (A)의 피해를 주고", ChatColor.GRAY),
                                String.format("%s(B)초만큼 약화 디버프 (C)를 적용합니다.", ChatColor.GRAY),
                                "",
                                String.format("%sA: 5 + d", ChatColor.WHITE),
                                String.format("%sB: 1 + 0.25d", ChatColor.WHITE),
                                String.format("%sC: I - 0~14, II - 15~", ChatColor.WHITE)));
                s2 = createItem(Material.NETHERITE_HELMET, String.format("%s기사도 %s(패시브) (공격력/방어력)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                        Arrays.asList("",
                                String.format("%s공격력과 방어력이 서로 전환됩니다.", ChatColor.GRAY),
                                "",
                                String.format("%s공격력 3 -> 방어력 1", ChatColor.WHITE),
                                String.format("%s방어력 3 -> 공격력 1", ChatColor.WHITE)));
                break;
            case MAGE:
                break;
            case WARRIOR:
                break;
            case PRIEST:
                break;
            case MERCENARY:
                break;
            case ARCHER:
                break;
        }
        inventory.setItem(19, s1);
        inventory.setItem(12, s2);
        inventory.setItem(30, s3);
        inventory.setItem(14, s4);
        inventory.setItem(32, s5);
        inventory.setItem(16, s6);
        inventory.setItem(34, s7);
        // -- // --
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   // 인벤토리 클릭 시
        if (!(e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("스킬")))
            return;    // 이 인벤토리를 클릭한게 아니라면 취소
        e.setCancelled(true);   // 위치 변경 취소
        if (!e.isLeftClick())
            return;
        ItemStack clickedItem = e.getCurrentItem(); // 클릭된 아이템
        Player player = (Player) e.getWhoClicked(); // 플레이어

        if (clickedItem == null) return;
        switch (clickedItem.getType()) {
            case PAPER:
                Bukkit.getPluginManager().callEvent(new OpenStatInventoryEvent(player));
                break;
            case ENCHANTED_BOOK:
                Bukkit.getPluginManager().callEvent(new OpenSkillInventoryEvent(player));
                break;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) { //인벤토리 드래그 시
        if (e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("스킬")) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }
}

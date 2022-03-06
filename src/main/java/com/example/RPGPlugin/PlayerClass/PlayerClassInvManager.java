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

public class PlayerClassInvManager implements Listener {

    private final ItemStack BLACK_PANEL;
    private final ItemStack PAPER;
    private final ItemStack BOOK;

    public PlayerClassInvManager() {
        BLACK_PANEL = createItem(Material.BLACK_STAINED_GLASS_PANE, "", null);
        PAPER = createItem(Material.PAPER, String.format("%s스탯", ChatColor.BOLD), null);
        BOOK = createItem(Material.ENCHANTED_BOOK, String.format("%s%s스킬", ChatColor.BOLD, ChatColor.AQUA), null);
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
    public void onOpenPlayerClassInventoryEvent(OpenPlayerClassInventoryEvent e) {
        e.player.openInventory(createInventory(e.player));
    }

    private Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "직업");

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_PANEL);
        }

        PlayerClass playerClass = PlayerClassManager.getPlayerClass(player);
        ItemStack classItem;
        switch (playerClass) {
            case NONE:
            default:
                classItem = createItem(Material.WHITE_STAINED_GLASS_PANE, String.format("%s없음", ChatColor.BOLD), Arrays.asList(String.format("%s%s퀘스트를 완수해 직업을 선택할 수 있습니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
            case KNIGHT:
                classItem = createItem(Material.NETHERITE_HELMET, String.format("%s%s기사", ChatColor.BOLD, ChatColor.GRAY), Arrays.asList(String.format("%s%s높은 방어력과 강력한 공격으로", ChatColor.BOLD, ChatColor.GRAY), String.format("%s%s수준높은 근접전투를 구사합니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
            case MAGE:
                classItem = createItem(Material.NETHER_STAR, String.format("%s%s마법사", ChatColor.BOLD, ChatColor.AQUA), Arrays.asList(String.format("%s%s긴 사거리의 스킬을 통해 원거리 전투를 구사합니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
            case WARRIOR:
                classItem = createItem(Material.IRON_SWORD, String.format("%s%s전사", ChatColor.BOLD, ChatColor.RED), Arrays.asList(String.format("%s%s체력 회복을 통한 지속 전투에 유리합니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
            case PRIEST:
                classItem = createItem(Material.WHITE_STAINED_GLASS_PANE, String.format("%s%s성직자", ChatColor.BOLD, ChatColor.YELLOW), Arrays.asList(String.format("%s%s든든한 국밥같은 후방 힐러입니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
            case MERCENARY:
                classItem = createItem(Material.GOLD_INGOT, String.format("%s%s용병", ChatColor.BOLD, ChatColor.GOLD), Arrays.asList(String.format("%s%s돈을 모을수록 강해지는 쩌는 직업입니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
            case ARCHER:
                classItem = createItem(Material.BOW, String.format("%s%s궁수", ChatColor.BOLD, ChatColor.GREEN), Arrays.asList(String.format("%s%s활을 통한 원거리 전투를 구사합니다.", ChatColor.BOLD, ChatColor.GRAY)));
                break;
        }

        inventory.setItem(10, PAPER);
        inventory.setItem(13, classItem);
        inventory.setItem(16, BOOK);
        // -- // --
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   // 인벤토리 클릭 시
        if (!(e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("직업")))
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
        if (e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("직업")) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }
}

package com.example.RPGPlugin.PlayerClass;

import com.example.RPGPlugin.SkillManager;
import com.example.RPGPlugin.Stat.OpenStatInventoryEvent;
import com.example.RPGPlugin.Stat.StatManager;
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
    private final ItemStack WHITE_PANEL;

    public SkillInvManager() {
        BLACK_PANEL = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        WHITE_PANEL = createItem(Material.WHITE_STAINED_GLASS_PANE, String.format("%s잠금", ChatColor.GRAY), null);
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
        ItemStack s1 = WHITE_PANEL, s2 = WHITE_PANEL, s3 = WHITE_PANEL, s4 = WHITE_PANEL, s5 = WHITE_PANEL, s6 = WHITE_PANEL, s7 = WHITE_PANEL;
        switch (playerClass) {
            default:
            case NONE:
                break;
            case KNIGHT:
                boolean tech = StatManager.getNode(player.getUniqueId()).getInt("attackPower") > StatManager.getNode(player.getUniqueId()).getInt("defense");
                String st1, st2;
                if (tech) { //attack
                    st1 = String.format("%s도약 후 주위를 빠르게 베어내", ChatColor.RED);
                    st2 = String.format("%s(4 + 공격력)의 피해를 주고 밀쳐냅니다.", ChatColor.RED);
                } else {
                    st1 = String.format("%s도약 후 방어 태세를 갖춰", ChatColor.AQUA);
                    st2 = String.format("%s3초 동안 방어력이 %d 증가합니다.", ChatColor.AQUA, player.getLevel() / 3 + 2);
                }
                s1 = createItem(Material.BLAZE_POWDER, String.format("%s도약(%s) %s(달리기+웅크리기) (공격력/방어력)", ChatColor.BOLD, tech ? "베기" : "방어 태세", ChatColor.DARK_GRAY),
                        Arrays.asList("",
                                String.format("%s쿨타임: %d초", ChatColor.GRAY, SkillManager.getSkillCdt("DASH") / 1000),
                                String.format("%s가장 높은 스탯에 따라 시전 후 효과가 바뀝니다. ( %s베기 %s/ %s방어 태세 )", ChatColor.DARK_GRAY, ChatColor.RED, ChatColor.GRAY, ChatColor.AQUA),
                                String.format("%s앞으로 짧은 거리를 도약하고", ChatColor.GRAY),
                                st1, st2));
                if (player.getLevel() >= 5) {
                    s2 = createItem(Material.BLAZE_POWDER, String.format("%s방패 찍기 %s(방패 + 좌클릭) (방어력)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                            Arrays.asList(String.format("%s쿨타임: %d초", ChatColor.GRAY, SkillManager.getSkillCdt("SHIELD_BLOW") / 1000),
                                    "",
                                    String.format("%s방패로 가격해 전방의 적들에게 (5 + 방어력)의 피해를 주고", ChatColor.GRAY),
                                    String.format("%s(1 + 0.15 * 방어력) 초만큼 기절시킵니다.", ChatColor.GRAY)));
                    s3 = createItem(Material.NETHERITE_HELMET, String.format("%s기사도 %s(패시브) (공격력/방어력)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                            Arrays.asList("",
                                    String.format("%s스탯으로 올린 공격력과 방어력이 서로 전환됩니다.", ChatColor.GRAY),
                                    "",
                                    String.format("%s얻은 공격력 (+%d)", ChatColor.GRAY, StatManager.getNode(player.getUniqueId()).getInt("defense") / 3),
                                    String.format("%s얻은 방어력 (+%d)", ChatColor.GRAY, StatManager.getNode(player.getUniqueId()).getInt("attackPower") / 3)));
                }

                break;
            case MAGE:
                break;
            case WARRIOR:
                s1 = createItem(Material.BLAZE_POWDER, String.format("%s무기 방어 %s (도끼 + 우클릭)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                        Arrays.asList("",
                                String.format("%s우클릭을 눌러 방어 태세를 취할 수 있습니다.", ChatColor.GRAY),
                                String.format("%s흡수한 피해만큼 도끼의 내구도가 감소하고,", ChatColor.GRAY),
                                String.format("%s0이 될 경우 방어가 해제됩니다. (내구도는 자동으로 차오릅니다.)", ChatColor.GRAY),
                                String.format("%s또한, 방어 중 받는 상태 이상을 무력화합니다.", ChatColor.GRAY)));
                s2 = createItem(Material.BLAZE_POWDER, String.format("%s회전 베기 %s(도끼 + 우클릭) (차지) (공격력)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                        Arrays.asList(String.format("%s쿨타임: %d초", ChatColor.GRAY, SkillManager.getSkillCdt("ROTATE_SLASH") / 1000),
                                "",
                                String.format("%s회전하며 적들을 타격해 (2 + 공격력)의 피해를 입힙니다.", ChatColor.GRAY),
                                String.format("%s스킬을 차지하는 동안 범위와 회전 횟수가 상승합니다.", ChatColor.GRAY)));
                s3 = createItem(Material.BLAZE_POWDER, String.format("%s광폭화 %s (공격력)", ChatColor.BOLD, ChatColor.DARK_GRAY),
                        Arrays.asList(String.format("%s쿨타임: %d초", ChatColor.GRAY, SkillManager.getSkillCdt("BERSERK") / 1000),
                                "",
                                String.format("%s힘을 해방해 폭발시킵니다.", ChatColor.GRAY),
                                String.format("%s7초 동안 힘 2, 저항 2를 얻습니다.", ChatColor.GRAY)));
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

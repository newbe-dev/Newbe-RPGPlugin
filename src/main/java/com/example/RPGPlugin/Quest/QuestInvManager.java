package com.example.RPGPlugin.Quest;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class QuestInvManager implements Listener {

    private final ItemStack BLACK_PANEL;
    private final ItemStack NONE;
    private final ItemStack PAPER;

    public QuestInvManager() {
        NONE = new ItemStack(Material.AIR);

        PAPER = createItem(Material.PAPER, String.format("%s%s비어 있음", ChatColor.ITALIC, ChatColor.STRIKETHROUGH), Arrays.asList(String.format("%sNPC들에게 말을 걸어 퀘스트를 받을 수 있습니다 !", ChatColor.GRAY)));

        BLACK_PANEL = createItem(Material.BLACK_STAINED_GLASS_PANE, "", null);
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
    public void onOpenQuestInventoryEvent(OpenQuestInventoryEvent e) {
        if (e.player == e.entity) {
            e.player.openInventory(createMyInventory(e.player));
        } else {
            e.player.openInventory(createQuestInventory(e.player, e.entity));
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) { // 우클릭
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();
        if (entity instanceof Player)
            return;
        if (!SerializeManager.yml.contains(String.format("Plugin.Quest.%s", entity.getUniqueId())))
            return;

        Bukkit.getScheduler().runTaskLater(SerializeManager.getPlugin(), () -> {
            OpenQuestInventoryEvent event = new OpenQuestInventoryEvent(player, entity);
            Bukkit.getPluginManager().callEvent(event);
        }, 1L);
    }

    private List<Quest>[] getQuests(Player player, Entity entity) {
        List<Integer> integerList = SerializeManager.yml.getIntegerList(String.format("Plugin.Quest.%s", entity.getUniqueId()));
        ConfigurationSection playerNode = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Quest.%s", player.getUniqueId()));
        if (playerNode == null)
            SerializeManager.yml.createSection(String.format("Plugin.Quest.%s", player.getUniqueId()));

        List<Quest> acceptableQuestList = new ArrayList<>();
        List<Quest> currentQuestList = new ArrayList<>();

        Loop:
        for (Integer i : integerList) { // 주민의 모든 퀘스트에 대해 반복 -> i : 퀘스트 인덱스
            Quest q = QuestManager.loadQuest(i); // 퀘스트 -> q : 퀘스트 객체
            if (q == null) continue;

            if (playerNode != null) {
                if (playerNode.getKeys(false).contains(String.valueOf(i))) {
                    currentQuestList.add(q);
                    continue;
                }
                if (playerNode.getKeys(false).contains(String.valueOf(q.conditionQuest)) && SerializeManager.yml.contains(String.format("Plugin.finishedQuest.%s.%d", player.getUniqueId(), q.conditionQuest))) {
                    continue;
                }
                if (SerializeManager.yml.getBoolean(String.format("Plugin.finishedQuest.%s.%d", player.getUniqueId(), q.questId))) {
                    continue;
                }
            }

            if (q.conditionItemList != null) { // conditionItemList 조건 체크
                for (ItemStack itemStack : q.conditionItemList) {
                    if (player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                        continue Loop;
                    }
                }
            }

            acceptableQuestList.add(q);
        }

        List<Quest>[] lists = new List[50];
        lists[0] = acceptableQuestList;
        lists[1] = currentQuestList;
        return lists;
    }

    private Inventory createQuestInventory(Player player, Entity entity) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder) entity, 54, "퀘스트");

        // -- 초기화 --
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_PANEL);
        }
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                inventory.setItem((i * 9) + j + 1, NONE);
            }
        }
        // -- // --

        loadPage(player, inventory, 0, 1);
        return inventory;
    }

    private Inventory createMyInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "퀘스트");
        // -- 초기화 --
        Set<Quest> questSet = QuestManager.getPlayerQuestMap().get(player.getUniqueId());
        if (questSet == null || questSet.size() == 0) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, BLACK_PANEL);
            }
            inventory.setItem(22, PAPER);
        } else {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, BLACK_PANEL);
            }
            for (int i = 0; i < 16; i++) {
                int column = i / 4; // 0 1 2 3
                int row = i % 4;
                inventory.setItem(10 + 9 * column + row * 2, NONE);
            }

            Quest[] quests = questSet.toArray(new Quest[0]);
            for (int i = 0; i < questSet.size(); i++) {
                int column = i % 4; // 0 1 2 3

                ItemStack book = createItem(Material.ENCHANTED_BOOK, String.format("%s%s%s %s(#%d)", ChatColor.WHITE, ChatColor.BOLD, quests[i].questName, ChatColor.DARK_GRAY, quests[i].questId), Arrays.asList(String.format("%s%s%d / %d", ChatColor.WHITE, ChatColor.BOLD, quests[i].progress, quests[i].goal), "", quests[i].isMainQuest ? String.format("%s%s메인 퀘스트", ChatColor.BOLD, ChatColor.AQUA) : String.format("%s%s일반 퀘스트", ChatColor.BOLD, ChatColor.GRAY), "", String.format("%s%s%s", ChatColor.GRAY, ChatColor.BOLD, quests[i].questDescription)));
                inventory.setItem(10 + 9 * column + i * 2, book);
            }
        }
        // -- // --
        return inventory;
    }

    public void acceptQuest(Player player, Quest q) {
        if (q.conditionQuest != -1) {
            if (!SerializeManager.yml.getBoolean(String.format("Plugin.finishedQuest.%s.%d", player.getUniqueId(), q.questId))) {
                player.sendMessage(String.format("%s%s먼저 이전 퀘스트를 완료해야합니다 ! (%s)", ChatColor.BOLD, ChatColor.DARK_RED, QuestManager.loadQuest(q.conditionQuest).questName));
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 5, 1);
                return;
            }
        }
        if (q.conditionItemList != null) {
            for (ItemStack itemStack : q.conditionItemList) {
                if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    player.sendMessage(String.format("%s%s아이템이 필요합니다 !", ChatColor.BOLD, ChatColor.DARK_RED));
                    return;
                }
            }
        }
        if (player.getLevel() < q.conditionLevel) {
            player.sendMessage(String.format("%s%s레벨이 부족합니다 !", ChatColor.BOLD, ChatColor.DARK_RED));
            return;
        }

        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Quest.%s", player.getUniqueId()));
        node.set(String.valueOf(q.questId), 0);
        q.progress = 0;
        QuestManager.setPlayerQuest(player.getUniqueId(), q);

        player.sendMessage(String.format("%s%s[ %s ] 퀘스트를 수락하셨습니다!", ChatColor.BOLD, ChatColor.YELLOW, q.questName));
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

        player.closeInventory();
    }

    public void completeQuest(Player player, Quest q) {
        boolean complete;
        if(q.questType.equals(Quest.QuestType.COLLECT)) {
            for(ItemStack i : q.conditionItemList) {
                if(!(player.getInventory().containsAtLeast(i, i.getAmount()))) {
                    return;
                }
            }
            for(ItemStack i : q.conditionItemList) {
                player.getInventory().removeItem(i);
            }
            complete = true;
        }else {
            complete = (QuestManager.getPlayerProgress(player.getUniqueId(), q.questId) >= q.goal);
        }
        if(q.conditionQuest != -1) {
            complete = SerializeManager.yml.getBoolean(String.format("Plugin.finishedQuest.%s.%d", player.getUniqueId(), q.questId));
        }
        if (complete && player.getLevel() >= q.conditionLevel) {
            if (q.rewardItemList != null) {
                int empty = 0;
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    if (player.getInventory().getItem(i) == null) {
                        empty += 1;
                    }
                }
                if (empty < q.rewardItemList.size()) {
                    player.sendMessage(String.format("%s%s공간이 충분하지 않습니다 !", ChatColor.BOLD, ChatColor.DARK_RED));
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 50, 2);
                    return;
                }
                for (ItemStack itemStack : q.rewardItemList) {
                    player.getInventory().addItem(itemStack);
                }
            }
            if (q.rewardQuest != -1) {
                acceptQuest(player, QuestManager.loadQuest(q.rewardQuest));
            }
            if (q.rewardCommand != null) {
                player.performCommand(q.rewardCommand);
            }
            if (q.rewardLocation != null) {
                player.teleport(q.rewardLocation);
            }

            // Complete
            player.sendMessage(String.format("%s[ %s ]%s 퀘스트를 완료하셨습니다!", ChatColor.BOLD.toString() + (q.isMainQuest ? ChatColor.GOLD : ChatColor.GRAY), q.questName, ChatColor.RESET));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 50, 5);
            QuestManager.removePlayerQuest(player.getUniqueId(), q.questId);
            SerializeManager.yml.set(String.format("Plugin.finishedQuest.%s.%d", player.getUniqueId(), q.questId), true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   //인벤토리 클릭 시
        if (!(e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("퀘스트")))
            return;    //이 인벤토리를 클릭한게 아니라면 취소
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Entity entity = (Entity) e.getInventory().getHolder();
        if (entity == null)
            return;
        ItemStack clickedItem = e.getCurrentItem(); //클릭된 아이템
        if (player != e.getInventory().getHolder()) { // 퀘스트
            if (clickedItem == null)
                return;

            if (clickedItem.getType().equals(Material.ENCHANTED_BOOK) && e.isLeftClick()) {
                for (Quest q : getQuests(player, entity)[0]) {
                    if (clickedItem.getItemMeta().getDisplayName().contains(q.questName)) {
                        acceptQuest(player, q);
                    }
                }
                for (Quest q : getQuests(player, entity)[1]) {
                    if (clickedItem.getItemMeta().getDisplayName().contains(q.questName)) {
                        completeQuest(player, q);
                    }
                }
            } else if (clickedItem.getType().equals(Material.LIME_DYE)) {
                if (e.isLeftClick()) {
                    nextPage(player, e.getInventory());
                } else if (e.isRightClick()) {
                    previousPage(player, e.getInventory());
                }
            }
        } else { // quest
            if (e.isRightClick() && e.isShiftClick()) {
                int questId = Integer.parseInt(String.valueOf(clickedItem.getItemMeta().getDisplayName().split("#")[1].charAt(0)));
                QuestManager.removePlayerQuest(player.getUniqueId(), questId);
                player.sendMessage(String.format("%s%s[ %s ] 퀘스트를 포기했습니다.", ChatColor.BOLD, ChatColor.AQUA, SerializeManager.yml.getString(String.format("Plugin.Quest.%d.questName", questId))));

                player.openInventory(createMyInventory(player));
            }
        }
    }

    private void previousPage(Player player, Inventory inventory) {
        ItemStack dye = inventory.getItem(40);
        int currentPage = Integer.parseInt(dye.getItemMeta().getDisplayName().split(" / ")[0].replace("§l", ""));
        int maxPage = Integer.parseInt(dye.getItemMeta().getDisplayName().split(" / ")[1]);

        if (currentPage == 1) {
            return;
        }
        currentPage -= 1;
        loadPage(player, inventory, currentPage - 1, maxPage);
    }

    private void nextPage(Player player, Inventory inventory) {
        ItemStack dye = inventory.getItem(40);
        int currentPage = Integer.parseInt(dye.getItemMeta().getDisplayName().split(" / ")[0].replace("§l", ""));
        int maxPage = Integer.parseInt(dye.getItemMeta().getDisplayName().split(" / ")[1]);

        if (currentPage == maxPage) {
            return;
        }
        loadPage(player, inventory, currentPage, maxPage);
    }

    private void loadPage(Player player, Inventory inventory, int page, int maxPage) {
        List<Quest>[] quests = getQuests(player, (Entity) inventory.getHolder());
        List<Quest> list = new ArrayList<>();
        list.addAll(quests[1]);
        list.addAll(quests[0]);
        if (list.size() <= page) {
            ItemStack paper = createItem(Material.PAPER, String.format("%s%s(진행 중 / 수락 가능)한 퀘스트가 존재하지 않습니다.", ChatColor.GRAY, ChatColor.BOLD), null);
            inventory.setItem(22, paper);
        } else {
            Quest q = list.get(page);
            int progress = SerializeManager.yml.getInt(String.format("Plugin.Quest.%s.%d", player.getUniqueId(), q.questId));

            ItemStack book;
            if (quests[1].contains(q)) { // 현재 진행중인 퀘스트라면
                book = createItem(Material.ENCHANTED_BOOK, String.format("%s%s%s %s(진행 중)", ChatColor.WHITE, ChatColor.BOLD, q.questName, ChatColor.GRAY), Arrays.asList(String.format("%s%s%d / %d", ChatColor.BOLD, ChatColor.WHITE, progress, q.goal), "", q.isMainQuest ? String.format("%s메인 퀘스트", ChatColor.AQUA) : String.format("%s일반 퀘스트", ChatColor.GRAY), "", String.format("%s%s", ChatColor.GRAY, q.questDescription)));
            } else {
                book = createItem(Material.ENCHANTED_BOOK, String.format("%s%s%s", ChatColor.WHITE, ChatColor.BOLD, q.questName), Arrays.asList("", q.isMainQuest ? String.format("%s메인 퀘스트", ChatColor.AQUA) : String.format("%s일반 퀘스트", ChatColor.GRAY), "", String.format("%s%s", ChatColor.GRAY, q.questDescription)));
            }
            inventory.setItem(22, book);

            ItemStack dye = createItem(Material.LIME_DYE, String.format("%s%s%d / %d",ChatColor.WHITE, ChatColor.BOLD, page + 1, maxPage), null);
            inventory.setItem(40, dye);

            q.progress = progress;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) { //인벤토리 드래그 시
        if (e.getInventory().contains(BLACK_PANEL) && e.getView().getTitle().equals("퀘스트")) {  //만약 드래그된 인벤토리가 이 인벤토리라면
            e.setCancelled(true);   //위치 변경 취소
        }
    }
}

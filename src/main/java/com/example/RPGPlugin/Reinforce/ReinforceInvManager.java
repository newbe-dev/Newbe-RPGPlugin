package com.example.RPGPlugin.Reinforce;

import com.example.RPGPlugin.IntTagType;
import com.example.RPGPlugin.SerializeManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReinforceInvManager implements Listener {
    private final ItemStack PINK_PANEL;
    private final ItemStack GRAY_PANEL;
    private final ConfigurationSection REINFORCE_NODE;

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
        if (SerializeManager.yml.contains("Reinforce.table")) {
            REINFORCE_NODE = SerializeManager.yml.getConfigurationSection("Reinforce.table");
        } else {
            REINFORCE_NODE = SerializeManager.yml.createSection("Reinforce.table");
        }
    }

    @EventHandler
    public void onOpenReinforceInventory(OpenReinforceInventoryEvent e) {
        e.player.openInventory(createInventory());
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, 45, "??????");
        inventory.clear();

        ItemStack anvil = createItem(Material.ANVIL, String.format("%s??????", ChatColor.BOLD), Arrays.asList("-", String.format("%s-", ChatColor.BLUE), String.format("%s-", ChatColor.GRAY), String.format("%s-", ChatColor.DARK_GRAY)), true);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, GRAY_PANEL);
        }
        inventory.setItem(11, new ItemStack(Material.AIR));
        inventory.setItem(22, anvil);
        inventory.setItem(24, PINK_PANEL);
        inventory.setItem(29, new ItemStack(Material.AIR));

        reloadPage(inventory);
        // -- // --
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {   // ???????????? ?????? ???
        Inventory inventory = e.getInventory();
        if (!(inventory.contains(GRAY_PANEL) && e.getView().getTitle().equals("??????")))
            return;    // ??? ??????????????? ???????????? ???????????? ??????
        ItemStack clickedItem = e.getCurrentItem(); // ????????? ?????????
        if (clickedItem == null) return;
        if (clickedItem.equals(PINK_PANEL) || clickedItem.equals(GRAY_PANEL) || clickedItem.getType().equals(Material.ANVIL)) {
            e.setCancelled(true);   //?????? ?????? ??????
        }

        reloadPage(inventory);
        if (e.isLeftClick() && clickedItem.getType().equals(Material.ANVIL)) {
            reinforce((Player) e.getWhoClicked(), inventory);
        }
    }

    @EventHandler
    public void onInteractInventory(InventoryInteractEvent e) {
        reloadPage(e.getInventory());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Bukkit.getPlayer("NewbieDev").sendMessage("drag");
        if (e.getInventory().contains(GRAY_PANEL) && e.getView().getTitle().equals("??????")) {  //?????? ???????????? ??????????????? ??? ??????????????????
            reloadPage(e.getInventory());
            if (e.getOldCursor().equals(PINK_PANEL) || e.getOldCursor().equals(GRAY_PANEL) || e.getOldCursor().getType().equals(Material.ANVIL)) {
                e.setCancelled(true);   //?????? ?????? ??????
            } else if (e.getCursor() != null && (e.getCursor().equals(PINK_PANEL) || e.getCursor().equals(GRAY_PANEL) || e.getCursor().getType().equals(Material.ANVIL))) {
                e.setCancelled(true);   //?????? ?????? ??????
            }
        }
    }

    private void reloadPage(Inventory inventory) {
        ItemStack i1 = inventory.getItem(11), i2 = inventory.getItem(29);
        String noItem = String.format("%s- ????????? ???????????? ???????????????.", ChatColor.DARK_GRAY);
        String inCorrectItem = String.format("%s- ????????? ??? ?????? ??????????????????.", ChatColor.DARK_GRAY);
        String noSpell = String.format("%s- ?????? ???????????? ???????????????.", ChatColor.DARK_GRAY);
        String inCorrectSpell = String.format("%s- ???????????? ?????? ?????? ????????? ???????????????.", ChatColor.DARK_GRAY);
        List<String> stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(String.format("%s-", ChatColor.WHITE), String.format("%s??????: -", ChatColor.GREEN), String.format("%s?????? ??????: -", ChatColor.GRAY), String.format("%s?????? ??????: -", ChatColor.RED), String.format("%s??????: -", ChatColor.DARK_GRAY), ""));

        if (i1 == null) {
            stringList.add(noItem);
        } else {
            if (!REINFORCE_NODE.contains(ChatColor.stripColor(i1.getItemMeta().getDisplayName()))) { // ?????? ??????
                stringList.add(inCorrectItem);
            }
        }
        if (i2 == null) {
            stringList.add(noSpell);
        } else {
            String name = ChatColor.stripColor(i2.getItemMeta().getDisplayName());
            if (!(name.equals("?????? ?????????") || name.equals("?????? ?????????") || name.equals("?????? ?????????"))) { // ?????? ?????????
                stringList.add(inCorrectSpell);
            }
        }

        if (stringList.size() > 6) {
            inventory.setItem(22, createItem(Material.ANVIL, String.format("%s??????", ChatColor.BOLD), stringList, true));
        } else {
            int magnitude = 0;
            String name = ChatColor.stripColor(i2.getItemMeta().getDisplayName());
            switch (name) {
                case "?????? ?????????":
                    magnitude = 1;
                    break;
                case "?????? ?????????":
                    magnitude = 2;
                    break;
                case "?????? ?????????":
                    magnitude = 3;
                    break;
            }
            List<Float> table = REINFORCE_NODE.getFloatList(String.format("%s.%d", ChatColor.stripColor(i1.getItemMeta().getDisplayName()), magnitude));
            float success = table.get(0), fail = table.get(1), degrade = table.get(2), destroy = table.get(3); // ?????? ?????? ?????? ??????
            inventory.setItem(22, createItem(Material.ANVIL, String.format("%s??????", ChatColor.BOLD), Arrays.asList(String.format("%s-", ChatColor.WHITE), String.format("%s??????: %.3f", ChatColor.GREEN, success), String.format("%s?????? ??????: %.3f", ChatColor.GRAY, fail), String.format("%s?????? ??????: %.3f", ChatColor.RED, degrade), String.format("%s??????: %.3f", ChatColor.DARK_GRAY, destroy)), true));
        }
    }

    private void reinforce(Player player, Inventory inventory) {
        ItemStack i1 = inventory.getItem(11), i2 = inventory.getItem(29);
        if (i1 == null || i2 == null) {
            player.sendMessage(String.format("%s????????? ??? ???????????? !", ChatColor.RED));
        } else {
            int magnitude = 0;
            String name = ChatColor.stripColor(i2.getItemMeta().getDisplayName());
            switch (name) {
                case "?????? ?????????":
                    magnitude = 1;
                    break;
                case "?????? ?????????":
                    magnitude = 2;
                    break;
                case "?????? ?????????":
                    magnitude = 3;
                    break;
            }
            if (!REINFORCE_NODE.contains(ChatColor.stripColor(i1.getItemMeta().getDisplayName())) || magnitude == 0) {
                player.sendMessage(String.format("%s%s????????? ??? ???????????? !", ChatColor.BOLD, ChatColor.RED));
            } else {
                List<Float> table = REINFORCE_NODE.getFloatList(String.format("%s.%d", ChatColor.stripColor(i1.getItemMeta().getDisplayName()), magnitude - 1));
                float success = table.get(0), fail = table.get(1), degrade = table.get(2); // ?????? ?????? ?????? ??????

                Random random = new Random();
                float r = random.nextFloat();

                if (r < success) {
                    player.sendMessage(String.format("%s%s????????? ?????????????????? !", ChatColor.BOLD, ChatColor.GREEN));
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 50, 1);

                    setReinforceLevel(i1, getReinforceLevel(i1) + 1);

                } else if (r < success + fail) {
                    player.sendMessage(String.format("%s%s????????? ?????????????????? ! ( ????????? ??????????????? )", ChatColor.BOLD, ChatColor.RED));
                    player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 50, 1.3f);

                } else if (r < success + fail + degrade) {
                    player.sendMessage(String.format("%s%s????????? ?????????????????? ! ( ????????? ??????????????? )", ChatColor.BOLD, ChatColor.GRAY));
                    player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 50, 0.7f);

                    setReinforceLevel(i1, getReinforceLevel(i1) - 1);
                } else {
                    player.sendMessage(String.format("%s%s????????? ????????????????????? !", ChatColor.BOLD, ChatColor.DARK_RED));
                    player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 50, 1);

                    i1 = null;
                }

                if(i1 != null) {
                    changeLore(i1, getReinforceLevel(i1));
                }
                inventory.setItem(11, i1);

                inventory.setItem(29, null);
            }
        }
        reloadPage(inventory);
    }

    private void changeLore(ItemStack itemStack, int newMagnitude) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasLore()) {
            itemMeta.setLore(Arrays.asList(String.format("%s%d", ChatColor.WHITE, newMagnitude)));
        }
        if(newMagnitude < 5) {
            itemMeta.getLore().set(0, String.format("%s%d", ChatColor.WHITE, newMagnitude));
        }
        else if(newMagnitude < 10) {
            itemMeta.getLore().set(0, String.format("%s%d", ChatColor.AQUA, newMagnitude));
        }
        else if(newMagnitude < 15) {
            itemMeta.getLore().set(0, String.format("%s%d", ChatColor.DARK_PURPLE, newMagnitude));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private int getReinforceLevel(ItemStack itemStack) {
        PersistentDataType<byte[], Integer> intTagType = new IntTagType();
        NamespacedKey key = new NamespacedKey(SerializeManager.getPlugin(), "reinforce-level");
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer tagContainer = itemMeta.getPersistentDataContainer();

        if (tagContainer.has(key, intTagType)) {
            return tagContainer.get(key, intTagType);
        } else {
            return 0;
        }
    }

    private void setReinforceLevel(ItemStack itemStack, int value) {
        if (itemStack == null) return;
        NamespacedKey key = new NamespacedKey(SerializeManager.getPlugin(), "reinforce-level");
        PersistentDataType<byte[], Integer> intTagType = new IntTagType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(key, intTagType, value);
        }
        itemStack.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        Player player = e.getPlayer();
        if (entity instanceof Player) return;
        if (!SerializeManager.yml.contains(String.format("Plugin.Reinforce.Villager.%s", entity.getUniqueId())))
            return;
        OpenReinforceInventoryEvent event = new OpenReinforceInventoryEvent(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Inventory inventory = e.getInventory();
        if (e.getInventory().contains(GRAY_PANEL) && e.getView().getTitle().equals("??????")) {
            ItemStack i1 = inventory.getItem(11), i2 = inventory.getItem(29);
            if (i1 != null) {
                if (player.getInventory().addItem(i1).size() != 0) {
                    player.getWorld().dropItem(player.getLocation(), i1);
                }
            }
            if (i2 != null) {
                if (player.getInventory().addItem(i2).size() != 0) {
                    player.getWorld().dropItem(player.getLocation(), i2);
                }
            }
        }
    }
}
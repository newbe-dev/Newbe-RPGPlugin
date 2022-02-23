package com.example.RPGPlugin.Shop;

import com.example.RPGPlugin.SerializeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopInfo {
    public static ArrayList<ArrayList<tradeInfo>> shopInfoArrayList = new ArrayList<>();

    public static void InitShopInfo() {
        ArrayList<tradeInfo> tradeInfoArrayList = new ArrayList<>();
        List<String> lore = new ArrayList<>();

        ItemStack sell = new ItemStack(Material.WOODEN_SWORD, 1);
        ItemMeta meta = sell.getItemMeta();
        ItemStack price = new ItemStack(Material.EMERALD, 3);

        meta.setDisplayName(String.format("%s%s평범한 칼", ChatColor.WHITE, ChatColor.BOLD));
        lore.add(String.format("%sCommon", ChatColor.WHITE));
        lore.add(String.format("%sPrice: 에메랄드 3개", ChatColor.GRAY));
        meta.setLore(lore);
        sell.setItemMeta(meta);
        tradeInfoArrayList.add(new tradeInfo(sell, price));

        lore.clear();
        sell = new ItemStack(Material.WOODEN_AXE, 1);
        price = new ItemStack(Material.EMERALD, 3);
        meta = sell.getItemMeta();
        meta.setDisplayName(String.format("%s%s평범한 도끼", ChatColor.WHITE, ChatColor.BOLD));
        lore.add(String.format("%sCommon", ChatColor.WHITE));
        lore.add(String.format("%sPrice: 에메랄드 3개", ChatColor.GRAY));
        meta.setLore(lore);
        sell.setItemMeta(meta);
        tradeInfoArrayList.add(new tradeInfo(sell, price));

        shopInfoArrayList.add((ArrayList<tradeInfo>) tradeInfoArrayList.clone());

        tradeInfoArrayList.clear();
        lore.clear();
        sell = new ItemStack(Material.STONE_SWORD, 1);
        price = new ItemStack(Material.EMERALD, 10);
        meta = sell.getItemMeta();
        meta.setDisplayName(String.format("%s%s초심자의 칼", ChatColor.BLUE, ChatColor.BOLD));
        lore.add(String.format("%sRare", ChatColor.DARK_BLUE));
        lore.add(String.format("%sPrice: 에메랄드 10개", ChatColor.GRAY));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        sell.setItemMeta(meta);
        tradeInfoArrayList.add(new tradeInfo(sell, price));

        lore.clear();
        sell = new ItemStack(Material.STONE_AXE, 1);
        price = new ItemStack(Material.EMERALD, 10);
        meta = sell.getItemMeta();
        meta.setDisplayName(String.format("%s%s초심자의 도끼", ChatColor.BLUE, ChatColor.BOLD));
        lore.add(String.format("%sRare", ChatColor.DARK_BLUE));
        lore.add(String.format("%sPrice: 에메랄드 10개", ChatColor.GRAY));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        sell.setItemMeta(meta);
        tradeInfoArrayList.add(new tradeInfo(sell, price));

        shopInfoArrayList.add((ArrayList<tradeInfo>) tradeInfoArrayList.clone());
    }

    public static void conductSerializing() {
        for (int i = 0; i < shopInfoArrayList.size(); i++) {
            for (int j = 0; j < shopInfoArrayList.get(i).size(); j++) {
                SerializeManager.yml.set(String.format("Plugin.shop%d.trade%d.buy", i, j), shopInfoArrayList.get(i).get(j).buyItemStack);
                SerializeManager.yml.set(String.format("Plugin.shop%d.trade%d.price", i, j), shopInfoArrayList.get(i).get(j).priceItemStack);
            }
        }
        SerializeManager.saveFile();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "상점 목록을 성공적으로 직렬화했습니다!");
    }

    public static void conductDeserializing() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "상점 목록을 불러오는 중입니다 ..");
        for (int i = 0; SerializeManager.yml.contains(String.format("Plugin.shop%d", i)); i++) {
            ArrayList<tradeInfo> temp = new ArrayList<>();
            for (int j = 0; SerializeManager.yml.contains(String.format("Plugin.shop%d.trade%d", i, j)); j++) {
                tradeInfo t = new tradeInfo(SerializeManager.yml.getItemStack(String.format("Plugin.shop%d.trade%d.buy", i, j)), SerializeManager.yml.getItemStack(String.format("Plugin.shop%d.trade%d.price", i, j)));
                temp.add(t);
            }
            shopInfoArrayList.add(temp);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "상점 목록을 성공적으로 역직렬화했습니다!");
    }
}

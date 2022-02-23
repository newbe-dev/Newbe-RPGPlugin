package com.example.RPGPlugin.Shop;
import org.bukkit.inventory.ItemStack;

public class tradeInfo {
    public final ItemStack buyItemStack;
    public final ItemStack priceItemStack;

    public tradeInfo(ItemStack buyItemStack, ItemStack priceItemStack) {
        this.buyItemStack = buyItemStack;
        this.priceItemStack = priceItemStack;
    }
}

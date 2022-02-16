package com.example.RPGPlugin;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class tradeInfo implements Serializable {

    static final long serialVersionUID = 42L;
    public final ItemStack buyItemStack;
    public final ItemStack priceItemStack;

    public tradeInfo(ItemStack buyItemStack, ItemStack priceItemStack) {
        this.buyItemStack = buyItemStack;
        this.priceItemStack = priceItemStack;
    }
}

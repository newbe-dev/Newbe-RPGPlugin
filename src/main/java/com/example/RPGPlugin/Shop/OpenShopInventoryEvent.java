package com.example.RPGPlugin.Shop;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenShopInventoryEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public Player player;
    public int shopNo; // 상점 번호

    public OpenShopInventoryEvent(Player player, int shopNo) {
        this.player = player;
        this.shopNo = shopNo;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

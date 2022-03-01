package com.example.RPGPlugin.Quest;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenQuestInventoryEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public Player player;
    public Entity entity;

    public OpenQuestInventoryEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

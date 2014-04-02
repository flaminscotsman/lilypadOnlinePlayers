package me.flamin.lilypadOnlinePlayers.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HubPlayerQuitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() { return handlers; }

    private final String name;

    public HubPlayerQuitEvent(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the leaving player
     *
     * @return name of the user
     */
    public String getName() { return this.name; }
}

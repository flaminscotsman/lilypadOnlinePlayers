package me.flamin.lilypadOnlinePlayers.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;

import java.util.UUID;

public class HubPlayerVisibilityChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() { return handlers; }

    private final String name;
    private final UUID uuid;
    private final boolean vanishing;

    public HubPlayerVisibilityChangeEvent(String name, boolean vanishing) {
        this.name = name;
        this.uuid = null;
        this.vanishing = vanishing;
    }

    public HubPlayerVisibilityChangeEvent(String name, UUID uuid, boolean vanishing) {
        this.name = name;
        this.uuid = uuid;
        this.vanishing = vanishing;
    }

    public HubPlayerVisibilityChangeEvent(PlayerEntry entry) {
        this.name = entry.getName();
        this.uuid = entry.getUUID();
        this.vanishing = entry.getVisible();
    }

    /**
     * Gets the name of the player changing visibility
     *
     * @return name of the user changing visibility
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the UUID of the joining player
     *
     * @return UUID of the user
     */
    public UUID getUUID() { return this.uuid; }

    /**
     * Gets the new visibility of the player
     *
     * @return true if vanishing, false is revealing
     */
    public boolean isVanishing() {
        return this.vanishing;
    }

    /**
     * Gets if the player will be visible
     *
     * @return true if vanishing, false is revealing
     */
    public boolean isVisible() {
        return !this.vanishing;
    }
}

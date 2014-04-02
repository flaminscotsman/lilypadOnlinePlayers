package me.flamin.lilypadOnlinePlayers.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;

public class HubPlayerVisibilityChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() { return handlers; }

    private final String name;
    private final boolean vanishing;

    public HubPlayerVisibilityChangeEvent(String name, boolean vanishing) {
        this.name = name;
        this.vanishing = vanishing;
    }

    public HubPlayerVisibilityChangeEvent(PlayerEntry entry) {
        this.name = entry.getName();
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

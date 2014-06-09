package me.flamin.lilypadOnlinePlayers.events;

import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class HubPlayerQuitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() { return handlers; }

    private final String name;
    private final UUID uuid;

    @Deprecated
    public HubPlayerQuitEvent(String name) {
        this.name = name;
        this.uuid = null;
    }

    public HubPlayerQuitEvent(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    public HubPlayerQuitEvent(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public HubPlayerQuitEvent(PlayerEntry entry) {
        this.name = entry.getName();
        this.uuid = entry.getUUID();
    }

    /**
     * Gets the name of the leaving player
     *
     * @return name of the user
     */
    public String getName() { return this.name; }

    /**
     * Gets the UUID of the joining player
     *
     * @return UUID of the user
     */
    public UUID getUUID() { return this.uuid; }
}

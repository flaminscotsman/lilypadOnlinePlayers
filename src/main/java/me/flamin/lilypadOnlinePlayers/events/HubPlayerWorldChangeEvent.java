package me.flamin.lilypadOnlinePlayers.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;

public class HubPlayerWorldChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

    private final String name;
    private final String server;
    private final String world;

    public HubPlayerWorldChangeEvent(String name, String world, String server) {
        this.name = name;
        this.server = server;
        this.world = world;
    }

    public HubPlayerWorldChangeEvent(PlayerEntry entry) {
        this.name = entry.getName();
        this.server = entry.getServer();
        this.world = entry.getWorld();
    }

    /**
     * Gets the name of the player changing worlds
     *
     * @return name of the user
     */
    public String getName() { return this.name; }

    /**
     * Gets the server containing the player
     *
     * @return the player changing visibility
     */
    public String getServer() { return this.server; }

    /**
     * Gets the new world of the player
     *
     * @return the player changing visibility
     */
    public String getWorld() { return this.world; }
}

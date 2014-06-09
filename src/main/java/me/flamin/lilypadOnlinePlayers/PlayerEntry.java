package me.flamin.lilypadOnlinePlayers;

import java.io.Serializable;
import java.util.UUID;

// Java handles generating the serialVersionUID, so we don't need to assign one - hide this warning.
@SuppressWarnings("serial")
public class PlayerEntry implements Serializable {
    public PlayerEntry() {
    }

    // Properties.
    private String name;
    private String server;
    private String world;
    private UUID uuid;
    private boolean visible;

    // Getters.
    public String getName() { return name; }
    public String getServer() { return server; }
    public boolean getVisible() { return visible; }
    public String getWorld() { return world; }
    public UUID getUUID() { return uuid; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setServer(String server) { this.server = server; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setWorld(String world) { this.world = world; }
    public void setUUID(String uuid) {this.uuid = UUID.fromString(uuid); }
    public void setUUID(UUID uuid) { this.uuid = uuid; }

}

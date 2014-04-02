package me.flamin.lilypadOnlinePlayers;

import java.io.Serializable;

public class PlayerEntry implements Serializable {
    // Properties.
    private String name;
    private String server;
    private String world;
    private boolean visible;

    // Getters.
    public String getName() { return name; }
    public String getServer() { return server; }
    public boolean getVisible() { return visible; }
    public String getWorld() { return world; }

    public void setName(String name) { this.name = name; }
    public void setServer(String server) { this.server = server; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setWorld(String world) { this.world = world; }
}

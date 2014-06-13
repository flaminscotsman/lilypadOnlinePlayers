package me.flamin.lilypadOnlinePlayers.packets;

import lilypad.client.connect.api.event.MessageEvent;
import me.flamin.lilypadOnlinePlayers.Actions;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import org.bukkit.entity.Player;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 *
 */
public class Packet_ADD extends AbstractPacket {
    // 6  | name | uuid | world | visibility
    public static final String packetID = Actions.ADD.getIDString();
    public static final int packetLength = 5;

    private String playerName;
    private UUID playerUUID;
    private String playerServer;
    private String playerWorld;
    private boolean visible;

    public void encode(Player player, LilypadOnlinePlayersHandler handler) {
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        this.playerServer = handler.getServerName();
        this.playerWorld = player.getWorld().getName();
        this.visible = handler.isVisible(player);
    }

    public void encode(PlayerEntry player) {
        this.playerName = player.getName();
        this.playerUUID = player.getUUID();
        this.playerServer = player.getServer();
        this.playerWorld = player.getWorld();
        this.visible = player.getVisible();
    }

    public void encode(String playerName, UUID playerUUID, String server, String world, boolean visible) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.playerServer = server;
        this.playerWorld = world;
        this.visible = visible;
    }

    public void decode(String[] packet, String sender) {
        if (!packet[0].equals(packetID) || packet.length != packetLength)
            throw new IllegalArgumentException("Invalid packet!");

        this.playerName = packet[1];
        this.playerUUID = UUID.fromString(packet[2]);
        this.playerServer = sender;
        this.playerWorld = packet[3];
        this.visible = Boolean.valueOf(packet[4]);
    }

    @Override
    public void decode(MessageEvent event) {
        String packet[];
        try {
            packet = event.getMessageAsString().split("\\x00");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid packet!");
        }

        if (!packet[0].equals(packetID) || packet.length != packetLength)
            throw new IllegalArgumentException("Invalid packet!");

        this.playerName = packet[1];
        this.playerUUID = UUID.fromString(packet[2]);
        this.playerServer = event.getSender();
        this.playerWorld = packet[3];
        this.visible = Boolean.valueOf(packet[4]);
    }

    @Override
    public String getPlayer() {
        return playerName;
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public String getServer() {
        return playerServer;
    }

    public String getWorld() { return playerWorld; }

    public boolean getVisibility() { return visible; }

    @Override
    public String toString() {
        return joiner.join(packetID, playerName, playerUUID, playerWorld, ((Boolean)visible).toString());
    }

    @Override
    public PlayerEntry updatePlayerEntry(PlayerEntry player) {
        player.setName(this.playerName);
        player.setUUID(this.playerUUID);
        player.setServer(this.playerServer);
        player.setWorld(this.playerWorld);
        player.setVisible(this.visible);

        return player;
    }
}

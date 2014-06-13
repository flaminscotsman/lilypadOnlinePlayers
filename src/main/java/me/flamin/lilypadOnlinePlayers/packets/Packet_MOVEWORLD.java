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
public class Packet_MOVEWORLD extends AbstractPacket {
    // 8  | name | uuid | world
    public static final String packetID = Actions.MOVEWORLD.getIDString();
    public static final int packetLength = 4;

    private String playerName;
    private UUID playerUUID;
    private String playerServer;
    private String playerWorld;

    public void encode(Player player, LilypadOnlinePlayersHandler handler) {
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        this.playerServer = handler.getServerName();
        this.playerWorld = player.getWorld().getName();
    }

    public void encode(PlayerEntry player) {
        this.playerName = player.getName();
        this.playerUUID = player.getUUID();
        this.playerServer = player.getServer();
        this.playerWorld = player.getWorld();
    }

    public void encode(String playerName, UUID playerUUID, String server, String world, boolean visible) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.playerServer = server;
        this.playerWorld = world;
    }

    @Override
    public void decode(String[] packet, String sender) {
        if (!packet[0].equals(packetID) || packet.length != packetLength)
            throw new IllegalArgumentException("Invalid packet!");

        this.playerName = packet[1];
        this.playerUUID = UUID.fromString(packet[2]);
        this.playerServer = sender;
        this.playerWorld = packet[3];
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

    public String getWorld() {
        return playerWorld;
    }

    @Override
    public boolean getVisibility() {
        return true;
    }

    @Override
    public String toString() {
        return joiner.join(packetID, playerName, playerUUID, playerWorld);
    }

    @Override
    public PlayerEntry updatePlayerEntry(PlayerEntry player) {
        player.setName(this.playerName);
        player.setUUID(this.playerUUID);
        player.setServer(this.playerServer);
        player.setWorld(this.playerWorld);

        return player;
    }
}

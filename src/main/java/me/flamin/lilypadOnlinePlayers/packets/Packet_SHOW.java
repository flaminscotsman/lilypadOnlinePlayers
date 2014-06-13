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
public class Packet_SHOW extends AbstractPacket {
    // 10 | name | uuid
    public static final String packetID = Actions.SHOW.getIDString();
    public static final int packetLength = 3;

    private String playerName;
    private UUID playerUUID;
    private String playerServer;

    public void encode(Player player, LilypadOnlinePlayersHandler handler) {
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        this.playerServer = handler.getServerName();
    }

    public void encode(PlayerEntry player) {
        this.playerName = player.getName();
        this.playerUUID = player.getUUID();
        this.playerServer = player.getServer();
    }

    public void encode(String playerName, UUID playerUUID, String server, String world, boolean visible) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.playerServer = server;
    }

    @Override
    public void decode(String[] packet, String sender) {
        if (!packet[0].equals(packetID) || packet.length != packetLength)
            throw new IllegalArgumentException("Invalid packet!");

        this.playerName = packet[1];
        this.playerUUID = UUID.fromString(packet[2]);
        this.playerServer = sender;
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

    @Override
    public String getWorld() {
        return null;
    }

    @Override
    public boolean getVisibility() {
        return true;
    }

    @Override
    public String toString() {
        return joiner.join(packetID, playerName, playerUUID);
    }

    @Override
    public PlayerEntry updatePlayerEntry(PlayerEntry player) {
        player.setName(this.playerName);
        player.setUUID(this.playerUUID);
        player.setServer(this.playerServer);
        player.setVisible(true);

        return player;
    }
}

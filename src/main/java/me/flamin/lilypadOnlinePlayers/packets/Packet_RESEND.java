package me.flamin.lilypadOnlinePlayers.packets;

import lilypad.client.connect.api.event.MessageEvent;
import me.flamin.lilypadOnlinePlayers.Actions;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 *
 */
public class Packet_RESEND extends AbstractPacket {
    // 5
    public static final String packetID = Actions.RESEND.getIDString();
    public static final int packetLength = 1;

    public void encode(Player player, LilypadOnlinePlayersHandler handler) {
    }

    public void encode(PlayerEntry player) {
    }

    public void encode(String playerName, UUID playerUUID, String server, String world, boolean visible) {
    }

    @Override
    public void decode(String[] packet, String sender) {
    }

    @Override
    public void decode(MessageEvent event) {
    }

    @Override
    public String getPlayer() {
        return null;
    }

    @Override
    public UUID getPlayerUUID() {
        return null;
    }

    @Override
    public String getServer() {
        return null;
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
        return packetID;
    }

    @Override
    public PlayerEntry updatePlayerEntry(PlayerEntry player) {
        return player;
    }
}

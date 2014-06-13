package me.flamin.lilypadOnlinePlayers.packets;

import com.google.common.base.Joiner;
import lilypad.client.connect.api.event.MessageEvent;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayers;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AbstractPacket {
    protected static final String channelname = LilypadOnlinePlayers.channelname;
    protected static final char delimiter = '\0';
    protected static final Joiner joiner = Joiner.on(delimiter).skipNulls();

    public abstract void encode(Player player, LilypadOnlinePlayersHandler handler);
    public abstract void encode(PlayerEntry player);
    public abstract void encode(String playerName, UUID playerUUID, String server, String world, boolean visible);

    public abstract void decode(String[] packet, String sender);
    public abstract void decode(MessageEvent event);

    public abstract String getPlayer();
    public abstract UUID getPlayerUUID();
    public abstract String getServer();
    public abstract String getWorld();
    public abstract boolean getVisibility();

    public abstract String toString();
    public abstract PlayerEntry updatePlayerEntry(PlayerEntry player);
}

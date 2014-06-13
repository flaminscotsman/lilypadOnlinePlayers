package me.flamin.lilypadOnlinePlayers;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

public class LilypadOnlinePlayersHandler {
    private final LilypadOnlinePlayers plugin;

    public LilypadOnlinePlayersHandler(LilypadOnlinePlayers plugin) {
        this.plugin = plugin;
    }

    public LilypadOnlinePlayers getPlugin() { return plugin; }

    public boolean isLogged(Verbosity.VerbosityLevels level) { return plugin.verbosity.contains(level); }

    public boolean isVisible(Player player) { return plugin.isVisible(player); }

    public Map<String, PlayerEntry> getPlayers() { return Collections.unmodifiableMap(plugin.onlinePlayers); }

    public void addPlayer(final String player, final PlayerEntry entry) { plugin.onlinePlayers.put(player, entry); }

    public void removePlayer(final String player) { plugin.onlinePlayers.remove(player); }

    public boolean containsPlayer(final String player) { return plugin.onlinePlayers.containsKey(player); }

    public PlayerEntry getPlayer(String player) { return plugin.onlinePlayers.get(player); }

    public String getServerName() { return plugin.servername; }

    public void expirePlayer(final String player) { plugin.expiredPlayers.add(player); }

    public void unExpirePlayer(final String player) { plugin.expiredPlayers.remove(player); }

    public boolean isPlayerExpired(final String player) { return plugin.expiredPlayers.contains(player); }
}

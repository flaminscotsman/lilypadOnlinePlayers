package me.flamin.lilypadOnlinePlayers.listeners;

import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import me.flamin.lilypadOnlinePlayers.Verbosity;
import me.flamin.lilypadOnlinePlayers.packets.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import me.flamin.lilypadOnlinePlayers.Actions;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayers;

public class PlayerListener implements Listener {
    private static final String channelname = "lilypadPlayers";
    private final LilypadOnlinePlayersHandler handler;
    private final LilypadOnlinePlayers plugin;

    public PlayerListener(LilypadOnlinePlayersHandler handler) {
        this.handler = handler;
        this.plugin = handler.getPlugin();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED))
            return; // We do not wish to track blocked logins

        Player player = event.getPlayer();

        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
            plugin.getLogger().severe(String.format(
                    "[%1$s] - Local player %2$s %3$s joined this server (%4$s).",
                    plugin.getDescription().getName(),
                    (
                            handler.isLogged(Verbosity.VerbosityLevels.SHOW_UUIDS) ?
                                    player.getName() + " [" + player.getUniqueId().toString() + "]" :
                                    player.getName()
                    ),
                    (handler.isVisible(player)?"visibly":"invisibly"),
                    handler.getServerName()
            ));

        AbstractPacket packet = new Packet_ADD();
        packet.encode(player, handler);

        plugin.dispatchMessage(channelname, packet.toString());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
            plugin.getLogger().severe(String.format(
                    "[%1$s] - Local player %2$s left this server (%3$s).",
                    plugin.getDescription().getName(),
                    (
                            handler.isLogged(Verbosity.VerbosityLevels.SHOW_UUIDS) ?
                                    player.getName() + " [" + player.getUniqueId().toString() + "]" :
                                    player.getName()
                    ),
                    handler.getServerName()
            ));

        AbstractPacket packet = new Packet_REMOVE();
        packet.encode(player, handler);

        plugin.dispatchMessage(channelname, packet.toString());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerStatusChange(final VanishStatusChangeEvent event) {
        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
            plugin.getLogger().severe(String.format(
                    "[%1$s] - Local player %2$s has %3$s.",
                    plugin.getDescription().getName(),
                    (
                            handler.isLogged(Verbosity.VerbosityLevels.SHOW_UUIDS) ?
                                    event.getPlayer().getName() + " [" + event.getPlayer().getUniqueId().toString() + "]" :
                                    event.getPlayer().getName()
                    ),
                    (handler.isVisible(event.getPlayer())?"vanished":"unvanished")
            ));

        AbstractPacket packet = event.isVanishing() ? new Packet_VANISH() : new Packet_SHOW();
        packet.encode(event.getPlayer(), handler);

        plugin.dispatchMessage(channelname, packet.toString());
    }
}

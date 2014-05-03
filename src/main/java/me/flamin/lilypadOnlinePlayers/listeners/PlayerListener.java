package me.flamin.lilypadOnlinePlayers.listeners;

import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
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

        if (plugin.getConfig().getInt("debugLevel", 0) > 0)
            plugin.getLogger().info("Local player " + player.getName() + " logged in to " + player.getWorld().getName() + " and are " + (handler.isVisible(player)?"visible":"invisible") + ".");

        String msg =  Actions.ADD.getIDString() + '\0' + player.getName() + '\0' + handler.getServerName() + '\0' + player.getWorld().getName() + '\0' + handler.isVisible(player);
        plugin.dispatchMessage(channelname, msg);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        if (plugin.getConfig().getInt("debugLevel", 0) > 0)
            plugin.getLogger().info("Local player " + event.getPlayer().getName() + " quit.");

        String msg =  Actions.REMOVE.getIDString() + '\0' + event.getPlayer().getName() + '\0' + handler.getServerName();
        plugin.dispatchMessage(channelname, msg);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerStatusChange(final VanishStatusChangeEvent event) {
        if (plugin.getConfig().getInt("debugLevel", 0) > 0)
            plugin.getLogger().info("Local player " + event.getName() + " has " + (event.isVanishing()?"vanished":"unvanished") + ".");

        String msg = (event.isVanishing()?Actions.VANISH:Actions.SHOW).getIDString() + '\0' + event.getName() + '\0' + handler.getServerName();
        plugin.dispatchMessage(channelname, msg);
    }
}

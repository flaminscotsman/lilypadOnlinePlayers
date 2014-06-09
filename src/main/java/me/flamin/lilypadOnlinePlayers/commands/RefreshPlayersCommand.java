package me.flamin.lilypadOnlinePlayers.commands;

import me.flamin.lilypadOnlinePlayers.Actions;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayers;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerQuitEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;


public class RefreshPlayersCommand implements CommandExecutor {
    private static final String channelname = "lilypadPlayers";
    private final LilypadOnlinePlayersHandler handler;
    private final LilypadOnlinePlayers plugin;

    public RefreshPlayersCommand(LilypadOnlinePlayersHandler handler) {
        this.handler = handler;
        this.plugin = handler.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if ((commandSender instanceof Player) && ! commandSender.hasPermission("lilyPadOnlinePlayers.refresh")) {
            return false;
        }

        for (Map.Entry<String, PlayerEntry> iterEntry : handler.getPlayers().entrySet()) {
            PlayerEntry entry = iterEntry.getValue();

            handler.expirePlayer(entry.getName());
            plugin.getServer().getScheduler().runTaskLater(
                    plugin, new tidyUp(handler, entry.getName()), 1
            );
            plugin.getServer().getPluginManager().callEvent(new HubPlayerQuitEvent(entry));
        }

        String msg =  Actions.RESEND.getIDString();
        plugin.dispatchMessage(channelname, msg);
        commandSender.sendMessage("Initiated playerlist refresh.");
        return true;
    }

    private static class tidyUp implements Runnable {
        private final LilypadOnlinePlayersHandler handler;
        private final String player;

        tidyUp(final LilypadOnlinePlayersHandler handler, final String player) {
            this.handler = handler;
            this.player = player;
        }

        public void run() {
            if (handler.isPlayerExpired(player)) {
                handler.removePlayer(player);
                handler.unExpirePlayer(player);
            }
        }
    }
}

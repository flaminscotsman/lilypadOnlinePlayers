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


public class ForceRemoveCommand implements CommandExecutor {
    private static final String channelname = "lilypadPlayers";
    private final LilypadOnlinePlayersHandler handler;
    private final LilypadOnlinePlayers plugin;

    public ForceRemoveCommand(LilypadOnlinePlayersHandler handler) {
        this.handler = handler;
        this.plugin = handler.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0)
            return false;

        if ((commandSender instanceof Player) && !commandSender.hasPermission(command.getPermission())) {
            commandSender.sendMessage("Insufficient permissions to use /" + command.toString());
            return false;
        }

        PlayerEntry player = null;
        String matcher = args[0].toLowerCase();
        int matches = 0;

        for (Map.Entry<String, PlayerEntry> entry : handler.getPlayers().entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(matcher)) {
                player = entry.getValue();
                matches++;
            }
        }

        if (matches == 0) {
            commandSender.sendMessage("Failed to find player beginning with " + args[0]);
            return false;
        }

        if (matches > 1) {
            commandSender.sendMessage("Multiple players found matching " + args[0]);
            return false;
        }

        if (plugin.getServer().getPlayer(player.getUUID()) != null) {
            commandSender.sendMessage("Unable to remove player " + player
                    + " as they are currently logged in to this server.");
            return false;
        }

        String msg =  Actions.REMOVE.getIDString() + '\0' + player + '\0' + handler.getServerName();
        plugin.dispatchMessage(channelname, msg);

        handler.expirePlayer(player.getName());
        plugin.getServer().getScheduler().runTaskLater(plugin, new tidyUp(handler, player.getName()), 1);
        plugin.getServer().getPluginManager().callEvent(new HubPlayerQuitEvent(player));

        commandSender.sendMessage("Removed player " + player + " from " + handler.getServerName() + ".");
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

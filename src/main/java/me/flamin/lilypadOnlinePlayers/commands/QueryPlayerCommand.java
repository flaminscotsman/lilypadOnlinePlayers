package me.flamin.lilypadOnlinePlayers.commands;

import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class QueryPlayerCommand implements CommandExecutor {
    private static final String channelname = "lilypadPlayers";
    private final LilypadOnlinePlayersHandler handler;

    public QueryPlayerCommand(LilypadOnlinePlayersHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if ((commandSender instanceof Player) && ! commandSender.hasPermission(command.getPermission())
                || args.length > 1) {
            return false;
        }

        if (! handler.containsPlayer(args[0])) {
            commandSender.sendMessage("No player " + args[0] + " currently tracked.");
            return true;
        }

        PlayerEntry entry = handler.getPlayer(args[0]);
        String[] messages = new String[4];

        messages[0] = "Information for player " + entry.getName() + ":";
        messages[1] = "UUID: " + (entry.getUUID()==null ? "null" : entry.getUUID().toString());
        messages[2] = "Server: " + entry.getServer();
        messages[3] = "World: " + entry.getWorld();
        messages[4] = "Visibility: " + (entry.getVisible() ? "visible" : "vanished");

        commandSender.sendMessage(messages);
        return true;
    }
}

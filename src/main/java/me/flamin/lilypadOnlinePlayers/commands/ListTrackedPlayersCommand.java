package me.flamin.lilypadOnlinePlayers.commands;

import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;


public class ListTrackedPlayersCommand implements CommandExecutor {
    private static final String channelname = "lilypadPlayers";
    private final LilypadOnlinePlayersHandler handler;

    public ListTrackedPlayersCommand(LilypadOnlinePlayersHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if ((commandSender instanceof Player) && ! commandSender.hasPermission(command.getPermission())
                || args.length > 1) {
            return false;
        }

        StringBuilder message = new StringBuilder();

        for (Iterator<String> iterator = handler.getPlayers().keySet().iterator(); iterator.hasNext(); ) {
            message.append(iterator.next());
            if (iterator.hasNext())
                message.append(", ");
        }

        commandSender.sendMessage(message.append(".").toString());
        return true;
    }
}

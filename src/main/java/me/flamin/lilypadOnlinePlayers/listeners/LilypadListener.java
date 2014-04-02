package me.flamin.lilypadOnlinePlayers.listeners;

import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.event.ServerAddEvent;
import lilypad.client.connect.api.event.ServerRemoveEvent;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayersHandler;
import org.bukkit.entity.Player;
import me.flamin.lilypadOnlinePlayers.Actions;
import me.flamin.lilypadOnlinePlayers.LilypadOnlinePlayers;
import me.flamin.lilypadOnlinePlayers.PlayerEntry;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerJoinEvent;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerQuitEvent;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerVisibilityChangeEvent;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerWorldChangeEvent;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LilypadListener {
    private static final String channelname = "lilypadPlayers";
    private final LilypadOnlinePlayersHandler handler;
    private final LilypadOnlinePlayers plugin;

    public LilypadListener(LilypadOnlinePlayersHandler handler) {
        this.handler = handler;
        this.plugin = handler.getPlugin();
    }

    @EventListener
    public void onMessage(final MessageEvent event) {
        if (event.getChannel().equals(channelname)) {
            try {
                String[] tokens;
                tokens = event.getMessageAsString().split("\\x00");

                if (LilypadOnlinePlayers.DEBUG) {
                    String parsedString = formatString(event.getMessage());
                    plugin.getLogger().severe(String.format("[%1$s] - Message received from %2$s: %3$s",
                            plugin.getDescription().getName(), event.getSender(), parsedString));
                }

                Actions action = Actions.get(Integer.parseInt(tokens[0]));
                PlayerEntry entry;

                switch (action) {
                    case ADD:
                        entry = new PlayerEntry();
                        entry.setName(tokens[1]);
                        entry.setServer(tokens[2]);
                        entry.setVisible(Boolean.getBoolean(tokens[4]));
                        entry.setWorld(tokens[3]);

                        if (LilypadOnlinePlayers.DEBUG)
                            plugin.getLogger().severe("Player " + tokens[1] + " has joined " + tokens[3] +
                                ". Entry Contains" + entry.getName() + ", " + entry.getWorld() + ".");
                        if (handler.isPlayerExpired(tokens[1]))
                            handler.unExpirePlayer(tokens[1]);
                        handler.addPlayer(tokens[1], entry);
                        plugin.getServer().getPluginManager().callEvent(new HubPlayerJoinEvent(entry));
                        break;
                    case REMOVE:
                        if (!handler.containsPlayer(tokens[1]))
                            return;

                        entry = handler.getPlayer(tokens[1]);
						
                        if (!tokens[2].equals(entry.getServer()))
                            return;
                        handler.expirePlayer(tokens[1]);
                        plugin.getServer().getScheduler().runTaskLater(
                                plugin, new tidyUp(handler, entry.getName()), 1
                        );
                        plugin.getServer().getPluginManager().callEvent(new HubPlayerQuitEvent(tokens[1]));
                        break;
                    case MOVEWORLD:
                        entry = handler.getPlayer(tokens[1]);
                        entry.setWorld(tokens[2]);
                        handler.addPlayer(tokens[1], entry);
                        plugin.getServer().getPluginManager().callEvent(new HubPlayerWorldChangeEvent(entry));
                        break;
                    case VANISH:
                        entry = handler.getPlayer(tokens[1]);
                        entry.setVisible(false);
                        handler.addPlayer(tokens[1], entry);
                        plugin.getServer().getPluginManager().callEvent(new HubPlayerVisibilityChangeEvent(entry));
                        break;
                    case SHOW:
                        entry = handler.getPlayer(tokens[1]);
                        entry.setVisible(true);
                        handler.addPlayer(tokens[1], entry);
                        plugin.getServer().getPluginManager().callEvent(new HubPlayerVisibilityChangeEvent(entry));
                        break;
                    case RESEND:
                        for (Player player: plugin.getServer().getOnlinePlayers()) {
                            String msg = Actions.ADD.getIDString() + '\0' +player.getName() + '\0' +
                                    handler.getServerName() + '\0' + player.getWorld().getName() + '\0' +
                                    handler.isVanished(player);
                            plugin.dispatchMessage(channelname, msg);
                        }
                        break;
                    default:
                        plugin.getLogger().severe(String.format("[%s] - Malformed message received", plugin.getDescription().getName()));
                }
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
    }

    @EventListener
    public void onServerRemove(final ServerRemoveEvent event) {
        for (Map.Entry<String, PlayerEntry> iterEntry : handler.getPlayers().entrySet()) {
            PlayerEntry entry = iterEntry.getValue();

            handler.expirePlayer(entry.getName());
            plugin.getServer().getScheduler().runTaskLater(plugin, new tidyUp(handler, entry.getName()), 1);
            plugin.getServer().getPluginManager().callEvent(new HubPlayerQuitEvent(entry.getName()));
        }
    }

    @EventListener
    public void onServerAdd(final ServerAddEvent event) {
        if (event.getServer().equals(plugin.getServer().getName())) {
            for (Player player: plugin.getServer().getOnlinePlayers()) {
                String msg =  Actions.ADD.getIDString() + '\0' + player.getName() + '\0' + player.getServer().getName() + '\0' + player.getWorld().getName() + '\0' + handler.isVanished(player);
                plugin.dispatchMessage(channelname, msg);
            }
        } else {
            String target = event.getServer();
            for (Player player: plugin.getServer().getOnlinePlayers()) {
                String msg = Actions.ADD.getIDString() + '\0' + player.getName() + '\0' + player.getServer().getName() + '\0' + player.getWorld().getName() + '\0' + handler.isVanished(player);
                plugin.dispatchMessage(target, channelname, msg);
            }
        }
    }

    private String formatString(final byte[] byteArray) {
        StringBuilder parsedString = new StringBuilder();
        for (byte i: byteArray) {
            if(i < 32 || i > 126) {
                parsedString.append("\\").append(i & 0xFF);
            } else {
                parsedString.append(new String(new byte[] {i}, StandardCharsets.US_ASCII));
            }
        }

        return parsedString.toString();
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

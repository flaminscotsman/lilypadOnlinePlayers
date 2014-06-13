package me.flamin.lilypadOnlinePlayers.listeners;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import javax.annotation.Nonnull;
import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.event.ServerAddEvent;
import lilypad.client.connect.api.event.ServerRemoveEvent;
import me.flamin.lilypadOnlinePlayers.*;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerJoinEvent;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerQuitEvent;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerVisibilityChangeEvent;
import me.flamin.lilypadOnlinePlayers.events.HubPlayerWorldChangeEvent;
import me.flamin.lilypadOnlinePlayers.packets.*;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LilypadListener {
    private static final String channelname = LilypadOnlinePlayers.channelname;
    private static final Joiner joiner = Joiner.on(", ").skipNulls();
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

                if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_PACKETS)) {
                    String parsedString = formatString(event.getMessage());
                    plugin.getLogger().severe(String.format("[%1$s] - Message received from %2$s: %3$s",
                            plugin.getDescription().getName(), event.getSender(), parsedString));
                }

                Actions action = Actions.get(Integer.parseInt(tokens[0]));
                PlayerEntry entry;
                AbstractPacket packet;

                switch (action) {
                    case ADD:
                        packet = new Packet_ADD();
                        packet.decode(tokens, event.getSender());

                        entry = handler.containsPlayer(packet.getPlayer()) ? handler.getPlayer(packet.getPlayer()) : new PlayerEntry();
                        packet.updatePlayerEntry(entry);

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
                            plugin.getLogger().severe(String.format("[%1$s] - Player %2$s %3$s joined %4$s.",
                                    plugin.getDescription().getName(),
                                    (
                                        handler.isLogged(Verbosity.VerbosityLevels.SHOW_UUIDS) ?
                                        entry.getName() + " [" + entry.getUUID().toString() + "]" :
                                        entry.getName()
                                    ),
                                    (entry.getVisible()?"visibly":"invisibly"),
                                    event.getSender()
                            ));

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_EXPIRATIONS) && handler.isPlayerExpired(tokens[1]))
                            plugin.getLogger().severe("Unexpiring " + entry.getName() + " on rejoin.");

                        if (handler.isPlayerExpired(tokens[1]))
                            handler.unExpirePlayer(tokens[1]);

                        handler.addPlayer(entry.getName(), entry);

                        plugin.getServer().getPluginManager().callEvent(new HubPlayerJoinEvent(entry));
                        break;

                    case REMOVE:
                        packet = new Packet_REMOVE();
                        packet.decode(tokens, event.getSender());

                        PlayerEntry packetEntry = new PlayerEntry();
                        packet.updatePlayerEntry(packetEntry);

                        if (!handler.containsPlayer(packetEntry.getName()))
                            return;

                        entry = handler.getPlayer(packetEntry.getName());

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
                            if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
                                plugin.getLogger().severe(String.format("[%1$s] - Player %2$s quit %3$s.",
                                        plugin.getDescription().getName(),
                                        (
                                                handler.isLogged(Verbosity.VerbosityLevels.SHOW_UUIDS) ?
                                                        entry.getName() + " [" + entry.getUUID().toString() + "]" :
                                                        entry.getName()
                                        ),
                                        event.getSender()
                                ));

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_EXPIRATIONS)) {
                            if (event.getSender().equals(entry.getServer())) {
                                plugin.getLogger().severe("Expiring " + entry.getName() + " on quit.");
                            } else {
                                plugin.getLogger().severe("Not expiring " + entry.getName() + " as they are currently on this server.");
                            }
                        }

                        if (!event.getSender().equals(entry.getServer()))
                            break;

                        handler.expirePlayer(entry.getName());
                        plugin.getServer().getScheduler().runTaskLater(
                                plugin, new tidyUp(handler, entry.getName()), 1
                        );

                        plugin.getServer().getPluginManager().callEvent(new HubPlayerQuitEvent(tokens[1], tokens[2]));
                        break;

                    case MOVEWORLD:
                        packet = new Packet_MOVEWORLD();
                        packet.decode(tokens, event.getSender());

                        entry = handler.containsPlayer(packet.getPlayer()) ? handler.getPlayer(packet.getPlayer()) : new PlayerEntry();

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
                            plugin.getLogger().severe(String.format(
                                    "[%1$s] - Player %2$s has switched from %3$s to %4$s.",
                                    plugin.getDescription().getName(),
                                    entry.getName(),
                                    entry.getWorld(),
                                    packet.getWorld()
                            ));

                        packet.updatePlayerEntry(entry);
                        handler.addPlayer(entry.getName(), entry);

                        plugin.getServer().getPluginManager().callEvent(new HubPlayerWorldChangeEvent(entry));
                        break;

                    case VANISH:
                        packet = new Packet_VANISH();
                        packet.decode(tokens, event.getSender());

                        entry = handler.containsPlayer(packet.getPlayer()) ? handler.getPlayer(packet.getPlayer()) : new PlayerEntry();

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
                            plugin.getLogger().severe(String.format(
                                    "[%1$s] - Player %2$s has become invisible.",
                                    plugin.getDescription().getName(),
                                    entry.getName()
                            ));

                        packet.updatePlayerEntry(entry);
                        handler.addPlayer(entry.getName(), entry);

                        plugin.getServer().getPluginManager().callEvent(new HubPlayerVisibilityChangeEvent(entry));
                        break;

                    case SHOW:
                        packet = new Packet_SHOW();
                        packet.decode(tokens, event.getSender());

                        entry = handler.containsPlayer(packet.getPlayer()) ? handler.getPlayer(packet.getPlayer()) : new PlayerEntry();

                        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS))
                            plugin.getLogger().severe(String.format(
                                    "[%1$s] - Player %2$s has become visible.",
                                    plugin.getDescription().getName(),
                                    entry.getName()
                            ));

                        packet.updatePlayerEntry(entry);
                        handler.addPlayer(entry.getName(), entry);

                        plugin.getServer().getPluginManager().callEvent(new HubPlayerVisibilityChangeEvent(entry));
                        break;

                    case RESEND:
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            packet = new Packet_ADD();
                            packet.encode(player, handler);
                            plugin.dispatchMessage(channelname, packet.toString());
                        }
                        break;

                    case LEGACY_ADD: case LEGACY_REMOVE: case LEGACY_MOVEWORLD: case LEGACY_VANISH: case LEGACY_SHOW:
                        plugin.getLogger().severe(String.format(
                                "[%s] - Outdated message received from %s.",
                                plugin.getDescription().getName(),
                                event.getSender()
                        ));
                        break;

                    default:
                        plugin.getLogger().severe(String.format(
                                "[%s] - Malformed message received from %s",
                                plugin.getDescription().getName(),
                                event.getSender()
                        ));
                }
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
    }

    @EventListener
    public void onServerRemove(final ServerRemoveEvent event) {
        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS)) {
            Predicate<PlayerEntry> playerFilter = new Predicate<PlayerEntry>() {
                @Override
                public boolean apply(@Nonnull PlayerEntry entry) {
                    return entry.getServer().equals(event.getServer());
                }
            };

            plugin.getLogger().severe(String.format(
                    "[%1$s] - Lost connection with %2$s. Expiring: %3$s.",
                    plugin.getDescription().getName(),
                    event.getServer(),
                    joiner.join(Iterables.filter(handler.getPlayers().values(), playerFilter))
            ));
        }

        for (Map.Entry<String, PlayerEntry> iterEntry : handler.getPlayers().entrySet()) {
            PlayerEntry entry = iterEntry.getValue();

            handler.expirePlayer(entry.getName());
            plugin.getServer().getScheduler().runTaskLater(plugin, new tidyUp(handler, entry.getName()), 1);
            plugin.getServer().getPluginManager().callEvent(new HubPlayerQuitEvent(entry));
        }
    }

    @EventListener
    public void onServerAdd(final ServerAddEvent event) {
        if (handler.isLogged(Verbosity.VerbosityLevels.SHOW_MOVEMENTS)) {
            if (event.getServer().equals(plugin.getServer().getName())) {
                plugin.getLogger().severe(String.format(
                        "[%1$s] - Connection with %2$s established.",
                        plugin.getDescription().getName(),
                        event.getServer()
                ));
            } else {
                plugin.getLogger().severe(String.format(
                        "[%1$s] - Regained connection with network",
                        plugin.getDescription().getName()
                ));
            }
        }

        AbstractPacket packet;

        if (event.getServer().equals(plugin.getServer().getName())) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                packet = new Packet_ADD();
                packet.encode(player, handler);
                plugin.dispatchMessage(channelname, packet.toString());
            }
        } else {
            String target = event.getServer();

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                packet = new Packet_ADD();
                packet.encode(player, handler);
                plugin.dispatchMessage(target, channelname, packet.toString());
            }

            for (Map.Entry<String, PlayerEntry> entry : handler.getPlayers().entrySet()) {
                if (entry.getValue().getServer().equals(event.getServer()) && handler.isPlayerExpired(entry.getKey())) {
                    handler.unExpirePlayer(entry.getKey());
                }
            }
        }
    }

    private String formatString(final byte[] byteArray) {
        StringBuilder parsedString = new StringBuilder();
        for (byte i : byteArray) {
            if (i < 32 || i > 126) {
                parsedString.append("\\").append(i & 0xFF);
            } else {
                parsedString.append(new String(new byte[]{i}, StandardCharsets.US_ASCII));
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
                if (handler.getPlugin().getConfig().getInt("debugLevel", 0) > 2) {
                    handler.getPlugin().getLogger().info("Expiring " + player + ".");
                }
                handler.removePlayer(player);
                handler.unExpirePlayer(player);
            } else if (handler.getPlugin().getConfig().getInt("debugLevel", 0) > 2) {
                handler.getPlugin().getLogger().info("Player " + player + " has been unmarked for expiry.");
            }
        }
    }
}

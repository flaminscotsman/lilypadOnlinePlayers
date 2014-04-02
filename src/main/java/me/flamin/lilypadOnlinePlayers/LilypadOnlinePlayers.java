package me.flamin.lilypadOnlinePlayers;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.impl.MessageRequest;
import lilypad.client.connect.api.result.FutureResultListener;
import lilypad.client.connect.api.result.StatusCode;
import lilypad.client.connect.api.result.impl.MessageResult;
import me.flamin.lilypadOnlinePlayers.commands.ForceRemoveCommand;
import me.flamin.lilypadOnlinePlayers.commands.ListTrackedPlayersCommand;
import me.flamin.lilypadOnlinePlayers.commands.QueryPlayerCommand;
import me.flamin.lilypadOnlinePlayers.commands.RefreshPlayersCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import me.flamin.lilypadOnlinePlayers.listeners.LilypadListener;
import me.flamin.lilypadOnlinePlayers.listeners.PlayerListener;
import org.kitteh.vanish.VanishPlugin;

import java.util.*;

public class LilypadOnlinePlayers extends JavaPlugin {
    public static final boolean DEBUG = false;
    final Map<String, PlayerEntry> onlinePlayers = new HashMap<String, PlayerEntry>();
    final Set<String> expiredPlayers = new HashSet<String>();
    private Connect connect;
    String servername;
    private VanishPlugin vanishplugin;
    private LilypadOnlinePlayersHandler handler;

    @Override
    public void onEnable() {
        if (!hookLilypad()) {
            getLogger().severe(String.format("[%s] - Unable to find lilyconnect, disabling!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        servername = connect.getSettings().getUsername();

        handler = new LilypadOnlinePlayersHandler(this);
        getServer().getServicesManager().register(
                LilypadOnlinePlayersHandler.class, handler, this, ServicePriority.Normal
        );

        if (this.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")) {
            try {
                vanishplugin = (VanishPlugin) this.getServer().getPluginManager().getPlugin("VanishNoPacket");
            }
            catch (NullPointerException e) {
                getLogger().severe(
                        String.format("[%s] - Unable to bind to VanishNoPacket, making best guess at vanish status",
                        getDescription().getName())
                );
            }
        }

        connect.registerEvents(new LilypadListener(handler));
        getServer().getPluginManager().registerEvents(new PlayerListener(handler), this);

        getCommand("forceremove").setExecutor(new ForceRemoveCommand(handler));
        getCommand("listtrackedplayers").setExecutor(new ListTrackedPlayersCommand(handler));
        getCommand("refreshplayers").setExecutor(new RefreshPlayersCommand(handler));
        getCommand("queryplayer").setExecutor(new QueryPlayerCommand(handler));
    }

    private boolean hookLilypad() {
        if (getServer().getPluginManager().getPlugin("LilyPad-Connect") == null) {
            return false;
        }
        RegisteredServiceProvider<Connect> rsp = getServer().getServicesManager().getRegistration(Connect.class);
        if (rsp == null) {
            return false;
        }
        connect = rsp.getProvider();
        return connect != null;
    }

    public boolean dispatchMessage(String channelname, String msg) {
        return dispatchMessage("", channelname, msg);
    }

    public boolean dispatchMessage(String target, String channelname, String msg) {
        final boolean[] result = {false};
        try {
            connect.request(new MessageRequest(target, channelname, msg)).registerListener(new FutureResultListener<MessageResult>() {
                public void onResult(MessageResult redirectResult) {
                    if (redirectResult.getStatusCode() == StatusCode.SUCCESS) {
                        result[0] = true;
                    }
                }
            });
        } catch (Exception e) {
            getLogger().severe("Error sending message: " + e.getMessage());
        }
        return result[0];
    }

    public boolean isVanished(Player player) {
        return !(vanishplugin == null) && vanishplugin.getManager().isVanished(player);
    }
}


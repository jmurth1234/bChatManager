package net.rymate.bchatmanager;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;

/**
 * Main class
 *
 * @author rymate
 */
public class bChatManager extends JavaPlugin {

    public static Chat chat = null;
    private bChatListener listener;

    public void onEnable() {
        //setup the config
        setupConfig();

        //Chatlistener - can you hear me?
        this.listener = new bChatListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);

        //Vault chat hooks
        setupChat();

        //setup the Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[bChatManager] Enabled!");
    }

    private void setupConfig() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * Code to setup the Chat variable in Vault. Allows me to hook to all the prefix plugins.
     */
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    //
    //  Begin methods from Functions.java
    //

    public String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
        return format.replace("%prefix", chat.getPlayerPrefix(player))
                .replace("%suffix", chat.getPlayerSuffix(player))
                .replace("%world", worldName)
                .replace("%player", player.getName())
                .replace("%displayname", player.getDisplayName());
    }

    public String colorize(String string) {
        if (string == null) {
            return "";
        }
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }


}

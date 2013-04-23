package net.rymate.bchatmanager;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import net.rymate.bchatmanager.metrics.Metrics;

import java.io.IOException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class
 *
 * @author rymate
 */
public class bChatManager extends JavaPlugin {

    public static Chat chat = null;
    private bChatListener listener;
    public YamlConfiguration config;

    public void onEnable() {
        //setup the config
        setupConfig();

        //Chatlistener - can you hear me?
        this.listener = new bChatListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);

        //Vault chat hooks
        setupChat();

        //setup the Metrics
        Metrics metrics;
        try {
            metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            Logger.getLogger(bChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("[bChatManager] Enabled!");
    }

    private void setupConfig() {
        File configFile = new File(this.getDataFolder() + File.separator + "config.yml");
        try {
            if (!configFile.exists()) {
                this.saveDefaultConfig();
            }
        } catch (Exception ex) {
            Logger.getLogger(bChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        config = new YamlConfiguration();
        config.loadConfiguration(configFile);

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
                .replace("%displayname", player.getDisplayName())
                .replace("%group", chat.getPrimaryGroup(player));
    }

    public String colorize(String string) {
        if (string == null) {
            return "";
        }
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }
}

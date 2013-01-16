/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rymate.bchatmanager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author rymate
 */
public class bChatListener implements Listener {
    public String MESSAGE_FORMAT = "%prefix %player: &f%message";
    public String LOCAL_MESSAGE_FORMAT = "[LOCAL] %prefix %player: &f%message";
    public String PERSONAL_MESSAGE_FORMAT = "[MSG] [%player -> %reciever] &f%message";
    public String OP_MESSAGE_FORMAT = "&c[OPS ONLY] %player: &f%message";

    public Boolean RANGED_MODE = false;
    public double CHAT_RANGE = 100d;
    private final bChatManager plugin;
    YamlConfiguration config;

    public bChatListener(bChatManager aThis) {
        config = new YamlConfiguration();
        //config.load();
        this.MESSAGE_FORMAT = config.getString("formats.message-format", this.MESSAGE_FORMAT);
        this.LOCAL_MESSAGE_FORMAT = config.getString("formats.local-message-format", this.LOCAL_MESSAGE_FORMAT);
        this.PERSONAL_MESSAGE_FORMAT = config.getString("formats.personal-message-format", this.PERSONAL_MESSAGE_FORMAT);
        this.RANGED_MODE = config.getBoolean("toggles.ranged-mode", this.RANGED_MODE);
        this.CHAT_RANGE = config.getDouble("other.chat-range", this.CHAT_RANGE);

        this.plugin = aThis;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        
        String message = MESSAGE_FORMAT;
        boolean localChat = RANGED_MODE;
        
        if (localChat) {
            message = LOCAl_MESSAGE_FORMAT;
            //TODO: reimplement local chat
        }


        String chatMessage = event.getMessage();

        message = plugin.replacePlayerPlaceholders(player, message);
        message = plugin.colorize(message);

        if (player.hasPermission("bchatmanager.color")) {
            chatMessage = plugin.colorize(chatMessage);
        }

        message = message.replace("%message", chatMessage);

        event.setFormat(message);
        event.setMessage(chatMessage);

    }
}

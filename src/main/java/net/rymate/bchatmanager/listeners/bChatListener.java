/*
 * bChatManager - Chat management plugin for Bukkit
 * Originally - PermissionsEx chat management plugin for Bukkit
 * Copyright (C) 2011 rymate1234
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.rymate.bchatmanager.listeners;

import java.io.File;
import net.rymate.bchatmanager.util.Configuration;
import net.rymate.bchatmanager.Functions;
import net.rymate.bchatmanager.bChatManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

/**
 * ChatListener for bChatManager
 *
 * @author t3hk0d3
 * @author rymate1234
 */
public class bChatListener implements Listener {

    public String MESSAGE_FORMAT = "%prefix %player: &f%message";
    public String LOCAL_MESSAGE_FORMAT = "[LOCAL] %prefix %player: &f%message";
    public String PERSONAL_MESSAGE_FORMAT = "[FROM] %prefix %player ---> &f%message";
    public String DISPLAY_NAME_FORMAT = "%prefix%player%suffix";
    public String ALERT_FORMAT = "&c[ALERT] &f%message";
    public Boolean RANGED_MODE = false;
    public double CHAT_RANGE = 100d;
    private final bChatManager plugin;
    Configuration config;
    Functions f;

    public bChatListener(File configFile, bChatManager p) {
        config = new Configuration(configFile);
        config.init(p);
        this.MESSAGE_FORMAT = config.getString("formats.message-format", this.MESSAGE_FORMAT);
        this.LOCAL_MESSAGE_FORMAT = config.getString("formats.local-message-format", this.LOCAL_MESSAGE_FORMAT);
        this.PERSONAL_MESSAGE_FORMAT = config.getString("formats.personal-message-format", this.PERSONAL_MESSAGE_FORMAT);
        this.RANGED_MODE = config.getBoolean("toggles.ranged-mode", this.RANGED_MODE);
        this.CHAT_RANGE = config.getDouble("other.chat-range", this.CHAT_RANGE);
        this.DISPLAY_NAME_FORMAT = config.getString("formats.display-name-format", this.DISPLAY_NAME_FORMAT);
        this.ALERT_FORMAT = config.getString("formats.alert-format", this.ALERT_FORMAT);
        this.plugin = p;
        this.f = new Functions(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        String message = MESSAGE_FORMAT;
        boolean localChat = RANGED_MODE;

        String chatMessage = event.getMessage();
        if (chatMessage.startsWith("!") && player.hasPermission("bchatmanager.chat.global")) {
            localChat = false;
            chatMessage = chatMessage.substring(1);
        }

        if (chatMessage.startsWith("#") && player.hasPermission("bchatmanager.chat.alert")) {
            localChat = false;
            chatMessage = chatMessage.substring(1);
            message = ALERT_FORMAT;
        }

        if (chatMessage.startsWith("@") && player.hasPermission("bchatmanager.chat.message")) {
            chatMessage = chatMessage.substring(1);
            String[] messageSplit = chatMessage.split(" ");
            Player reciever = plugin.getServer().getPlayer(messageSplit[0]);
            if (reciever == null) {
                player.sendMessage("This player isn't online or you just typed the @ symmbol! Ignoring.");
                event.setCancelled(true);
            } else {
                chatMessage = chatMessage.replaceFirst(messageSplit[0], "");
                localChat = false;
                event.getRecipients().clear();
                event.getRecipients().add(player);
                event.getRecipients().add(reciever);
                event.getRecipients().addAll(f.getSpies());
                message = PERSONAL_MESSAGE_FORMAT;
            }
        }

        if (localChat == true) {
            message = LOCAL_MESSAGE_FORMAT;
        }

        message = f.colorize(message);

        if (player.hasPermission("bchatmanager.chat.color")) {
            chatMessage = f.colorize(chatMessage);
        }

        message = message.replace("%message", "%2$s").replace("%displayname", "%1$s");
        message = f.replacePlayerPlaceholders(player, message);
        message = f.replaceTime(message);

        event.setFormat(message);
        event.setMessage(chatMessage);

        if (localChat) {
            double range = CHAT_RANGE;
            event.getRecipients().clear();
            event.getRecipients().addAll(f.getLocalRecipients(player, message, range));
            event.getRecipients().addAll(f.getSpies());
        }
    }
}

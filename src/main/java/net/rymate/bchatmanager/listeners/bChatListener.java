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
import java.util.LinkedList;
import java.util.List;
import net.rymate.bchatmanager.util.Configuration;
import net.rymate.bchatmanager.Functions;
import net.rymate.bchatmanager.bChatManager;
import net.rymate.bchatmanager.channels.Channel;
import net.rymate.bchatmanager.channels.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * ChatListener for bChatManager
 *
 * @author t3hk0d3
 * @author rymate1234
 */
public class bChatListener implements Listener {

    public String MESSAGE_FORMAT = "&2[%channel] %prefix %player: &f%message";
    public String DISPLAY_NAME_FORMAT = "%prefix%player%suffix";
    public String OP_MESSAGE_FORMAT = "&c[OPS ONLY] %player: &f%message";
    public String PERSONAL_MESSAGE_FORMAT = "[MSG] [%player -> %reciever] &f%message";
    private final bChatManager plugin;
    Configuration config;
    Functions f;
    ChannelManager chan;
    private final String glob;
    private boolean channelChat = true;
    private boolean IP_FILTER = true;

    public bChatListener(File configFile, bChatManager p) {
        config = new Configuration(configFile);
        config.init(p);
        this.MESSAGE_FORMAT = config.getString(
                "channels.channel-chat-format", this.MESSAGE_FORMAT);
        this.DISPLAY_NAME_FORMAT = config.getString(
                "formats.display-name-format", this.DISPLAY_NAME_FORMAT);
        this.OP_MESSAGE_FORMAT = config.getString("formats.op-message-format",
                this.OP_MESSAGE_FORMAT);
        this.PERSONAL_MESSAGE_FORMAT = config
                .getString("formats.personal-message-format",
                this.PERSONAL_MESSAGE_FORMAT);


        this.IP_FILTER = config.getBoolean("toggles.ip-filter", true);


        this.glob = config.getString("channels.default-channel", "global");
        this.plugin = p;
        this.chan = plugin.getChannelManager();
        this.f = new Functions(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        String message = MESSAGE_FORMAT;
        String chatMessage = event.getMessage();
        boolean opMessage = false;

        if (chatMessage.startsWith("@") && player.hasPermission("bchatmanager.chat.message")) {
            chatMessage = chatMessage.substring(1);
            String[] messageSplit = chatMessage.split(" ");
            Player reciever = plugin.getServer().getPlayer(messageSplit[0]);
            if (messageSplit[0] == "ops") {
                chatMessage = chatMessage.replaceFirst(messageSplit[0], "");
                chatMessage = chatMessage.replaceAll("%reciever", messageSplit[0]);

                List<Player> recipients = new LinkedList<Player>();
                event.getRecipients().clear();
                event.getRecipients().add(player);

                for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
                    if (recipient.isOp()) {
                        recipients.add(recipient);
                    }
                }

                event.getRecipients().addAll(recipients);
                message = PERSONAL_MESSAGE_FORMAT;
            } else if (reciever == null) {
                player.sendMessage("This player isn't online or you just typed the @ symmbol! Ignoring.");
                event.setCancelled(true);
            } else {
                chatMessage = chatMessage.replaceFirst(messageSplit[0], "");
                chatMessage = chatMessage.replaceAll("%reciever",
                        messageSplit[0]);
                channelChat = false;
                event.getRecipients().clear();
                event.getRecipients().add(player);
                event.getRecipients().add(reciever);
                event.getRecipients().addAll(f.getSpies());
                message = PERSONAL_MESSAGE_FORMAT;
            }
        }

        if (chatMessage.startsWith("%") && player.isOp()) {
            List<Player> recipients = new LinkedList<Player>();
            event.getRecipients().clear();
            event.getRecipients().add(player);
            for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
                if (recipient.isOp()) {
                    recipients.add(recipient);
                }
            }
            event.getRecipients().addAll(recipients);
            message = OP_MESSAGE_FORMAT;
            opMessage = true;
        }

        message = f.colorize(message);

        if (player.hasPermission("bchatmanager.chat.color")) {
            chatMessage = f.colorize(chatMessage);
        }

        if (opMessage) {
            event.setCancelled(true);
            List<Player> pl = (List<Player>) event.getRecipients();
            message = message.replace("%message", chatMessage).replace(
                    "%displayname", "%1$s");
            message = f.replacePlayerPlaceholders(player, message);
            message = f.replaceTime(message);
            for (int j = 0; j < pl.size(); j++) {
                pl.get(j).sendMessage(message);
            }
        }

        message = message.replace("%message", "%2$s").replace("%displayname",
                "%1$s");
        message = f.replacePlayerPlaceholders(player, message);
        message = f.replaceTime(message);
        message = message.replace("%channel",
                chan.getActiveChannel(player.getName()).getName());

        if (IP_FILTER) {
            message = message.replaceAll("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "lmao");
        }
        if (channelChat) {
            // start channel stuff :D
            Channel c = chan.getActiveChannel(player.getName());
            List<String> pls = c.getPlayersInChannel();
            List<Player> recipients = new LinkedList<Player>();

            event.getRecipients().clear();
            event.getRecipients().add(player);
            //System.out.println("LOL READ THIS " + pls.size());
            for (int i = 0; i < pls.size(); i++) {
                Player p = Bukkit.getPlayer(pls.get(i));
                if (p != null) {
                    if (p.isOnline()) {
                        recipients.add(Bukkit.getPlayer(pls.get(i)));
                    }
                }
            }
            event.getRecipients().addAll(recipients);
        }
        
        event.setFormat(message);
        event.setMessage(chatMessage);

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = (Player) event.getPlayer();
        // shove them in the default channel if they ain't in it
        if (chan.getPlayerChannels(player.getName(), glob) != null) {
            if (chan.getPlayerChannels(player.getName(), glob).isEmpty()) {
                chan.setActiveChannel(player.getName(), config.getString("channels.default-channel", "global"));
                chan.save();
            }
        } else {
            chan.setActiveChannel(player.getName(), config.getString("channels.default-channel", "global"));
            chan.save();
        }
    }
}

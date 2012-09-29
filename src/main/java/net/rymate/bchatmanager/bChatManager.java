/*
 * bChatManager - bPermissions chat management plugin for Bukkit
 * Originally - PermissionsEx chat management plugin for Bukkit
 * Copyright (C) 2011 t3hk0d3 http://www.tehkode.ru
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
package net.rymate.bchatmanager;

import net.rymate.bchatmanager.util.Configuration;
import net.rymate.bchatmanager.listeners.bChatListener;
import net.rymate.bchatmanager.listeners.LegacyChatListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import net.rymate.bchatmanager.channels.Channel;
import net.rymate.bchatmanager.channels.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

/**
 * Main class for bChatManager
 *
 * @author t3hk0d3
 * @author rymate1234
 */
public class bChatManager extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    protected bChatListener listener;
    protected LegacyChatListener lListener;
    public File configFile;
    private Configuration config;
    private ChannelManager chan;

    public bChatManager() {
        super();
    }

    @Override
    public void onEnable() {
        //guess what this does
        setupConfig();

        //setup the channel manager.
        if (config.getBoolean("toggles.chat-channels", true) == true) {
            chan = new ChannelManager();
            boolean check = chan.load();
            if (check == false) {
                logger.info("[bChatManager] It appears this is your first time using bChatManager! Lets create a default channel...");
                chan.addChannel(config.getString("channels.default-channel", "global"));
                chan.save();
            }

            String glob = config.getString("channels.default-channel", "global");

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (chan.getPlayerChannels(player.getName(), glob).isEmpty()) {
                    chan.setActiveChannel(player.getName(), glob);
                    chan.save();
                }
            }
        }

        //don't want channels? don't use 'em! :D
        if (!config.getBoolean("toggles.chat-channels", true)) {
            this.lListener = new LegacyChatListener(configFile, this);
            this.getServer().getPluginManager().registerEvents(this.lListener, this);
        } else {
            this.listener = new bChatListener(configFile, this);
            this.getServer().getPluginManager().registerEvents(this.listener, this);
        }

        //setup the Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //and we're done!
        Messages.ENABLED.print();
    }

    @Override
    public void onDisable() {
        this.listener = null;
        if (config.getBoolean("toggles.chat-channels", true)) {
            chan.save();
        }
        logger.info("[bChatManager] bChatManager disabled!");
    }

    public void setupConfig() {
        configFile = new File(this.getDataFolder() + File.separator + "config.yml");
        config = new Configuration(configFile);
        config.init(this);
    }

    public ChannelManager getChannelManager() {
        return chan;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((command.getName().equals("me")) && (config.getBoolean("toggles.control-me", true))) {
            String meFormat = config.getString("formats.me-format", "* %player %message");
            Double chatRange = config.getDouble("other.chat-range", 100);
            boolean rangedMode = config.getBoolean("toggles.ranged-mode", false);
            Functions f = new Functions(this);
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Ya need to type something after it :P");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
                return true;
            }
            Player player = (Player) sender;
            int i;
            StringBuilder me = new StringBuilder();
            for (i = 0; i < args.length; i++) {
                me.append(args[i]);
                me.append(" ");
            }
            String meMessage = me.toString();
            String message = meFormat;
            message = f.colorize(message);

            if (sender.hasPermission("bchatmanager.chat.color")) {
                meMessage = f.colorize(meMessage);
            }

            message = message.replace("%message", meMessage).replace("%displayname", "%1$s");
            message = f.replacePlayerPlaceholders(player, message);
            message = f.replaceTime(message);

            if (rangedMode) {
                List<Player> pl = f.getLocalRecipients(player, message, chatRange);
                for (int j = 0; j < pl.size(); j++) {
                    pl.get(j).sendMessage(message);
                }
                sender.sendMessage(message);
                System.out.println(message);
            } else {
                getServer().broadcastMessage(message);
            }
            return true;
        }

        if ((command.getName().equals("join")) && (config.getBoolean("toggles.chat-channels", true))) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Please specify a channel to join.");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
                return true;
            }

            if (sender.hasPermission("bchatmanager.join")) {
                Messages.NO_PERMISSIONS.send(sender);
            }

            Player p = (Player) sender;
            List<Channel> list = chan.getPlayerChannels(p.getName(), null);
            if (chan.getChannel(args[0]) != null) {
                if (!list.contains(chan.getChannel(args[0]))) {
                    chan.getChannel(args[0]).addPlayer(p);
                    chan.setActiveChannel(p.getName(), args[0]);
                    String message = Messages.CHANNEL_JOINED.get();
                    List<String> playerz = chan.getChannel(args[0]).getPlayersInChannel();
                    for (int i = 0; i > playerz.size(); i++) {
                        Player thingy = this.getServer().getPlayer(playerz.get(i));
                        thingy.sendMessage(message);
                    }
                } else {
                    Messages.IN_CHANNL_ANYWAY.send(p);
                    return true;
                }
            } else {
                chan.addChannel(args[0]);
                chan.getChannel(args[0]).addPlayer(p);
                chan.setActiveChannel(p.getName(), args[0]);
                String message = Messages.CHANNEL_JOINED.get();
                message = message.replaceAll("%player", p.getName()).replaceAll("%channel", args[0]);
                List<String> playerz = chan.getChannel(args[0]).getPlayersInChannel();
                for (int i = 0; i > playerz.size(); i++) {
                    Player thingy = this.getServer().getPlayer(playerz.get(i));
                    thingy.sendMessage(message);
                }
            }
            return true;
        }

        if ((command.getName().equals("leave")) && (config.getBoolean("toggles.chat-channels", true))) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Please specify a channel to leave.");
                return false;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
                return true;
            }

            if (sender.hasPermission("bchatmanager.leave")) {
                Messages.NO_PERMISSIONS.send(sender);
            }

            Player p = (Player) sender;
            List<Channel> list = chan.getPlayerChannels(p.getName(), null);
            if (chan.getChannel(args[0]) != null) {
                if (list.contains(chan.getChannel(args[0]))) {
                    chan.getChannel(args[0]).rmPlayer(p);
                    chan.setActiveChannel(p.getName(), config.getString("channels.default-channel", "global"));
                    String message = Messages.CHANNEL_LEFT.get();
                    List<String> playerz = chan.getChannel(args[0]).getPlayersInChannel();
                    for (int i = 0; i > playerz.size(); i++) {
                        Player thingy = this.getServer().getPlayer(playerz.get(i));
                        thingy.sendMessage(message);
                    }
                } else {
                    Messages.NOT_IN_CHANNEL.send(p);
                    return true;
                }
            } else {
                Messages.CANT_LEAVE_CHANNEL_NULL.send(p);
            }
            return true;
        }

        if ((command.getName().equals("focus")) && (config.getBoolean("toggles.chat-channels", true))) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
                return true;
            }
            if (args.length < 1) {
                String chanName = chan.getActiveChannel(sender.getName()).getName();
                sender.sendMessage(ChatColor.GREEN + "You are currently focused on: " + chanName);
                return true;
            }

            if (sender.hasPermission("bchatmanager.focus")) {
                Messages.NO_PERMISSIONS.send(sender);
            }

            Player p = (Player) sender;
            List<Channel> list = chan.getPlayerChannels(p.getName(), null);
            if (chan.getChannel(args[0]) != null) {
                if (list.contains(chan.getChannel(args[0]))) {
                    Channel c = chan.getChannel(args[0]);
                    chan.setActiveChannel(p.getName(), c.getName());
                } else {
                    Messages.NOT_IN_CHANNEL.send(p);
                    return true;
                }
            } else {
                Messages.NOT_IN_CHANNEL.send(p);
                return true;
            }
        }

        if ((command.getName().equals("bchatreload"))) {
            if (!(sender instanceof Player)) {
                getServer().getPluginManager().disablePlugin(this);
                getServer().getPluginManager().enablePlugin(this);
                sender.sendMessage(ChatColor.AQUA + "[bChatManager] Plugin reloaded!");
                return true;
            }

            if (sender.hasPermission("bchatmanager.reload")) {
                Messages.NO_PERMISSIONS.send(sender);
                return true;
            }

            getServer().getPluginManager().disablePlugin(this);
            getServer().getPluginManager().enablePlugin(this);
            sender.sendMessage(ChatColor.AQUA + "[bChatManager] Plugin reloaded!");
            return true;
        }
        return true;
    }
}
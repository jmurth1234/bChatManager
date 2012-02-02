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

import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for bChatManager
 * 
 * @oldauthor t3hk0d3
 * @author rymate1234
 */
public class bChatManager extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    protected bChatListener listener;

    @Override
    public void onEnable() {
        setupConfig();
        setupCommands();
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, this.listener, Priority.Normal, this);
        try {
            // create a new metrics object
            Metrics metrics = new Metrics();

            // 'this' in this context is the Plugin object
            metrics.beginMeasuringPlugin(this);
        } catch (Exception e) {
            System.out.println(e);
            // Failed to submit the stats :-(
        }
        logger.info("[ChatManager] ChatManager enabled.");
    }

    @Override
    public void onDisable() {
        this.listener = null;
        logger.info("[ChatManager] ChatManager disabled!");
    }

    public void setupConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.listener = new bChatListener((YamlConfiguration) this.getConfig(), this);
    }

    public void setupCommands() {
        boolean use = this.getConfig().getBoolean("me-format", true);
        if (use == true) {
            getCommand("me").setExecutor(new MeCommand(this.getConfig(), this));
        }
    }

    public enum Messages {

        PASSWORD_WRONG("Password incorrect.");
        private String format;

        Messages(String format) {
            this.format = format;
        }

        /**
         * Sends a message.
         *
         * @param sender reciever
         */
        void send(CommandSender sender) {
            sender.sendMessage(format);
        }

        /**
         * Prints a message prefixed with [bChatManager] to the console.
         */
        void print() {
            logger.info("[bChatManager] " + format);
        }
    }
}

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

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for bChatManager
 *
 * @author t3hk0d3
 * @author rymate1234
 */
public class bChatManager extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    protected bChatListener listener;
    public File configFile;
    private Configuration config;

    @Override
    public void onEnable() {
        setupConfig();
        this.getServer().getPluginManager().registerEvents(this.listener, this);
        try {
            Metrics metrics = new Metrics();
            metrics.beginMeasuringPlugin(this);
        } catch (Exception e) {
            System.out.println(e);
        }
        logger.info("[bChatManager] bChatManager enabled.");
    }

    @Override
    public void onDisable() {
        this.listener = null;
        logger.info("[bChatManager] bChatManager disabled!");
    }

    public void setupConfig() {
        configFile = new File(this.getDataFolder() + File.separator + "config.yml");
        config = new Configuration(configFile);
        config.initialize();
        this.listener = new bChatListener(configFile, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (("me".equals(command.getName())) && (config.getBoolean("toggles.control-me", true))) {
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
        return true;
    }
}

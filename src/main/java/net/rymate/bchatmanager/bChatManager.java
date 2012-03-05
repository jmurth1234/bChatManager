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
import java.util.logging.Logger;
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
        setupCommands();
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
        configFile = new File (this.getDataFolder() + File.separator + "config.yml");
        config = new Configuration(configFile);
        config.initialize();
        this.listener = new bChatListener(configFile, this);
    }

    public void setupCommands() {
        boolean use = config.getBoolean("toggles.control-me", true);
        if (use == true) {
            getCommand("me").setExecutor(new MeCommand(configFile, this));
        }
    }
}

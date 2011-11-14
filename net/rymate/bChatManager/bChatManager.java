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
package net.rymate.bChatManager;

import de.bananaco.permissions.worlds.WorldPermissionsManager;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import com.randomappdev.pluginstats.Ping;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.info.InfoReader;

/**
 * Main class for bChatManager
 * 
 * @oldauthor t3hk0d3
 * @author rymate1234
 */
public class bChatManager extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    protected bChatListener listener;
    public InfoReader ir = null;
    public WorldPermissionsManager wpm;

    public bChatManager() {
    }

    @Override
    public void onEnable() {
        setupPrefixes();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.listener = new bChatListener((YamlConfiguration) this.getConfig(), this);
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, this.listener, Priority.Normal, this);
        Ping.init(this);
        getCommand("me").setExecutor(new MeCommand(this.getConfig(), this));
        logger.info("[ChatManager] ChatManager enabled.");
    }

    @Override
    public void onDisable() {
        this.listener = null;
        logger.info("[ChatManager] ChatManager disabled!");
    }

    public void setupPrefixes() {
        try {
            ir = Permissions.getInfoReader();
            wpm = Permissions.getWorldPermissionsManager();
        } catch (Exception e) {
            System.err.println("bPermissions not detected! Disabling plugin.");
            this.getPluginLoader().disablePlugin(this);
        }
    }
}

/*
 * ChatManager - PermissionsEx Chat management plugin for Bukkit
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
package net.rymate.ChatManager;

import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import com.randomappdev.pluginstats.Ping;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

/**
 *
 * @author t3hk0d3
 */
public class ChatManager extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    protected ChatListener listener;
    public InfoReader ir = null;

    public ChatManager() {
    }

    @Override
    public void onEnable() {
        setupPrefixes();
        this.getConfig().options().copyDefaults(true);
        this.listener = new ChatListener((YamlConfiguration) this.getConfig(), this);
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, this.listener, Priority.Normal, this);
        Ping.init(this);
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
        } catch (Exception e) {
            System.err.println("bPermissions not detected! Disabling plugin.");
            this.getPluginLoader().disablePlugin(this);
        }
    }
}

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
package net.rymate.bchatmanager;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Main functions in bChatManager :D
 * @author rymate1234
 */

public class Functions {

    private final String displayNameFormat;

    public Functions(bChatManager plugin) {
        this.displayNameFormat = plugin.getConfig().getString("display-name-format", this.displayNameFormat);
    }

    public String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
        return format.replace("%prefix", getInfo(player, "prefix")).replace("%suffix", getInfo(player, "suffix")).replace("%world", worldName).replace("%player", player.getName()).replace("%displayname", player.getDisplayName());
    }

    public String replaceTime(String message) {
        Calendar calendar = Calendar.getInstance();

        if (message.contains("%h")) {
            message = message.replace("%h", String.format("%02d", calendar.get(Calendar.HOUR)));
        }

        if (message.contains("%H")) {
            message = message.replace("%H", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
        }

        if (message.contains("%g")) {
            message = message.replace("%g", Integer.toString(calendar.get(Calendar.HOUR)));
        }

        if (message.contains("%G")) {
            message = message.replace("%G", Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        }

        if (message.contains("%i")) {
            message = message.replace("%i", String.format("%02d", calendar.get(Calendar.MINUTE)));
        }

        if (message.contains("%s")) {
            message = message.replace("%s", String.format("%02d", calendar.get(Calendar.SECOND)));
        }

        if (message.contains("%a")) {
            message = message.replace("%a", (calendar.get(Calendar.AM_PM) == 0) ? "am" : "pm");
        }

        if (message.contains("%A")) {
            message = message.replace("%A", (calendar.get(Calendar.AM_PM) == 0) ? "AM" : "PM");
        }

        return message;
    }

    public String colorize(String string) {
        if (string == null) {
            return "";
        }
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }

    public void updateDisplayNames() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            updateDisplayName(player);
        }
    }

    public void updateDisplayName(Player player) {
        String worldName = player.getWorld().getName();
        player.setDisplayName(colorize(replacePlayerPlaceholders(player, this.displayNameFormat)));
    }

    public List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
            // Recipient are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }
            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) {
                continue;
            }
            recipients.add(recipient);
        }
        return recipients;
    }

    public List<Player> getSpies() {
        List<Player> recipients = new LinkedList<Player>();
        for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
            if (recipient.hasPermission("bchatmanager.spy")) {
                recipients.add(recipient);
            }
        }
        return recipients;
    }

    //IT WORKS!
    public String getInfo(Player player, String info) { 
        String output;
        output = ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), info);
        String colored = colorize(output);
        if (colored == null) {
            return "";
        } else {
            return colored;
        }
    }
}

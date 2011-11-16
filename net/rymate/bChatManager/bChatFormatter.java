/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rymate.bChatManager;

import java.util.Calendar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Ryan
 */
public class bChatFormatter {

    private final bChatManager plugin;

    public bChatFormatter(bChatManager plugin) {
        this.plugin = plugin;
    }

    protected String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
        return format.replace("%prefix", this.colorize(plugin.ir.getPrefix(player)))
                     .replace("%suffix", this.colorize(plugin.ir.getSuffix(player)))
                     .replace("%world", worldName)
                     .replace("%player", player.getName());
    }

    protected String replaceTime(String message) {
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

    protected String colorize(String string) {
        if (string == null) {
            return "";
        }
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }
}

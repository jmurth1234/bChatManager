/*
 * bChatManager - PermissionsEx Chat management plugin for Bukkit
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

import de.bananaco.permissions.interfaces.PermissionSet;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 *
 * @author t3hk0d3
 */
public class bChatListener extends PlayerListener {
    
    public final static String MESSAGE_FORMAT = "<%prefix%player%suffix> %message";
    public final static Boolean RANGED_MODE = false;
    public final static double CHAT_RANGE = 100d;

    protected String messageFormat = MESSAGE_FORMAT;
    protected boolean rangedMode = RANGED_MODE;
    protected double chatRange = CHAT_RANGE;
    protected String displayNameFormat = "%prefix%player%suffix";
    
    protected String optionChatRange = "chat-range";
    protected String optionMessageFormat = "message-format";
    protected String optionRangedMode = "force-ranged-mode";
    protected String optionDisplayname = "display-name-format";
    private final bChatManager plugin;
    private final String alertFormat;

    public bChatListener(YamlConfiguration config, bChatManager aThis) {
        this.messageFormat = config.getString("message-format", this.messageFormat);
        this.rangedMode = config.getBoolean("ranged-mode", this.rangedMode);
        this.chatRange = config.getDouble("chat-range", this.chatRange);
        this.displayNameFormat = config.getString("display-name-format", this.displayNameFormat);
        this.alertFormat = config.getString("alert-format", this.alertFormat);
        this.plugin = aThis;
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        String message = messageFormat;
        boolean localChat = rangedMode;

        String chatMessage = event.getMessage();
        if (chatMessage.startsWith("!") && player.hasPermission("bchatmanager.chat.global")) {
            localChat = false;
            chatMessage = chatMessage.substring(1);
            //message = globalMessageFormat;
        }

        if (chatMessage.startsWith("@") && player.hasPermission("bchatmanager.chat.alert")) {
            localChat = false;
            chatMessage = chatMessage.substring(1);
            message = alertFormat;
        }

        message = this.colorize(message);

        if (player.hasPermission("bchatmanager.chat.color")) {
            chatMessage = this.colorize(chatMessage);
        }

        message = message.replace("%message", "%2$s").replace("%displayname", "%1$s");
        message = this.replacePlayerPlaceholders(player, message);
        message = this.replaceTime(message);
        
        event.setFormat(message);
        event.setMessage(chatMessage);

        if (localChat) {
            double range = chatRange;
            
            event.getRecipients().clear();
            event.getRecipients().addAll(this.getLocalRecipients(player, message, range));
        }
    }
    
    protected void updateDisplayNames(){
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            updateDisplayName(player);
        }
    }
    
    protected void updateDisplayName(Player player){
        
        String worldName = player.getWorld().getName();
        player.setDisplayName(this.colorize(this.replacePlayerPlaceholders(player, this.displayNameFormat)));
    }
    
    protected String replacePlayerPlaceholders(Player player, String format){
        String worldName = player.getWorld().getName();  
        return format.replace("%prefix", this.colorize(plugin.ir.getPrefix(player)))
                     .replace("%suffix", this.colorize(plugin.ir.getSuffix(player)))
                     .replace("%world", worldName)                     
                     .replace("%player", player.getName())
                     .replace("%group", getGroup(player));
    }


    protected List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
            // Recipient are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }

            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance && !sender.hasPermission("chatmanager.override.ranged")) {
                continue;
            }

            recipients.add(recipient);
        }
        return recipients;
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
        if(string == null){
            return "";
        }
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }

    private CharSequence getGroup(Player player) {
        PermissionSet world = plugin.wpm.getPermissionSet(player.getWorld());
        List<String> grouplist = world.getGroups(player);
        String group = grouplist.get(0);
        return group;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rymate.bChatManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Ryan
 */
class MeCommand implements CommandExecutor {

    public MeCommand(FileConfiguration config, bChatManager aThis) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Ya need to type something after it :P");
            return false;
        }
        int i;
        StringBuilder me = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            me.append(" ");
            me.append(args[i]);
        }
        sender.sendMessage(me.toString());
        return true;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rymate.bchatmanager;

import java.util.logging.Logger;
import org.bukkit.command.CommandSender;

/**
 *
 * @author rymate
 */
public enum Messages {

    PASSWORD_WRONG("Password incorrect."),
    NO_PERMISSIONS("Thou shalt not use that command."),
    ENABLED("bChatManager enabled! Have a nice day :)"),
    CHANNEL_REMOVED("The channel %channel has been destroyed"),
    CHANNEL_JOINED("%player has joined %channel"),
    CHANNEL_REMOVE_ERROR("Someone tried to remove a non-existant channel!"),
    CHANNEL_LEFT("%player has left %channel"),
    CHANNEL_ACTIVE("Your active channel is now %channel"),
    ERR("------------ AN ERROR OCCURED. BELOW IS ERROR ------------"
    + "\n %error \n MORE DETAILS BELOW...."),
    ERR_END("------------ PLEASE REPORT TO THE BUKKIT DEV PAGE ------------"), 
    IN_CHANNL_ANYWAY("You're already in that channel! Use /focus to focus it.");
    private String format;

    Messages(String format) {
        this.format = format;
    }

    /**
     * Sends a message.
     *
     * @param sender receiver
     */
    public void send(CommandSender sender) {
        sender.sendMessage(format);
    }

    /**
     * Sends a formatted message. I wish I could be bothered to make a formatter
     * object :3
     *
     * @param sender person who will receive the message
     * @param toReplace string to replace in the message
     * @param toReplaceWith what to replace the above string with
     *
     */
    public void sendFormatted(CommandSender sender, String toReplace, String toReplaceWith) {
        format = format.replaceAll(toReplace, toReplaceWith);
        sender.sendMessage(format);
    }

    /**
     * Gets a String from this Enum.
     *
     * @return a string
     */
    public String get() {
        return format;
    }

    /**
     * Prints a message prefixed with [bChatManager] to the console.
     */
    public void print() {
        Logger logger = Logger.getLogger("Minecraft");
        logger.info("[bChatManager] " + format);
    }

    /**
     * Prints an error message prefixed with [bChatManager] to the console. Can
     * have optional error message/stack trace
     */
    public void printErr(String toReplace, String toReplaceWith) {
        format = format.replaceAll(toReplace, toReplaceWith);
        Logger logger = Logger.getLogger("Minecraft");
        logger.severe("[bChatManager] " + format);
    }
}

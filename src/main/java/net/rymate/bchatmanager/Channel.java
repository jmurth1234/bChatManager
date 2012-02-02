package net.rymate.bchatmanager;

import java.util.List;
import org.bukkit.entity.Player;

/**
 * Represents a chat channel. These will be managed by ChannelManager hopefully.
 * You'll also be able to hook one directly by getting them through the ChannelManager
 * That's if I ever get round to doing an API ;)
 * It'll definitely be used internally :D
 * @author rymate
 */
public class Channel {
    public String name; //kinda obvious
    public boolean isPublic; //in case I implement a list of channels (might make a spout one eventually)
    List<Player> usersInChannel; //duh
    public boolean isPassworded;
    public String password;
    
    /**
     * Creates a new channel with the specified name
     * 
     * @param s the name of the channel
     */
    public Channel(String s) {
        this.name = s;
    }
    
    public void addPlayer(Player p) {
        usersInChannel.add(p);
    }
    
    public void rmPlayer(Player p) {
        if (usersInChannel.contains(p)) {
            usersInChannel.remove(p);
        }
    }
    
}

package net.rymate.bchatmanager;

import java.util.List;
import org.bukkit.entity.Player;

/**
 * Represents a chat channel. These will be managed by ChannelManager hopefully.
 * You'll also be able to hook one directly by getting them through the ChannelManager
 * That's if I ever get round to doing an API ;)
 * It'll definitely be used internally :D
 * 
 * @author rymate
 */
public class Channel {

    private String name; //kinda obvious
    private boolean isPublic; //in case I implement a list of channels (might make a spout one eventually)
    private List<Player> usersInChannel; //duh
    private boolean isPassworded;
    private String password;

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

    public List<Player> getPlayersInChannel() {
        return usersInChannel;
    }
    
    public String getName() {
        return name;
    }

    public boolean checkPassword(String s) {
        if (isPassworded) {
            if (s == password) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

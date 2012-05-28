package net.rymate.bchatmanager.channels;

import java.io.Serializable;
import java.util.List;
import net.rymate.bchatmanager.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Represents a chat channel. These will be managed by ChannelManager hopefully.
 * You'll also be able to hook one directly by getting them through the ChannelManager
 * That's if I ever get round to doing an API ;)
 * It'll definitely be used internally :D
 * 
 * @author rymate
 */
public class Channel implements Serializable {

    private String name; //kinda obvious
    private List<String> usersInChannel; //duh
    private String colour; //colour of the channel (for formatting)

    /**
     * Creates a new channel with the specified name
     * 
     * @param s the name of the channel
     */
    public Channel(String s) {
        this.name = s;
    }

    public void addPlayer(Player p) {
        usersInChannel.add(p.getName());
    }

    public void rmPlayer(Player p) {
        if (usersInChannel.contains(p.getName())) {
            usersInChannel.remove(p.getName());
        }
    }

    public List<String> getPlayersInChannel() {
        return usersInChannel;
    }
    
    public String getName() {
        return name;
    }
    
    public void destory() {
        for (int i = 0; i < usersInChannel.size(); i++) {
            rmPlayer(Bukkit.getPlayer(usersInChannel.get(i)));
            Messages.CHANNEL_REMOVED.send(Bukkit.getPlayer(usersInChannel.get(i)));
        }
    }
}

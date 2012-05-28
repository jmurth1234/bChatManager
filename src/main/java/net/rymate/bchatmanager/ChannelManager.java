/*
 * Class file for the storage ad manegment of chat channels
 * Provides an easy way to make sure they are accessable. basically every class 
 * that requires access to the chat channels will go via this.
 */

package net.rymate.bchatmanager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ryan
 */
public class ChannelManager implements Serializable {
    
    private List<Channel> channels; //duh
    
    /**
     * The following hashmap stores 2 strings
     * The player name and the channel name
     * Its basically so I can keep track of the player's active channel
     */
    private Map<String, String> activeChannel = new HashMap<String, String>();
    
    public Channel getChannel(String name) {
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getName().equals(name)) {
                return channels.get(i);
            } 
        }
        return null;
    }
    
    public void addChannel(String s) {
        Channel c = new Channel(s);
        channels.add(c);
    }
    
    public void rmChannel(String s) {
        Channel c = getChannel(s);
        if (c == null) {
            Messages.CHANNEL_REMOVE_ERROR.print();
            return;
        }
        c.destory();
        channels.remove(c);
    }
    
    
    /** 
     * This gets the players active channel (the one they are chatting in).
     * If they are in a channel, return that channel
     * If not, return the default global channel :D
     * 
     * @param p the name of the player
     * @return The players current channel
     */
    
    public Channel getActiveChannel(String p) {
        return getChannel("global");
    }
    
}

/*
 * Class file for the storage ad manegment of chat channels
 * Provides an easy way to make sure they are accessable. basically every class 
 * that requires access to the chat channels will go via this.
 */
package net.rymate.bchatmanager;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Ryan
 */
public class ChannelManager implements Serializable {
    
    private List<Channel> channels; //duh
    
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
    
}

/*
 * Class file for the storage ad manegment of chat channels
 * Provides an easy way to make sure they are accessable. basically every class 
 * that requires access to the chat channels will go via this.
 */
package net.rymate.bchatmanager.channels;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.rymate.bchatmanager.Messages;
import net.rymate.bchatmanager.util.SLAPI;
import org.bukkit.Bukkit;

/**
 *
 * @author Ryan
 */
public class ChannelManager implements Serializable {

    private List<Channel> channels; //duh
    /**
     * The following hashmap stores 2 strings The player name and the channel
     * name Its basically so I can keep track of the player's active channel
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
     * This gets the players active channel (the one they are chatting in). If
     * they are in a channel, return that channel If not, return the default
     * global channel :D
     *
     * @param p the name of the player
     * @return The players current channel
     */
    public Channel getActiveChannel(String p) {
        String chan = activeChannel.get(p);
        if (chan != null) {
            return getChannel(chan); //does an internet
        } else {
            return getChannel("global"); //does a book
        }
    }

    /**
     * This sets the players active channel. Returns true if the channel exists,
     * false otherwise.
     *
     * @param p the players's name
     * @return a boolean
     */
    public boolean setActiveChannel(String p, String chan) {
        if (getChannel(chan) != null) {
            activeChannel.put(p, chan);
            Messages.CHANNEL_ACTIVE.sendFormatted(Bukkit.getPlayer(p), "%channel", chan);
            return true;
        } else {
            return false;
        }
    }

    public boolean load() {
        if (new File("channels.bin").exists()) {
            try {
                //lets load shitty uneditable persistance!
                channels = (List<Channel>) SLAPI.load("channels.bin");
                activeChannel = (Map<String, String>) SLAPI.load("active_channel.bin");
                return true;
                //yay, it worked!
            } catch (Exception e) {
                //something went wrong!
                Messages.ERR.printErr("%error", e.getMessage());
                e.printStackTrace();
                Messages.ERR_END.printErr(null, null);
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean save() {
        try {
            SLAPI.save(channels, "channels.bin");
            SLAPI.save(activeChannel, "active_channel.bin");
            return true;
        } catch (Exception e) {
            //something went wrong!
            Messages.ERR.printErr("%error", e.getMessage());
            e.printStackTrace();
            Messages.ERR_END.printErr(null, null);
            return false;
        }
    }
}

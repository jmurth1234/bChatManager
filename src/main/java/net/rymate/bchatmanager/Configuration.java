package net.rymate.bchatmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Ryan
 */
public class Configuration {

    YamlConfiguration config;
    File f;

    public Configuration(File f) {
        config = new YamlConfiguration();
        this.f = f;
    }

    public boolean getBoolean(String s, boolean b) {
        return config.getBoolean(s, b);
    }

    public String getString(String s, String ss) {
        return config.getString(s, ss);
    }

    double getDouble(String s, double dd) {
        return config.getDouble(s, dd);
    }

    public void init(bChatManager p) {
        try {
            if (!f.exists()) {
                p.saveDefaultConfig();
            }
            config.load(f);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

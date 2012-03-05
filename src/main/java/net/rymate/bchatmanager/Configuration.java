package net.rymate.bchatmanager;

import java.io.File;
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
        try {
            if (f.exists()) {
                config.load(f);
            } else {
                f.createNewFile();
                config.load(f);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
    public void initialize () {
        
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
}

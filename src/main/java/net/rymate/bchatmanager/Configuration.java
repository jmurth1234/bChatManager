package net.rymate.bchatmanager;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Ryan
 */
public class Configuration {

    YamlConfiguration config;

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
}

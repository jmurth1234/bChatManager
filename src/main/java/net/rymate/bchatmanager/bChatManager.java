package net.rymate.bchatmanager;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// lel factions

/**
 * Main class
 *
 * @author rymate
 */
public class bChatManager extends JavaPlugin {

    public static Chat chat = null;
    private bChatListener listener;
    private YamlConfiguration config;
    private boolean factions = false;
    private boolean mv;
    private MultiverseCore core;
    private PluginCommand meCmd;

    public void onEnable() {
        meCmd = this.getCommand("me");
        //setup the config
        setupConfig();

        //Chatlistener - can you hear me?
        this.listener = new bChatListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);

        //Vault chat hooks
        setupChat();

        //check if factions is installed
        if (this.getServer().getPluginManager().isPluginEnabled("Factions")) {
            factions = true;
        }

        //check if Multiverse-Core is installed
        if (this.getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {
            mv = true;
            core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        }

        System.out.println("[bChatManager] Enabled");
    }

    @Override
    public void onDisable() {
        // make null all the things
        config = null;
        listener = null;
        System.out.println("[bChatManager] Disabled");
    }

    private void setupConfig() {
        File configFile = new File(this.getDataFolder() + File.separator + "config.yml");
        try {
            if (!configFile.exists()) this.saveDefaultConfig();

            config = new YamlConfiguration();
            config.load(configFile);

            if (config.getBoolean("toggles.control-me", true)) {
                registerBukkitCommand(meCmd);
            } else {
                unRegisterBukkitCommand(meCmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /*
     * Code to setup the Chat variable in Vault. Allows me to hook to all the prefix plugins.
     */
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    @Override
    public YamlConfiguration getConfig() {
        if (config == null) {
            setupConfig();
        }
        return config;
    }

    //
    //  Begin methods from Functions.java
    //
    public String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
        if (factions) {
            format = format.replaceAll("%faction", this.getFaction(player));
        } else {
            format = format.replaceAll("%faction", "");
        }

        MultiverseWorld mvWorld = null;
        if (mv) {
            mvWorld = core.getMVWorldManager().getMVWorld(player.getWorld());
        }
        if (mvWorld != null) {
            format = format.replaceAll("%mvworld", mvWorld.getColoredWorldString());
        } else {
            format = format.replaceAll("%mvworld", "");
        }
        return format.replaceAll("%prefix", chat.getPlayerPrefix(player))
                .replaceAll("%suffix", chat.getPlayerSuffix(player))
                .replaceAll("%world", worldName)
                .replaceAll("%uuid", player.getUniqueId().toString()) // for people who really want UUIDs in chat
                .replaceAll("%player", player.getName())
                .replaceAll("%displayname", player.getDisplayName())
                .replaceAll("%group", chat.getPrimaryGroup(player));
    }

    public String colorize(String string) {
        if (string == null) {
            return "";
        }

        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }

    public List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : getServer().getOnlinePlayers()) {
            // Recipient are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }
            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) {
                continue;
            }
            recipients.add(recipient);
        }
        return recipients;
    }

    private String getFaction(Player player) {
        String factionString = "";
        try {
            MPlayer uplayer = MPlayer.get(player);
            Faction faction = uplayer.getFaction();
            factionString = faction.getName();
        } catch (Exception e) {
            System.out.println("Factions support failed! Disabling factions support.");
            factions = false;
        }

        return factionString;
    }

    public List<Player> getSpies() {
        List<Player> recipients = new LinkedList<Player>();
        for (Player recipient : this.getServer().getOnlinePlayers()) {
            if (recipient.hasPermission("bchatmanager.spy")) {
                recipients.add(recipient);
            }
        }
        return recipients;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((command.getName().equalsIgnoreCase("me")) && (config.getBoolean("toggles.control-me", true))) {
            String meFormat = config.getString("formats.me-format", "* %player %message");
            Double chatRange = config.getDouble("other.chat-range", 100);
            boolean rangedMode = config.getBoolean("toggles.ranged-mode", false);
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Ya need to type something after it :P");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("bchatmanager.me")) {
                sender.sendMessage(ChatColor.RED + "You dont have permissions to do this!");
                return true;
            }

            int i;
            StringBuilder me = new StringBuilder();

            for (i = 0; i < args.length; i++) {
                me.append(args[i]);
                me.append(" ");
            }

            String meMessage = me.toString();
            String message = meFormat;

            if (sender.hasPermission("bchatmanager.chat.color")) {
                meMessage = colorize(meMessage);
            }

            message = replacePlayerPlaceholders(player, message);
            message = colorize(message);

            message = message.replace("%message", meMessage);

            if (rangedMode) {
                List<Player> pl = getLocalRecipients(player, message, chatRange);
                for (int j = 0; j < pl.size(); j++) {
                    pl.get(j).sendMessage(message);
                }
                sender.sendMessage(message);
                System.out.println(message);
            } else {
                getServer().broadcastMessage(message);
            }
            return true;
        }

        if ((command.getName().equals("bchatreload"))) {
            if (!(sender instanceof Player)) {
                setupConfig();
                listener.reloadConfig();
                sender.sendMessage(ChatColor.AQUA + "[bChatManager] Configs reloaded!");
                return true;
            }

            if (!sender.hasPermission("bchatmanager.reload")) {
                sender.sendMessage(ChatColor.AQUA + "[bChatManager] Wtf, you can't do this!");
                return true;
            }

            setupConfig();
            listener.reloadConfig();
            sender.sendMessage(ChatColor.AQUA + "[bChatManager] Configs reloaded!");
            return true;
        }
        return false;
    }

    // reflection hacks
    // thanks to zeeveener from https://bukkit.org/threads/how-to-unregister-commands-from-your-plugin.131808/
    private Object getPrivateField(Object object, String field) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public void unRegisterBukkitCommand(PluginCommand cmd) {
        try {
            Object result = getPrivateField(getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (String alias : cmd.getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName())) {
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerBukkitCommand(PluginCommand cmd) {
        try {
            Object result = getPrivateField(getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.put(cmd.getName(), meCmd);
            for (String alias : cmd.getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName())) {
                    knownCommands.put(alias, meCmd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

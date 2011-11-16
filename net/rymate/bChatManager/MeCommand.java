package net.rymate.bChatManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Ryan
 */
class MeCommand implements CommandExecutor {
    private final bChatManager plugin;
    private final String meFormat;
    private final bChatFormatter f;

    public MeCommand(FileConfiguration config, bChatManager aThis) {
        this.meFormat = config.getString("me-format", this.meFormat);
        this.plugin = aThis;
        this.f = new bChatFormatter(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Ya need to type something after it :P");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
            return true;
        }
        Player player = (Player) sender;
        int i;
        StringBuilder me = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            me.append(" ");
            me.append(args[i]);
        }
        String meMessage = me.toString();
        String message = meFormat;
        message = f.colorize(message);

        if (sender.hasPermission("bchatmanager.chat.color")) {
            meMessage = f.colorize(meMessage);
        }

        message = message.replace("%message", meMessage).replace("%displayname", "%1$s");
        message = f.replacePlayerPlaceholders(player, message);
        message = f.replaceTime(message);

        plugin.getServer().broadcastMessage(message);
        return true;
    }
}

package net.rymate.bchatmanager;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
    private final boolean rangedMode;
    private final double chatRange;

    public MeCommand(FileConfiguration config, bChatManager aThis) {
        this.meFormat = config.getString("me-format", this.meFormat);
        this.chatRange = config.getDouble("chat-range", this.chatRange);
        this.rangedMode = config.getBoolean("ranged-mode", this.rangedMode);
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
            me.append(args[i]);
            me.append(" ");
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

        if (rangedMode) {
            List<Player> pl = getLocalRecipients(player, message, chatRange);
            for (int j = 0; j < pl.size(); j++) {
                pl.get(j).sendMessage(message);
            }
            sender.sendMessage(message);
            System.out.println(message);
        } else {
            plugin.getServer().broadcastMessage(message);
        }
        return true;
    }

    protected List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
            // Recipient are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }

            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance && !sender.hasPermission("chatmanager.override.ranged")) {
                continue;
            }

            recipients.add(recipient);
        }
        return recipients;
    }
}

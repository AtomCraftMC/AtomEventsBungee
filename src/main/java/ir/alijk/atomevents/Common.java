package ir.alijk.atomevents;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class Common {
        public static String colorize(String text) {
                return ChatColor.translateAlternateColorCodes('&', text);
        }

        public static void send(CommandSender sender, String text) {
                sender.sendMessage(colorize(AtomEvents.getInstance().PREFIX + text));
        }
}

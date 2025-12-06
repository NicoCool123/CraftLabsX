package CraftLabsX.bansystem;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class BanCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = ChatColor.DARK_RED + "[BanSystem] " + ChatColor.RESET;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = label.toLowerCase();

        if (args.length < 1) {
            sendUsage(sender, cmd);
            return true;
        }

        String targetName = args[0];
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
        BanList profileBanList = Bukkit.getBanList(BanList.Type.PROFILE);

        switch (cmd) {
            case "tempban":
                handleTempBan(sender, profileBanList, offlineTarget, args);
                break;
            case "permaban":
                handlePermBan(sender, profileBanList, offlineTarget, args);
                break;
            case "unban":
                handleUnban(sender, profileBanList, offlineTarget);
                break;
            default:
                sender.sendMessage(PREFIX + ChatColor.RED + "Unknown command: " + cmd);
        }
        return true;
    }

    private void handleTempBan(CommandSender sender, BanList banList, OfflinePlayer target, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /tempban <player> <duration> [reason]");
            return;
        }

        long millis = parseDuration(args[1]);
        if (millis <= 0) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Invalid duration! Use 1h, 2d, 30m, etc.");
            return;
        }

        Instant expiry = Instant.now().plusMillis(millis);
        String reason = (args.length > 2) ? joinArgs(args, 2) : ChatColor.RED + "Temporary ban";

        // Always convert Instant -> Date to avoid ambiguous call
        banList.addBan(target.getPlayerProfile(), reason, Date.from(expiry), sender.getName());

        kickIfOnline(target.getName(), reason, expiry);
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Temp-banned " + target.getName() +
                " until " + formatInstant(expiry));
    }

    private void handlePermBan(CommandSender sender, BanList banList, OfflinePlayer target, String[] args) {
        String reason = (args.length > 1) ? joinArgs(args, 1) : ChatColor.RED + "Permanently banned";

        // Use Date null for permanent ban
        banList.addBan(target.getPlayerProfile(), reason, (Date) null, sender.getName());

        kickIfOnline(target.getName(), reason, null);
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Permanently banned " + target.getName());
    }

    private void handleUnban(CommandSender sender, BanList banList, OfflinePlayer target) {
        banList.pardon(target.getPlayerProfile());
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Unbanned " + target.getName());
    }

    private void kickIfOnline(String targetName, String reason, Instant expiry) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target != null) {
            String message = reason;
            if (expiry != null) {
                message += "\nUntil: " + formatInstant(expiry);
            }
            target.kickPlayer(message);
        }
    }

    private long parseDuration(String input) {
        try {
            long num = Long.parseLong(input.substring(0, input.length() - 1));
            char unit = Character.toLowerCase(input.charAt(input.length() - 1));
            switch (unit) {
                case 's': return num * 1000;
                case 'm': return num * 60 * 1000;
                case 'h': return num * 60 * 60 * 1000;
                case 'd': return num * 24 * 60 * 60 * 1000;
                case 'w': return num * 7 * 24 * 60 * 60 * 1000;
                default: return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private String joinArgs(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    private String formatInstant(Instant instant) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date.from(instant));
    }

    private void sendUsage(CommandSender sender, String command) {
        switch (command) {
            case "tempban":
                sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /tempban <player> <duration> [reason]");
                break;
            case "permaban":
                sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /permaban <player> [reason]");
                break;
            case "unban":
                sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /unban <player>");
                break;
            default:
                sender.sendMessage(PREFIX + ChatColor.RED + "Unknown usage.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String cmd = alias.toLowerCase();

        if (args.length == 1) {
            if (cmd.equals("unban")) {
                completions.addAll(Bukkit.getBanList(BanList.Type.PROFILE).getBanEntries()
                        .stream()
                        .map(entry -> entry.getTarget()) // String names
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            } else {
                completions.addAll(Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()));
            }
        } else if (args.length == 2 && cmd.equals("tempban")) {
            completions.addAll(Arrays.asList("30m", "1h", "2h", "1d", "1w"));
        }
        return completions;
    }
}
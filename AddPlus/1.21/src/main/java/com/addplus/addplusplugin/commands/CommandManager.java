package com.addplus.addplusplugin.commands;

import com.addplus.addplusplugin.AddPlusPlugin;
import com.addplus.addplusplugin.items.CustomItem;
// No need to import GuiManager here for the command, plugin instance will hold it
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final AddPlusPlugin plugin;
    private static final int ITEMS_PER_PAGE = 8;

    public CommandManager(AddPlusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender, label);
                break;
            case "reload":
                if (!sender.hasPermission("addplus.reload")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                plugin.getItemConfigLoader().loadItems();
                sender.sendMessage(ChatColor.GREEN + "AddPlus configurations and items have been reloaded.");
                plugin.getLogger().info("AddPlus configurations reloaded by " + sender.getName() + ". " + plugin.getItemRegistry().getAllItems().size() + " items loaded.");
                break;
            case "give":
                if (!sender.hasPermission("addplus.give")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                handleGiveCommand(sender, args, label);
                break;
            case "list":
                if (!sender.hasPermission("addplus.list")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                handleListCommand(sender, args, label);
                break;
            case "gui":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                    return true;
                }
                if (!sender.hasPermission("addplus.gui")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                plugin.getGuiManager().openPrimaryGui((Player) sender, 1);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /" + label + " help for available commands.");
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GOLD + "--- AddPlus Help ---");
        sender.sendMessage(ChatColor.AQUA + "/" + label + " help" + ChatColor.WHITE + " - Shows this help message.");
        sender.sendMessage(ChatColor.AQUA + "/" + label + " reload" + ChatColor.WHITE + " - Reloads item configurations (addplus.reload).");
        sender.sendMessage(ChatColor.AQUA + "/" + label + " give <player> <item_id> [amount]" + ChatColor.WHITE + " - Gives a custom item (addplus.give).");
        sender.sendMessage(ChatColor.AQUA + "/" + label + " list [page]" + ChatColor.WHITE + " - Lists all custom items (addplus.list).");
        sender.sendMessage(ChatColor.AQUA + "/" + label + " gui" + ChatColor.WHITE + " - Opens the item browser GUI (addplus.gui).");
    }

    private void handleGiveCommand(CommandSender sender, String[] args, String label) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " give <player> <item_id> [amount]");
            return;
        }
        Player targetPlayer = Bukkit.getPlayerExact(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found or is not online.");
            return;
        }
        String itemId = args[2];
        Optional<CustomItem> customItemOpt = plugin.getItemRegistry().getItem(itemId);
        if (!customItemOpt.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Custom item with ID '" + itemId + "' not found.");
            String availableItems = plugin.getItemRegistry().getAllItems().stream().map(CustomItem::getId).collect(Collectors.joining(", "));
            if (availableItems.isEmpty()) availableItems = "None";
            sender.sendMessage(ChatColor.YELLOW + "Registered items: " + availableItems);
            return;
        }
        CustomItem customItem = customItemOpt.get();
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Amount must be a positive integer.");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount specified. Must be a number.");
                return;
            }
        }
        ItemStack itemStack = customItem.createItemStack(amount);
        if (!targetPlayer.getInventory().addItem(itemStack).isEmpty()) {
             targetPlayer.getWorld().dropItemNaturally(targetPlayer.getLocation(), itemStack);
             targetPlayer.sendMessage(ChatColor.YELLOW + "Your inventory was full, so " + amount + "x " + customItem.getDisplayName() + ChatColor.YELLOW + " was dropped nearby.");
             if (!(sender instanceof Player) || !((Player)sender).getUniqueId().equals(targetPlayer.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + targetPlayer.getName() + "'s inventory was full. The item(s) were dropped at their location.");
             }
        } else {
            targetPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + "x " + customItem.getDisplayName() + ChatColor.GREEN + "!");
            if (!(sender instanceof Player) || !((Player)sender).getUniqueId().equals(targetPlayer.getUniqueId())) {
                sender.sendMessage(ChatColor.GREEN + "Gave " + amount + "x " + customItem.getDisplayName() + ChatColor.GREEN + " to " + targetPlayer.getName() + ".");
            }
        }
    }

    private void handleListCommand(CommandSender sender, String[] args, String label) {
        List<CustomItem> allItems = new ArrayList<>(plugin.getItemRegistry().getAllItems());
        if (allItems.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "There are no custom items currently registered.");
            return;
        }
        Collections.sort(allItems, (item1, item2) -> item1.getId().compareToIgnoreCase(item2.getId()));
        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid page number. Please use a number.");
                return;
            }
        }
        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        if (totalPages == 0) page = 1;
        sender.sendMessage(ChatColor.GOLD + "--- Custom Items (Page " + page + "/" + totalPages + ") ---");
        if (allItems.isEmpty() && totalPages == 0) {
             sender.sendMessage(ChatColor.YELLOW + "No items to display.");
             return;
        }
        if (page > totalPages && totalPages > 0) {
            sender.sendMessage(ChatColor.RED + "Invalid page number. Maximum page is " + totalPages);
            return;
        }
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());
        for (int i = startIndex; i < endIndex; i++) {
            CustomItem item = allItems.get(i);
            sender.sendMessage(ChatColor.AQUA + item.getId() + ChatColor.WHITE + " - " + ChatColor.translateAlternateColorCodes('&', item.getDisplayName()));
        }
        if (totalPages > 1) {
            String nextPageCommand = "/" + label + " list " + (page + 1);
            String prevPageCommand = "/" + label + " list " + (page - 1);
            String navigationMessage = "";
            if (page > 1) {
                 navigationMessage += ChatColor.YELLOW + "<< Prev (" + prevPageCommand + ChatColor.YELLOW + ")";
            }
            if (page < totalPages) {
                if (!navigationMessage.isEmpty()) navigationMessage += ChatColor.WHITE + " | ";
                navigationMessage += ChatColor.YELLOW + "Next >> (" + nextPageCommand + ChatColor.YELLOW + ")";
            }
             if (!navigationMessage.isEmpty()) sender.sendMessage(navigationMessage);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> currentArgCompletions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "reload", "give", "list", "gui");
            for (String sub : subCommands) {
                if (sub.equals("reload") && !sender.hasPermission("addplus.reload")) continue;
                if (sub.equals("give") && !sender.hasPermission("addplus.give")) continue;
                if (sub.equals("list") && !sender.hasPermission("addplus.list")) continue;
                if (sub.equals("gui") && !sender.hasPermission("addplus.gui")) continue;
                if (sub.equals("gui") && !(sender instanceof Player)) continue; // GUI only for players
                currentArgCompletions.add(sub);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("addplus.give")) {
                 Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .forEach(currentArgCompletions::add);
            } else if (args[0].equalsIgnoreCase("list") && sender.hasPermission("addplus.list")) {
                int totalItems = plugin.getItemRegistry().getAllItems().size();
                int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE); // Use ITEMS_PER_PAGE from list command
                if (totalPages > 1) {
                    for (int i = 1; i <= totalPages; i++) {
                        currentArgCompletions.add(String.valueOf(i));
                    }
                }
            }
            // No args for 'gui' or 'reload' at this point
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("addplus.give")) {
                plugin.getItemRegistry().getAllItems().stream()
                    .map(CustomItem::getId)
                    .forEach(currentArgCompletions::add);
            }
        } else if (args.length == 4) {
             if (args[0].equalsIgnoreCase("give") && sender.hasPermission("addplus.give")) {
                Arrays.asList("1", "16", "32", "64").forEach(currentArgCompletions::add);
            }
        }

        // Filter completions based on current input
        String currentInput = args[args.length-1].toLowerCase();
        for(String s : currentArgCompletions){
            if(s.toLowerCase().startsWith(currentInput)){
                completions.add(s);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}

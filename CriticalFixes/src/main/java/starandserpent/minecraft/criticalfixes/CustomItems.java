package starandserpent.minecraft.criticalfixes;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CustomItems implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    // Constructor.
    public CustomItems(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        boolean isAllowedToUsecommand = false;
        if (sender instanceof ConsoleCommandSender) {
            isAllowedToUsecommand = true;
        }

        if (sender.isOp()) {
            isAllowedToUsecommand = true;
        }

        if (!isAllowedToUsecommand) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("You need to provide an argument.");
            return false;
        }

        if (args.length > 3) {
            sender.sendMessage("Too many arguments.");
            return false;
        }

        if (args.length == 1) {
            sender.sendMessage("You need to provide an item name.");
            return false;
        }

        Player player = getPlayerReference(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        String itemName = args[1];

        CustomItemsEnum customItem = null;
        try {
            // Try to get the CustomItemsEnum for the itemName.
            customItem = CustomItemsEnum.getMaterial(itemName);

        } catch (IllegalArgumentException e) {
            sender.sendMessage("Item not found.");
            return false;
        }

        int amount = 1;

        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Amount must be a number.");
                return false;
            }
        }

        if (command.getName().equalsIgnoreCase("anna")) {
            System.out.println("Anna command executed.");
            return giveCustomItem(player, customItem, amount);
        }

        return false;
    }


    private boolean giveCustomItem(Player player, CustomItemsEnum customItemsEnum, int amount) {

        // Create a custom item stack.
        var customItemMaterial = customItemsEnum.getMaterial();
        var customItemId = customItemsEnum.getId();
        var customItemName = customItemsEnum.getItemName();

        ItemStack itemStack = new ItemStack(customItemMaterial, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            player.sendMessage("Item meta is null. Cannot give item.");
            return false;
        }
        itemMeta.setCustomModelData(customItemId);
        itemMeta.setItemName(customItemName);
        itemStack.setItemMeta(itemMeta);

        // Check if player has space for the item stack.
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("Your inventory is full. Cannot give item.");
            return false;
        }

        // Give the item stack to player.
        player.sendMessage("Gave " + amount + " " + customItemName + " to " + player.getName());
        player.getInventory().addItem(itemStack);

        return true;
    }

    private Player getPlayerReference(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        return player;
    }

}

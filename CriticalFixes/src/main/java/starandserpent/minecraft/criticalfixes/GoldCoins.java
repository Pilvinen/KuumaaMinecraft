package starandserpent.minecraft.criticalfixes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class GoldCoins implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public GoldCoins(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        registerGoldCoinPileRecipe();
        registerGoldCoinStackRecipe();
        registerGoldCoinsRecipe();
        registerGoldCoinsFromStackRecipe();
    }

    // Register gold coins to gold coin pile recipe.
    private void registerGoldCoinPileRecipe() {
        // Create the result item (shulker shell with custom data)
        ItemStack result = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282032);
            meta.setItemName("Kasa kultakolikoita");
            result.setItemMeta(meta);
        }

        // Define the shaped recipe
        NamespacedKey key = new NamespacedKey(plugin, "GOLD_COIN_PILE");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("G");

        // Create a custom RecipeChoice for 64 gold nuggets
        ItemStack goldNuggetStack = new ItemStack(Material.GOLD_NUGGET, 64);
        RecipeChoice.ExactChoice goldNuggetChoice = new RecipeChoice.ExactChoice(goldNuggetStack);

        // Set the ingredient for the recipe
        recipe.setIngredient('G', goldNuggetChoice);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    // Register gold coins to gold coin stack recipe.
    private void registerGoldCoinStackRecipe() {
        // Create the result item (shulker shell with custom data)
        ItemStack result = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282033);
            meta.setItemName("Pino kultakolikoita");
            result.setItemMeta(meta);
        }

        // Define the shaped recipe
        NamespacedKey key = new NamespacedKey(plugin, "GOLD_COIN_STACK");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("GG", "GG");

        // Create a custom RecipeChoice for 64 gold nuggets
        ItemStack goldNuggetStack = new ItemStack(Material.GOLD_NUGGET, 16);
        RecipeChoice.ExactChoice goldNuggetChoice = new RecipeChoice.ExactChoice(goldNuggetStack);

        // Set the ingredient for the recipe
        recipe.setIngredient('G', goldNuggetChoice);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    // Register gold coin pile to gold coins recipe.
    private void registerGoldCoinsRecipe() {
        // Create the result item (64 ancient gold coins)
        ItemStack result = new ItemStack(Material.GOLD_NUGGET, 64);

        // Define the recipe
        NamespacedKey key = new NamespacedKey(plugin, "GOLD_COINS");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        // Create a custom ingredient choice for shulker shells with custom model data 282032
        ItemStack shulkerShell = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = shulkerShell.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282032);
            meta.setItemName("Kasa kultakolikoita");
            shulkerShell.setItemMeta(meta);
        }
        recipe.addIngredient(new RecipeChoice.ExactChoice(shulkerShell));

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    // Register gold coin stack to gold coins recipe.
    private void registerGoldCoinsFromStackRecipe() {
        // Create the result item (64 ancient gold coins)
        ItemStack result = new ItemStack(Material.GOLD_NUGGET, 64);

        // Define the recipe
        NamespacedKey key = new NamespacedKey(plugin, "GOLD_COINS_FROM_STACK");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        // Create a custom ingredient choice for shulker shells with custom model data 282033
        ItemStack shulkerShell = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = shulkerShell.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282033);
            meta.setItemName("Pino kultakolikoita");
            shulkerShell.setItemMeta(meta);
        }
        recipe.addIngredient(new RecipeChoice.ExactChoice(shulkerShell));

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    @EventHandler public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult().getType() == Material.SHULKER_SHELL) {
            ItemStack[] matrix = event.getInventory().getMatrix();

            var recipe = event.getRecipe();
            var result = recipe.getResult();
            // Check for specific meta data custom model values.
            if (!result.hasItemMeta()) {
                return;
            }
            var meta = event.getRecipe().getResult().getItemMeta();
            if (!meta.hasCustomModelData()) {
                return;
            }

            var customModelData = meta.getCustomModelData();
            if (!(customModelData == 282032 // Gold coin pile
                    || customModelData == 282033)) { // Gold coin stack
                return;
            }

            boolean isGoldCoinPile = false;
            boolean isGoldCoinStack = false;

            for (ItemStack item : matrix) {
                if (item != null && item.getType() == Material.GOLD_NUGGET) {
                    if (item.getAmount() == 64) {
                        isGoldCoinPile = true;
                    } else if (item.getAmount() == 16) {
                        isGoldCoinStack = true;
                    } else {
                        event.getInventory().setResult(null);
                        return;
                    }
                }
            }

            if (isGoldCoinPile) {
                for (ItemStack item : matrix) {
                    if (item != null && item.getType() == Material.GOLD_NUGGET && item.getAmount() < 64) {
                        event.getInventory().setResult(null);
                        return;
                    }
                }
            } else if (isGoldCoinStack) {
                int totalGoldNuggets = 0;
                for (ItemStack item : matrix) {
                    if (item != null && item.getType() == Material.GOLD_NUGGET) {
                        totalGoldNuggets += item.getAmount();
                    }
                }
                if (totalGoldNuggets < 64) {
                    event.getInventory().setResult(null);
                }
            }

        }
    }

    @EventHandler public void onCraftItem(CraftItemEvent event) {

        var recipe = event.getRecipe();
        var result = recipe.getResult();
        var meta = result.getItemMeta();
        if (meta == null) {
            return;
        }
        if (!meta.hasCustomModelData()) {
            return;
        }
        var customModelData = meta.getCustomModelData();

        if (result.getType() == Material.SHULKER_SHELL && customModelData == 282032) {

            ItemStack[] matrix = event.getInventory().getMatrix();

            for (ItemStack item : matrix) {
                if (item != null && item.getType() == Material.GOLD_NUGGET) {
                    if (item.getAmount() == 64) {
                        item.setAmount(0); // Remove the entire stack
                        return;
                    }
                }
            }
        } else if (result.getType() == Material.SHULKER_SHELL && customModelData == 282033) {
            ItemStack[] matrix = event.getInventory().getMatrix();
            int totalGoldNuggets = 0;
            List<Integer> validIndices = new ArrayList<>();

            // First pass: Check if we have enough gold nuggets
            for (int i = 0; i < matrix.length; i++) {
                ItemStack item = matrix[i];
                if (item != null && item.getType() == Material.GOLD_NUGGET) {
                    totalGoldNuggets += item.getAmount();
                    validIndices.add(i);
                }
            }

            // If we have at least 64 gold nuggets, proceed to subtract 16 from each valid stack
            if (totalGoldNuggets >= 64) {
                for (int index : validIndices) {
                    ItemStack item = matrix[index];
                    int amountToSubtract = Math.min(16, item.getAmount());
                    item.setAmount(item.getAmount() - amountToSubtract);
                    if (item.getAmount() <= 0) {
                        matrix[index] = null; // Remove the stack if it's empty
                    }
                }
            } else {
                event.setCancelled(true); // Cancel the event if there aren't enough gold nuggets
            }
        }
    }

}

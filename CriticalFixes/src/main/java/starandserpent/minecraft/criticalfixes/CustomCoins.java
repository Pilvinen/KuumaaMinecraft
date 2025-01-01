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

public class CustomCoins implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public CustomCoins(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        registerGoldCoinPileRecipe();
        registerGoldCoinStackRecipe();
        registerGoldCoinsRecipe();
        registerGoldCoinsFromStackRecipe();
        registerIronCoinPileRecipe();
        registerIronCoinStackRecipe();
        registerIronCoinsRecipe();
        registerIronCoinsFromStackRecipe();
        registerNetheriteCoinPileRecipe();
        registerNetheriteCoinStackRecipe();
        registerNetheriteCoinsRecipe();
        registerNetheriteCoinsFromStackRecipe();
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

    private void registerIronCoinPileRecipe() {
        // Create the result item (shulker shell with custom data)
        ItemStack result = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282059);
            meta.setItemName("Kasa rautakolikoita");
            result.setItemMeta(meta);
        }

        // Define the shaped recipe
        NamespacedKey key = new NamespacedKey(plugin, "IRON_COIN_PILE");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("I");

        // Create a custom RecipeChoice for 64 iron nuggets
        ItemStack ironNuggetStack = new ItemStack(Material.IRON_NUGGET, 64);
        RecipeChoice.ExactChoice ironNuggetChoice = new RecipeChoice.ExactChoice(ironNuggetStack);

        // Set the ingredient for the recipe
        recipe.setIngredient('I', ironNuggetChoice);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerIronCoinStackRecipe() {
        // Create the result item (shulker shell with custom data)
        ItemStack result = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282060);
            meta.setItemName("Pino rautakolikoita");
            result.setItemMeta(meta);
        }

        // Define the shaped recipe
        NamespacedKey key = new NamespacedKey(plugin, "IRON_COIN_STACK");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("II", "II");

        // Create a custom RecipeChoice for 64 iron nuggets
        ItemStack ironNuggetStack = new ItemStack(Material.IRON_NUGGET, 16);
        RecipeChoice.ExactChoice ironNuggetChoice = new RecipeChoice.ExactChoice(ironNuggetStack);

        // Set the ingredient for the recipe
        recipe.setIngredient('I', ironNuggetChoice);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerIronCoinsRecipe() {
        // Create the result item (64 ancient iron coins)
        ItemStack result = new ItemStack(Material.IRON_NUGGET, 64);

        // Define the recipe
        NamespacedKey key = new NamespacedKey(plugin, "IRON_COINS");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        // Create a custom ingredient choice for shulker shells with custom model data 282059
        ItemStack shulkerShell = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = shulkerShell.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282059);
            meta.setItemName("Kasa rautakolikoita");
            shulkerShell.setItemMeta(meta);
        }
        recipe.addIngredient(new RecipeChoice.ExactChoice(shulkerShell));

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerIronCoinsFromStackRecipe() {
        // Create the result item (64 ancient iron coins)
        ItemStack result = new ItemStack(Material.IRON_NUGGET, 64);

        // Define the recipe
        NamespacedKey key = new NamespacedKey(plugin, "IRON_COINS_FROM_STACK");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        // Create a custom ingredient choice for shulker shells with custom model data 282060
        ItemStack shulkerShell = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = shulkerShell.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282060);
            meta.setItemName("Pino rautakolikoita");
            shulkerShell.setItemMeta(meta);
        }
        recipe.addIngredient(new RecipeChoice.ExactChoice(shulkerShell));

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerNetheriteCoinPileRecipe() {
        // Create the result item (shulker shell with custom data)
        ItemStack result = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282061);
            meta.setItemName("Kasa netheriittikolikoita");
            result.setItemMeta(meta);
        }

        // Define the shaped recipe
        NamespacedKey key = new NamespacedKey(plugin, "NETHERITE_COIN_PILE");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("N");

        // Create a custom RecipeChoice for 64 netherite scraps
        ItemStack netheriteScrapStack = new ItemStack(Material.NETHERITE_SCRAP, 64);
        RecipeChoice.ExactChoice netheriteScrapChoice = new RecipeChoice.ExactChoice(netheriteScrapStack);

        // Set the ingredient for the recipe
        recipe.setIngredient('N', netheriteScrapChoice);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerNetheriteCoinStackRecipe() {
        // Create the result item (shulker shell with custom data)
        ItemStack result = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282062);
            meta.setItemName("Pino netheriittikolikoita");
            result.setItemMeta(meta);
        }

        // Define the shaped recipe
        NamespacedKey key = new NamespacedKey(plugin, "NETHERITE_COIN_STACK");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("NN", "NN");

        // Create a custom RecipeChoice for 64 netherite scraps
        ItemStack netheriteScrapStack = new ItemStack(Material.NETHERITE_SCRAP, 16);
        RecipeChoice.ExactChoice netheriteScrapChoice = new RecipeChoice.ExactChoice(netheriteScrapStack);

        // Set the ingredient for the recipe
        recipe.setIngredient('N', netheriteScrapChoice);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerNetheriteCoinsRecipe() {
        // Create the result item (64 ancient netherite coins)
        ItemStack result = new ItemStack(Material.NETHERITE_SCRAP, 64);

        // Define the recipe
        NamespacedKey key = new NamespacedKey(plugin, "NETHERITE_COINS");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        // Create a custom ingredient choice for shulker shells with custom model data 282061
        ItemStack shulkerShell = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = shulkerShell.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282061);
            meta.setItemName("Kasa netheriittikolikoita");
            shulkerShell.setItemMeta(meta);
        }
        recipe.addIngredient(new RecipeChoice.ExactChoice(shulkerShell));

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    private void registerNetheriteCoinsFromStackRecipe() {
        // Create the result item (64 ancient netherite coins)
        ItemStack result = new ItemStack(Material.NETHERITE_SCRAP, 64);

        // Define the recipe
        NamespacedKey key = new NamespacedKey(plugin, "NETHERITE_COINS_FROM_STACK");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        // Create a custom ingredient choice for shulker shells with custom model data 282062
        ItemStack shulkerShell = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = shulkerShell.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(282062);
            meta.setItemName("Pino netheriittikolikoita");
            shulkerShell.setItemMeta(meta);
        }
        recipe.addIngredient(new RecipeChoice.ExactChoice(shulkerShell));

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }



    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
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

        ItemStack[] matrix = event.getInventory().getMatrix();
        if (result.getType() == Material.SHULKER_SHELL) {
            switch (customModelData) {
                case 282032: // Gold coin pile
                    handleGoldCoinPileCraft(matrix);
                    break;
                case 282033: // Gold coin stack
                    boolean materialsFoundForGoldCoinStack = handleGoldCoinStackCraft(matrix);
                    if (!materialsFoundForGoldCoinStack) {
                        event.setCancelled(true);
                    }
                    break;
                case 282059: // Iron coin pile
                    handleIronCoinPileCraft(matrix);
                    break;
                case 282060: // Iron coin stack
                    boolean materialsFoundForIronCoinStack = handleIronCoinStackCraft(matrix);
                    if (!materialsFoundForIronCoinStack) {
                        event.setCancelled(true);
                    }
                    break;
                case 282061: // Netherite coin pile
                    handleNetheriteCoinPileCraft(matrix);
                    break;
                case 282062: // Netherite coin stack
                    boolean materialsFoundForNetheriteCoinStack = handleNetheriteCoinStackCraft(matrix);
                    if (!materialsFoundForNetheriteCoinStack) {
                        event.setCancelled(true);
                    }
                    break;
            }
        }
    }

    private void handleGoldCoinPileCraft(ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() == Material.GOLD_NUGGET) {
                if (item.getAmount() == 64) {
                    item.setAmount(0); // Remove the entire stack
                    return;
                }
            }
        }
    }

    private boolean handleGoldCoinStackCraft(ItemStack[] matrix) {
        int totalGoldNuggets = 0;
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            ItemStack item = matrix[i];
            if (item != null && item.getType() == Material.GOLD_NUGGET) {
                totalGoldNuggets += item.getAmount();
                validIndices.add(i);
            }
        }
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
            return false; // Cancel the event if there aren't enough gold nuggets
        }
        // Success.
        return true;
    }

    private void handleIronCoinPileCraft(ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                if (item.getAmount() == 64) {
                    item.setAmount(0); // Remove the entire stack
                    return;
                }
            }
        }
    }

    private boolean handleIronCoinStackCraft(ItemStack[] matrix) {
        int totalIronNuggets = 0;
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            ItemStack item = matrix[i];
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                totalIronNuggets += item.getAmount();
                validIndices.add(i);
            }
        }
        if (totalIronNuggets >= 64) {
            for (int index : validIndices) {
                ItemStack item = matrix[index];
                int amountToSubtract = Math.min(16, item.getAmount());
                item.setAmount(item.getAmount() - amountToSubtract);
                if (item.getAmount() <= 0) {
                    matrix[index] = null; // Remove the stack if it's empty
                }
            }
        } else {
            return false; // Cancel the event if there aren't enough iron nuggets
        }
        // Success.
        return true;
    }

    private void handleNetheriteCoinPileCraft(ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() == Material.NETHERITE_SCRAP) {
                if (item.getAmount() == 64) {
                    item.setAmount(0); // Remove the entire stack
                    return;
                }
            }
        }
    }

    private boolean handleNetheriteCoinStackCraft(ItemStack[] matrix) {
        int totalNetheriteScrap = 0;
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            ItemStack item = matrix[i];
            if (item != null && item.getType() == Material.NETHERITE_SCRAP) {
                totalNetheriteScrap += item.getAmount();
                validIndices.add(i);
            }
        }
        if (totalNetheriteScrap >= 64) {
            for (int index : validIndices) {
                ItemStack item = matrix[index];
                int amountToSubtract = Math.min(16, item.getAmount());
                item.setAmount(item.getAmount() - amountToSubtract);
                if (item.getAmount() <= 0) {
                    matrix[index] = null; // Remove the stack if it's empty
                }
            }
        } else {
            return false; // Cancel the event if there aren't enough netherite scrap
        }
        // Success.
        return true;
    }

}
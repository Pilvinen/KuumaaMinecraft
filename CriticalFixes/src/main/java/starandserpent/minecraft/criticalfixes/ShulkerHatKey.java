package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Objects;

public class ShulkerHatKey {
    private final Material material;
    private final CustomItemsEnum customModelId;
    private final Permission hatPermission;

    public ShulkerHatKey(Material material, CustomItemsEnum customModelId, Permission hatPermission) {
        this.material = material;
        this.customModelId = customModelId;
        this.hatPermission = hatPermission;
    }

    public Material getMaterial() {
        return material;
    }

    public CustomItemsEnum getCustomModelId() {
        return customModelId;
    }

    public Permission getHatPermission() {
        return hatPermission;
    }

    public boolean canWearHat(Player player) {
        return player.hasPermission(hatPermission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShulkerHatKey that = (ShulkerHatKey) o;
        return customModelId == that.customModelId && material == that.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, customModelId);
    }
}
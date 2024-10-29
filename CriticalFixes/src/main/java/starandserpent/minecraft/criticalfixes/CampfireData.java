package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class CampfireData {
    private int sequenceNumber;
    private Material material;
    private BlockFace facing;
    private boolean isLit;

    public CampfireData(int sequenceNumber, Material material, BlockFace facing, boolean isLit) {
        this.sequenceNumber = sequenceNumber;
        this.material = material;
        this.facing = facing;
        this.isLit = isLit;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public Material getMaterial() {
        return material;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public boolean isLit() {
        return isLit;
    }

}

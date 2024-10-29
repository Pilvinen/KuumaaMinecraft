package starandserpent.minecraft.criticalfixes;

// Cardinal directions for serialization.
// This is used to determine which way a block is rotated and facing.
public enum Facing {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    UP,
    DOWN;

    // Turn BlockFace into Facing.
    public static Facing fromBlockFace(org.bukkit.block.BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> Facing.NORTH;
            case SOUTH -> Facing.SOUTH;
            case EAST -> Facing.EAST;
            case WEST -> Facing.WEST;
            case UP -> Facing.UP;
            case DOWN -> Facing.DOWN;
            default -> null;
        };
    }

    // String to facing.
    public static Facing fromString(String facingString) {
        String loweredFacingString = facingString.toLowerCase();
        return switch (loweredFacingString) {
            case "north" -> Facing.NORTH;
            case "south" -> Facing.SOUTH;
            case "east" -> Facing.EAST;
            case "west" -> Facing.WEST;
            case "up" -> Facing.UP;
            case "down" -> Facing.DOWN;
            default -> null;
        };
    }

    // From facing to BlockFace.
    public org.bukkit.block.BlockFace toBlockFace() {
        return switch (this) {
            case NORTH -> org.bukkit.block.BlockFace.NORTH;
            case SOUTH -> org.bukkit.block.BlockFace.SOUTH;
            case EAST -> org.bukkit.block.BlockFace.EAST;
            case WEST -> org.bukkit.block.BlockFace.WEST;
            case UP -> org.bukkit.block.BlockFace.UP;
            case DOWN -> org.bukkit.block.BlockFace.DOWN;
        };
    }

}

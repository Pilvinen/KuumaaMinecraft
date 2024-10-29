package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItemsEnum {

    // Sleeping pod with int associated with it.
    SLEEPING_POD_OPEN_FEET(282000, Material.SHULKER_SHELL, "sleepinging_pod_open_feet", 1),
    SLEEPING_POD_OPEN_HEAD(282001, Material.SHULKER_SHELL, "sleepinging_pod_open_head", 1),
    SLEEPING_POD_CLOSED_FEET(282002, Material.SHULKER_SHELL, "sleepinging_pod_closed_feet", 1),
    SLEEPING_POD_CLOSED_HEAD(282003, Material.SHULKER_SHELL, "sleepinging_pod_closed_head", 1),
    EYELIDS_2X2(282004, Material.SHULKER_SHELL, "eyelids_2x2", 1),
    FULL_FACE_TEXTURE_SMILE(282005, Material.SHULKER_SHELL, "full_face_texture_smile", 1),
    FULL_FACE_TEXTURE_SAD(282006, Material.SHULKER_SHELL, "full_face_texture_sad", 1),
    FULL_FACE_TEXTURE_LAUGH(282007, Material.SHULKER_SHELL, "full_face_texture_laugh", 1),
    FULL_FACE_TEXTURE_SERIOUS(282008, Material.SHULKER_SHELL, "full_face_texture_serious", 1),
    FULL_FACE_TEXTURE_SHOCK(282009, Material.SHULKER_SHELL, "full_face_texture_shock", 1),
    POCKETWATCH(282010, Material.SHULKER_SHELL, "pocketwatch", 1),
    BOOK_STACK_2(282011, Material.BOOK, "book_stack_2", 1),
    BOOK_STACK_3(282012, Material.BOOK, "book_stack_3", 1),
    BOOK_STACK_4(282013, Material.BOOK, "book_stack_4", 1),
    BOOK_STACK_5(282014, Material.BOOK, "book_stack_5", 1),
    BOOK_UPRIGHT_BACK_1(282015, Material.BOOK, "book_upright_back_1", 1),
    BOOK_UPRIGHT_BACK_2(282016, Material.BOOK, "book_upright_back_2", 1),
    BOOK_UPRIGHT_BACK_3(282017, Material.BOOK, "book_upright_back_3", 1),
    BOOK_UPRIGHT_BACK_4(282018, Material.BOOK, "book_upright_back_4", 1),
    BOOK_UPRIGHT_BACK_5(282019, Material.BOOK, "book_upright_back_5", 1),
    BOOK_UPRIGHT_MIDDLE_1(282020, Material.BOOK, "book_upright_middle_1", 1),
    BOOK_UPRIGHT_MIDDLE_2(282021, Material.BOOK, "book_upright_middle_2", 1),
    BOOK_UPRIGHT_MIDDLE_3(282022, Material.BOOK, "book_upright_middle_3", 1),
    BOOK_UPRIGHT_MIDDLE_4(282023, Material.BOOK, "book_upright_middle_4", 1),
    BOOK_UPRIGHT_MIDDLE_5(282024, Material.BOOK, "book_upright_middle_5", 1),
    BOOK_UPRIGHT_FRONT_1(282025, Material.BOOK, "book_upright_front_1", 1),
    BOOK_UPRIGHT_FRONT_2(282026, Material.BOOK, "book_upright_front_2", 1),
    BOOK_UPRIGHT_FRONT_3(282027, Material.BOOK, "book_upright_front_3", 1),
    BOOK_UPRIGHT_FRONT_4(282028, Material.BOOK, "book_upright_front_4", 1),
    BOOK_UPRIGHT_FRONT_5(282029, Material.BOOK, "book_upright_front_5", 1),
    CAMPFIRE_SURROGATE_ITEM(282030, Material.SHULKER_SHELL, "Nuotio", 1),
    GRILL(282031, Material.SHULKER_SHELL, "Grilliritilä", 1);

    // Constructor.
    private final int id;
    private final Material material;
    private final String itemName;
    private final int maxStackSize;

    CustomItemsEnum(int id, Material material, String itemName, int maxStackSize) {
        this.id = id;
        this.material = material;
        this.itemName = itemName;
        this.maxStackSize = maxStackSize;
    }

    // Getter.
    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public static CustomItemsEnum getMaterial(String itemName) {
        CustomItemsEnum customItem = null;
        for (CustomItemsEnum item : CustomItemsEnum.values()) {
            if (item.getItemName().equals(itemName)) {
                customItem = item;
                break;
            }
        }
        return customItem;
    }

    public String getItemName() {
        return itemName;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    // Instance method to get an ItemStack for this enum constant
    public ItemStack getItem(int stackSize) {
        Material itemMaterial = this.getMaterial();
        int itemId = this.getId();
        ItemStack item = new ItemStack(itemMaterial, stackSize);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        itemMeta.setItemName(this.getItemName());
        itemMeta.setCustomModelData(itemId);
        itemMeta.setMaxStackSize(this.getMaxStackSize());
        item.setItemMeta(itemMeta);

        return item;
    }

}

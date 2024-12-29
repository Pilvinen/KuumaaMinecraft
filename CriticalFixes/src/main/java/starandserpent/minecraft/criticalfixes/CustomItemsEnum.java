package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItemsEnum {

    // Sleeping pod with int associated with it.
    SLEEPING_POD_OPEN_FEET(282000, Material.SHULKER_SHELL, "sleepinging_pod_open_feet", 1, "Horroskapseli"),
    SLEEPING_POD_OPEN_HEAD(282001, Material.SHULKER_SHELL, "sleepinging_pod_open_head", 1, "Horroskapseli"),
    SLEEPING_POD_CLOSED_FEET(282002, Material.SHULKER_SHELL, "sleepinging_pod_closed_feet", 1, "Horroskapseli"),
    SLEEPING_POD_CLOSED_HEAD(282003, Material.SHULKER_SHELL, "sleepinging_pod_closed_head", 1, "Horroskapseli"),
    EYELIDS_2X2(282004, Material.SHULKER_SHELL, "eyelids_2x2", 1, "Silmäluomet"),
    FULL_FACE_TEXTURE_SMILE(282005, Material.SHULKER_SHELL, "full_face_texture_smile", 1, "Hymyilevät kasvot"),
    FULL_FACE_TEXTURE_SAD(282006, Material.SHULKER_SHELL, "full_face_texture_sad", 1, "Surulliset kasvot"),
    FULL_FACE_TEXTURE_LAUGH(282007, Material.SHULKER_SHELL, "full_face_texture_laugh", 1, "Nauravat kasvot"),
    FULL_FACE_TEXTURE_SERIOUS(282008, Material.SHULKER_SHELL, "full_face_texture_serious", 1, "Vakavat kasvot"),
    FULL_FACE_TEXTURE_SHOCK(282009, Material.SHULKER_SHELL, "full_face_texture_shock", 1, "Järkyttyneet kasvot"),
    POCKETWATCH(282010, Material.SHULKER_SHELL, "pocketwatch", 1, "Taskukello"),
    BOOK_STACK_2(282011, Material.BOOK, "book_stack_2", 1, "Kirjapino"),
    BOOK_STACK_3(282012, Material.BOOK, "book_stack_3", 1, "Kirjapino"),
    BOOK_STACK_4(282013, Material.BOOK, "book_stack_4", 1, "Kirjapino"),
    BOOK_STACK_5(282014, Material.BOOK, "book_stack_5", 1, "Kirjapino"),
    BOOK_UPRIGHT_BACK_1(282015, Material.BOOK, "book_upright_back_1", 1, "Kirjoja"),
    BOOK_UPRIGHT_BACK_2(282016, Material.BOOK, "book_upright_back_2", 1, "Kirjoja"),
    BOOK_UPRIGHT_BACK_3(282017, Material.BOOK, "book_upright_back_3", 1, "Kirjoja"),
    BOOK_UPRIGHT_BACK_4(282018, Material.BOOK, "book_upright_back_4", 1, "Kirjoja"),
    BOOK_UPRIGHT_BACK_5(282019, Material.BOOK, "book_upright_back_5", 1, "Kirjoja"),
    BOOK_UPRIGHT_MIDDLE_1(282020, Material.BOOK, "book_upright_middle_1", 1, "Kirjoja"),
    BOOK_UPRIGHT_MIDDLE_2(282021, Material.BOOK, "book_upright_middle_2", 1, "Kirjoja"),
    BOOK_UPRIGHT_MIDDLE_3(282022, Material.BOOK, "book_upright_middle_3", 1, "Kirjoja"),
    BOOK_UPRIGHT_MIDDLE_4(282023, Material.BOOK, "book_upright_middle_4", 1, "Kirjoja"),
    BOOK_UPRIGHT_MIDDLE_5(282024, Material.BOOK, "book_upright_middle_5", 1, "Kirjoja"),
    BOOK_UPRIGHT_FRONT_1(282025, Material.BOOK, "book_upright_front_1", 1, "Kirjoja"),
    BOOK_UPRIGHT_FRONT_2(282026, Material.BOOK, "book_upright_front_2", 1, "Kirjoja"),
    BOOK_UPRIGHT_FRONT_3(282027, Material.BOOK, "book_upright_front_3", 1, "Kirjoja"),
    BOOK_UPRIGHT_FRONT_4(282028, Material.BOOK, "book_upright_front_4", 1, "Kirjoja"),
    BOOK_UPRIGHT_FRONT_5(282029, Material.BOOK, "book_upright_front_5", 1, "Kirjoja"),
    CAMPFIRE_SURROGATE_ITEM(282030, Material.SHULKER_SHELL, "nuotio", 1, "Nuotio"),
    GRILL(282031, Material.SHULKER_SHELL, "grilliritilä", 1, "Grilliritilä"),
    GOLD_COIN_PILE(282032, Material.SHULKER_SHELL, "kasa_kultakolikoita", 64, "Kasa kultakolikoita"),
    GOLD_COIN_STACK(282033, Material.SHULKER_SHELL, "pino_kultakolikoita", 64, "Pino kultakolikoita"),
    TENT(282034, Material.SHULKER_SHELL, "teltta", 1, "Teltta"),
    HORNED_HEADRESS(282035, Material.SHULKER_SHELL, "rajattoman_vallan_sarvikruunu", 1, "Rajattoman vallan sarvikruunu"),
    HAT_FARMER(282036, Material.SHULKER_SHELL, "heinähattu", 16, "Heinähattu"),
    HAT_SHEPHERD(282037, Material.SHULKER_SHELL, "paimentolaishattu", 16, "Paimentolaishattu"),
    HAT_FISHERMAN(282038, Material.SHULKER_SHELL, "huopahattu", 16, "Huopahattu"),
    HAT_FLETCHER(282039, Material.SHULKER_SHELL, "metsästyshattu", 16, "Metsästyshattu"),
    RED_HAT(282040, Material.SHULKER_SHELL, "hiippalakki", 16, "Hiippalakki"),
    BLACK_HOOD(282041, Material.SHULKER_SHELL, "musta_huppu", 16, "Musta huppu"),
    // Curtains, left side.
    ONE_CURTAIN_LEFT_FULLY_CLOSED(282042, Material.SHULKER_SHELL, "1_verho_vasen_täysin_kiinni", 64, "Vasemmanpuoleinen verho"),
    TWO_CURTAIN_LEFT_ALMOST_CLOSED(282043, Material.SHULKER_SHELL, "2_verho_vasen_melkein_kiinni", 64, "Vasemmanpuoleinen verho"),
    THREE_CURTAIN_LEFT_HALFWAY(282044, Material.SHULKER_SHELL, "3_verho_vasen_puoliväli", 64, "Vasemmanpuoleinen verho"),
    FOUR_CURTAIN_LEFT_ALMOST_OPEN(282045, Material.SHULKER_SHELL, "4_verho_vasen_melkein_auki", 64, "Vasemmanpuoleinen verho"),
    FIVE_CURTAIN_LEFT_FULLY_OPEN(282046, Material.SHULKER_SHELL, "5_verho_vasen_täysin_auki", 64, "Vasemmanpuoleinen verho"),
    SIX_CURTAIN_LEFT_ALMOST_OPEN(282047, Material.SHULKER_SHELL, "6_verho_vasen_melkein_auki", 64, "Vasemmanpuoleinen verho"),
    SEVEN_CURTAIN_LEFT_HALFWAY(282048, Material.SHULKER_SHELL, "7_verho_vasen_puoliväli", 64, "Vasemmanpuoleinen verho"),
    EIGHT_CURTAIN_LEFT_ALMOST_CLOSED(282049, Material.SHULKER_SHELL, "8_verho_vasen_melkein_kiinni", 64, "Vasemmanpuoleinen verho"),
    // Curtains, right side.
    ONE_CURTAIN_RIGHT_FULLY_CLOSED(282050, Material.SHULKER_SHELL, "1_verho_oikea_täysin_kiinni", 64, "Oikeanpuoleinen verho"),
    TWO_CURTAIN_RIGHT_ALMOST_CLOSED(282051, Material.SHULKER_SHELL, "2_verho_oikea_melkein_kiinni", 64, "Oikeanpuoleinen verho"),
    THREE_CURTAIN_RIGHT_HALFWAY(282052, Material.SHULKER_SHELL, "3_verho_oikea_puoliväli", 64, "Oikeanpuoleinen verho"),
    FOUR_CURTAIN_RIGHT_ALMOST_OPEN(282053, Material.SHULKER_SHELL, "4_verho_oikea_melkein_auki", 64, "Oikeanpuoleinen verho"),
    FIVE_CURTAIN_RIGHT_FULLY_OPEN(282054, Material.SHULKER_SHELL, "5_verho_oikea_täysin_auki", 64, "Oikeanpuoleinen verho"),
    SIX_CURTAIN_RIGHT_ALMOST_OPEN(282055, Material.SHULKER_SHELL, "6_verho_oikea_melkein_auki", 64, "Oikeanpuoleinen verho"),
    SEVEN_CURTAIN_RIGHT_HALFWAY(282056, Material.SHULKER_SHELL, "7_verho_oikea_puoliväli", 64, "Oikeanpuoleinen verho"),
    EIGHT_CURTAIN_RIGHT_ALMOST_CLOSED(282057, Material.SHULKER_SHELL, "8_verho_oikea_melkein_kiinni", 64, "Oikeanpuoleinen verho"),
    // More hats
    HAT_BARD(282058, Material.SHULKER_SHELL, "pelimannin_hattu", 16, "Pelimannin hattu");

    // Constructor.
    private final int id;
    private final Material material;
    private final String giveItemName;
    private final String displayName;
    private final int maxStackSize;

    CustomItemsEnum(int id, Material material, String giveItemName, int maxStackSize, String displayName) {
        this.id = id;
        this.material = material;
        this.giveItemName = giveItemName;
        this.maxStackSize = maxStackSize;
        this.displayName = displayName;
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
            if (item.getGiveItemName().equals(itemName)) {
                customItem = item;
                break;
            }
        }
        return customItem;
    }

    public String getGiveItemName() {
        return giveItemName;
    }
    public String getDisplayName() { return displayName; }

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
        itemMeta.setItemName(this.getDisplayName());
        itemMeta.setCustomModelData(itemId);
        itemMeta.setMaxStackSize(this.getMaxStackSize());
        item.setItemMeta(itemMeta);

        return item;
    }

}

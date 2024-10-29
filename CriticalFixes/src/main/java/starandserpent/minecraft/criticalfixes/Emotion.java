package starandserpent.minecraft.criticalfixes;

public enum Emotion {
    // Emotions.
    HAPPY(CustomItemsEnum.FULL_FACE_TEXTURE_SMILE),
    SAD(CustomItemsEnum.FULL_FACE_TEXTURE_SAD),
    SHOCKED(CustomItemsEnum.FULL_FACE_TEXTURE_SHOCK),
    LAUGHING(CustomItemsEnum.FULL_FACE_TEXTURE_LAUGH),
    SERIOUS(CustomItemsEnum.FULL_FACE_TEXTURE_SERIOUS);

    // Constructor.
    private final CustomItemsEnum customItem;
    Emotion(CustomItemsEnum customItem) {
        this.customItem = customItem;
    }

    // Getter.
    public CustomItemsEnum getCustomItem() {
        return customItem;
    }

    public int getId() {
        return customItem.getId();
    }

    public String getMaterial() {
        return customItem.getMaterial().name();
    }
}

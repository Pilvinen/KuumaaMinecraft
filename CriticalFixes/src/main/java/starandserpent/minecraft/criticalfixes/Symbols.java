package starandserpent.minecraft.criticalfixes;

// Enum of the character names.
public enum Symbols {

    // Spaces, negative.
    NEG1("\uF111"),
    NEG2("\uF112"),
    NEG4("\uF113"),
    NEG8("\uF114"),
    NEG16("\uF115"),
    NEG32("\uF116"),
    NEG64("\uF117"),
    NEG128("\uF118"),
    NEG256("\uF119"),
    NEG512("\uF11A"),
    NEG1024("\uF11B"),

    // Spaces, positive.
    POS1("\uF121"),
    POS2("\uF122"),
    POS4("\uF123"),
    POS8("\uF124"),
    POS16("\uF125"),
    POS32("\uF126"),
    POS64("\uF127"),
    POS128("\uF128"),
    POS256("\uF129"),
    POS512("\uF12A"),
    POS1024("\uF12B"),

    DOWN("\u4E0A"),

    // Text font symbols.
    MOON("☽"),
    CLOUD("☁"),
    SUN("☀"),
    SKULL("☠"),
    RAINCLOUD("🌧"),
    THUNDERCLOUD("⛈"),
    FIRE("🔥"),
    WATER("🌊"),
    BOXESEMPTY("☐"),
    BOXESCORRECT("☑"),
    BOXESINCORRECT("☒"),
    WARNINGTRIANGLE("⚠"),
    CHECKMARK("✔"),
    CIRCLE("⭘"),
    COMET("☄"),
    HEART("❤"),
    STAR("⭐"),
    SMILE("☻"),
    BELL("🔔"),
    FLAG("⚑"),
    ARROWLEFT("←"),
    ARROWRIGHT("→"),
    ARROWUP("↑"),
    ARROWDOWN("↓"),
    ARROWLEFTRIGHT("⇄"),
    ARROWUPDOWN("⇵"),
    DICE1("⚀"),
    DICE2("⚁"),
    DICE3("⚂"),
    DICE4("⚃"),
    DICE5("⚄"),
    DICE6("⚅"),

    // Graphical symbols.
    ARROW_UP("\u4E00"),
    ARROW_DOWN("\u4E01"),
    YELLOW_WARNING_TRIANGLE("\u4E02"),
    RED_WARNING_TRIANGLE("\u4E03"),
    SMALL_MOON("\u4E04"),
    MEDIUM_MOON("\u4E05"),
    KUUMAA_LOGO2("\u4E06"),
    SYSTEM_CONTAINER_TOP("\u4E07"),
    SYSTEM_CONTAINER_BOTTOM("\u4E08"),
    SYSTEM_CONTAINER_MIDDLE("\u4E09"),
    KUUMAA_LOGO1("\u4E0A"),
    KUUMAA_LOGO3("\u4E0B");

    public final String literal;

    Symbols(String literal){
        this.literal = literal;
    }

    @Override public String toString(){
        return this.literal;
    }

    private enum SpacingCharacters {

        // Negative spaces.
        NEG1(-1, Symbols.NEG1),
        NEG2(-2, Symbols.NEG2),
        NEG3(-4, Symbols.NEG4),
        NEG4(-8, Symbols.NEG8),
        NEG5(-16, Symbols.NEG16),
        NEG6(-32, Symbols.NEG32),
        NEG7(-64, Symbols.NEG64),
        NEG8(-128, Symbols.NEG128),
        NEG9(-256, Symbols.NEG256),
        NEG10(-512, Symbols.NEG512),
        NEG11(-1024, Symbols.NEG1024),

        // Positive spaces.
        POS1(1, Symbols.POS1),
        POS2(2, Symbols.POS2),
        POS4(4, Symbols.POS4),
        POS8(8, Symbols.POS8),
        POS16(16, Symbols.POS16),
        POS32(32, Symbols.POS32),
        POS64(64, Symbols.POS64),
        POS128(128, Symbols.POS128),
        POS256(256, Symbols.POS256),
        POS512(512, Symbols.POS512),
        POS1024(1024, Symbols.POS1024);

        // Weight defines the amount of spaces between the characters.
        private final int weight;

        // Reference to the symbols.
        private final Symbols symbolRef;

        // Constructor.
        SpacingCharacters(int weight, Symbols symbolRef){
            this.weight = weight;
            this.symbolRef = symbolRef;
        }

    }

    // What this does is that it gets the character by weight, ie.
    // if the weight is 1, it returns the character that has a weight of 1.
    // If the weight is not found in the enum, it returns null.
    public static Symbols getCharacterByWeight(int weight){
        for(SpacingCharacters ch : SpacingCharacters.values()){
            if(ch.weight == weight)
                return ch.symbolRef;
        }
        return null;
    }

    // Get space characters by pixel amount.
    // What this does is that it converts the pixel amount to binary string of Minecraft characters.
    // You get the amount of spaces you request or the closest amount of spaces to the requested amount.
    private static String getSpacing(int pixelAmount){
        //convert amount to binary string
        String binary = new StringBuilder(Integer.toBinaryString(Math.abs(pixelAmount))).reverse().toString();
        StringBuilder sb = new StringBuilder();
        char[] chArr = binary.toCharArray();
        for(int index = 0; index < chArr.length; index++){
            char ch = chArr[index];
            if(ch == '0') continue;

            int weight = (int)Math.pow(2, index);
            //if we are getting negative, flip weight
            weight = pixelAmount < 0 ? -weight : weight;
            Symbols ref = getCharacterByWeight(weight);

            if(ref != null)
                sb.append(ref.literal);
        }
        return sb.toString();
    }

    // Get negative spaces.
    public static String getNeg(int pixelAmount){
        return getSpacing(-Math.abs(pixelAmount));
    }

    // Get positive spaces.
    public static String getPos(int pixelAmount){
        return getSpacing(Math.abs(pixelAmount));
    }

}

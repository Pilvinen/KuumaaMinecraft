package starandserpent.minecraft.criticalfixes;

public class ClientInformationPacket {
    public String Locale;
    public byte ViewDistance;
    public int ChatMode; // Assuming VarInt Enum maps to int
    public boolean ChatColors;
    public byte DisplayedSkinParts;
    public int MainHand; // Assuming VarInt Enum maps to int
    public boolean EnableTextFiltering;
    public boolean AllowServerListings;
}

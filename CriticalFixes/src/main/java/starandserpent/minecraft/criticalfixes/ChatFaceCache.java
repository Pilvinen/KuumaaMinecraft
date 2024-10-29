package starandserpent.minecraft.criticalfixes;

public class ChatFaceCache {
    public String hatFace;
    public String noHatFace;

    public ChatFaceCache(String hatFace, String noHatFace) {
        this.hatFace = hatFace;
        this.noHatFace = noHatFace;
    }

    public String getHatFace(boolean hasHat) {
        if (hasHat) {
            return hatFace;
        } else {
            return noHatFace;
        }
    }

}

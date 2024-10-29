package starandserpent.minecraft.criticalfixes;

public class SoundData {
    private String sound;
    private double durationDay;
    private double durationNight;

    public SoundData(String sound, double durationDay, double durationNight) {
        this.sound = sound;
        this.durationDay = durationDay;
        this.durationNight = durationNight;
    }

    public String getSound() {
        return sound;
    }

    public double getDurationDayInMilliseconds() {
        return durationDay * 1000;
    }

    public double getDurationNightInMilliseconds() {
        return durationNight * 1000;
    }

}

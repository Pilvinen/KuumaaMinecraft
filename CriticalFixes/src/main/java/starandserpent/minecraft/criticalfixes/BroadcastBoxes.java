package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.chat.TextComponent;

public class BroadcastBoxes {
    private TextComponent topBox;
    private TextComponent[] middleBox;
    private TextComponent bottomBox;

    public BroadcastBoxes(TextComponent topBox, TextComponent[] middleBox, TextComponent bottomBox) {
        this.topBox = topBox;
        this.middleBox = middleBox;
        this.bottomBox = bottomBox;
    }

    public TextComponent getTopBox() {
        return topBox;
    }

    public TextComponent[] getMiddleBox() {
        return middleBox;
    }

    public TextComponent getBottomBox() {
        return bottomBox;
    }

}

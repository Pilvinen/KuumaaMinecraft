package starandserpent.minecraft.criticalfixes;

import github.scarsz.discordsrv.DiscordSRV;

public class SendToDiscord {

    private static DiscordSRV discordSRV;

    public SendToDiscord(DiscordSRV discord) {
        discordSRV = discord;
    }

    public static void setDiscordSRV(DiscordSRV discord) {
        discordSRV = discord;
    }



}

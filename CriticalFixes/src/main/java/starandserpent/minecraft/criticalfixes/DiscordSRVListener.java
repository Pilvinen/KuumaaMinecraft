package starandserpent.minecraft.criticalfixes;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.*;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.Plugin;

public class DiscordSRVListener {

    private final Plugin plugin;

    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        // Ensure JDA is initialized before using it
        if (DiscordUtil.getJda() != null) {
            DiscordUtil.getJda().addEventListener(new JDAListener(plugin));
            plugin.getLogger().info("Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
        } else {
            plugin.getLogger().warning("JDA is not initialized yet.");
        }
    }

//    @Subscribe(priority = ListenerPriority.MONITOR)
//    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
//        plugin.getLogger().info("Received a chat message on Discord: " + event.getMessage());
//    }

//    @Subscribe(priority = ListenerPriority.MONITOR)
//    public void aMessageWasSentInADiscordGuildByTheBot(DiscordGuildMessageSentEvent event) {
//        plugin.getLogger().info("A message was sent to Discord: " + event.getMessage());
//    }

//    @Subscribe
//    public void accountsLinked(AccountLinkedEvent event) {
//        Bukkit.broadcastMessage(event.getPlayer().getName() + " just linked their MC account to their Discord user " + event.getUser() + "!");
//    }

//    @Subscribe
//    public void accountUnlinked(AccountUnlinkedEvent event) {
//        User user = DiscordUtil.getJda().getUserById(event.getDiscordId());
//        if (user != null) {
//            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your account has been unlinked").queue());
//        }
//        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("unlinks");
//        if (textChannel != null) {
//            textChannel.sendMessage(event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ") has unlinked their associated Discord account: "
//                    + (event.getDiscordUser() != null ? event.getDiscordUser().getName() : "<not available>") + " (" + event.getDiscordId() + ")").queue();
//        } else {
//            plugin.getLogger().warning("Channel called \"unlinks\" could not be found in the DiscordSRV configuration");
//        }
//    }

//    @Subscribe
//    public void discordMessageProcessed(DiscordGuildMessagePostProcessEvent event) {
//        event.setProcessedMessage(event.getProcessedMessage().replace("cat", "dog"));
//    }
}
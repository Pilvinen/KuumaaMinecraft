package starandserpent.minecraft.criticalfixes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import starandserpent.minecraft.criticalfixes.Locking.LockingMechanism;

import java.io.IOException;
import java.util.List;
import static org.bukkit.Bukkit.getPluginManager;

public final class CriticalFixes extends JavaPlugin {

    PluginManager pluginManager = getPluginManager();
    private ProtocolManager protocolManager;

    private KuuChat kuuChat;
    private CitizensListener citizensListener;

    @Override public void onDisable() {
        System.out.println("Unloading CriticalFixes.");
        kuuChat.onDisable();
    }


    @Override public void onEnable() {
        // Proceed with the rest of the initialization
        initializePlugin();
    }

    private void initializePlugin() {

        showVersionNumber();

        protocolManager = ProtocolLibrary.getProtocolManager();

        registerDayLengthFix();
        registerDrowningFix();
        registerFallingBlocksFix();
        registerRespawnFix();
        registerRenkutusFix();
        registerWarp();
        registerCopperTray();
        registerAltars();
//        registerTabKeyListener();
        registerHideNames();

        try {
            registerKuuChat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        registerPlayerPoseListener();
        registerHook();
        registerCustomSounds();
        registerGamemodeFixes();
        registerCauldronImprovements();
        registerChainExtensions();
        registerSoundFixesForReplacedBlocks();
        registerPathImprovement();
        registerInspectThings();
        registerHats();
        registerBirchLeafFix();
        registerWeatherCommands();
        registerHatDetector();
        registerBucketFix();
        registerFieldFix();
        registerDangerousMobs();
        registerTakeAllPlaceAllInventory();
        registerBanImprovements();
        registerWelcome();
        registerSystemNotifications();
        registerDeathInventory();
        registerSleepPod();
        registerEmoticons();
        registerPlayersData();
        registerDoubleDoors();
        registerRegenerationFix();
        registerHungerFix();
        registerDaySleepingFix();
        registerBetterTrapdoors();
        registerServerStatistics();
        registerCustomItems();
        registerImprovedClock();
        registerItemFrameImprovements();
        registerEffectsLibrary();
        registerSnowballImprovements();
        registerTagGame();
        registerTrampledSnow();
        registerBlockBreakSpeedFix();
        registerDroppedItems();
        registerImprovedCampfires();
        registerSeasonsManagementSystem();
        registerPlayerDamageHandler();
        registerBanMobs();
        registerCopperFixes();
        registerReturnBell();
        registerBarbedWire();
        registerDamageKnockbackFix();
        registerBetterTNT();
        registerMobCheck();
        registerPlayerTracker();
        registerPlayerIdleTracker();
        registerBetterRestartServer();
        registerDamageEffects();
        registerCustomCoins();
        registerWhoWasHereWhileYouWereGone();
        registerCommands();
        registerIntroduction();
        registerCitizensIntegration();
        registerPermissionManager();
        registerCurtains();
        registerResourcePackLoader();
        registerLockingMechanism();
        registerOreFixes();
    }

    private void showVersionNumber() {
        System.out.println("CriticalFixes: Version 1.0.0.");
    }

    private void registerRespawnFix() {
        System.out.println("CriticalFixes: Loading RespawnFix.");
        var respawnFix = new RespawnFix(this);
        pluginManager.registerEvents(respawnFix, this);
    }

    private void registerDayLengthFix() {
        System.out.println("CriticalFixes: Loading DayLengthFix.");
        var dayLengthFix = new DayLengthFix(this);
        pluginManager.registerEvents(dayLengthFix, this);
    }

    private void registerDrowningFix() {
        System.out.println("CriticalFixes: Loading DrowningFix.");
        var drowningFix = new DrowningFix(this);
        pluginManager.registerEvents(drowningFix, this);
    }

    private void registerFallingBlocksFix() {
        System.out.println("CriticalFixes: Loading FallingBlocksFix.");
        var fallingBlocksFix = new FallingBlocksFix(this);
        pluginManager.registerEvents(fallingBlocksFix, this);
    }

    private void registerRenkutusFix() {
        System.out.println("CriticalFixes: Loading RenkutusFix.");
        var renkutusFix = new RenkutusFix(this);
        pluginManager.registerEvents(renkutusFix, this);
    }

    private void registerWarp() {
        System.out.println("CriticalFixes: Loading Warp.");
        var warp = new Warp(this);

        getCommand("setwarp").setExecutor(warp);
        getCommand("warp").setExecutor(warp);
        getCommand("warplist").setExecutor(warp);
        getCommand("deletewarp").setExecutor(warp);

        warp.loadWarps();
    }

    private void registerCopperTray() {
        System.out.println("CriticalFixes: Loading CopperTray.");
        var copperTray = new CopperTray(this);
        pluginManager.registerEvents(copperTray, this);
    }

    private void registerAltars() {
        System.out.println("CriticalFixes: Loading Altars.");
        var altars = new Altars(this);
        pluginManager.registerEvents(altars, this);
    }


//    private void registerTabKeyListener() {
//        System.out.println("CriticalFixes: Loading TabKeyListener.");
//        protocolManager.addPacketListener(new TabKeyListener(this, protocolManager));
//    }
    private void registerHideNames() {
        System.out.println("CriticalFixes: Loading HideNames.");
        var hideNames = new HideNames(this);
        pluginManager.registerEvents(hideNames, this);
    }

    private void registerKuuChat() throws IOException {
        System.out.println("CriticalFixes: Loading KuuChat.");
        kuuChat = new KuuChat(this, DiscordSRV.getPlugin());
        DiscordSRV.api.subscribe(kuuChat);
        pluginManager.registerEvents(kuuChat, this);
    }

    private void registerPlayerPoseListener() {
        System.out.println("CriticalFixes: Loading PlayerPoseListener.");
        var playerPoseListener = new PlayerPoseListener(this, protocolManager);
        protocolManager.addPacketListener(playerPoseListener);
    }

    private void registerHook() {
        System.out.println("CriticalFixes: Loading Hook.");
        var hook = new Hook(this);
        pluginManager.registerEvents(hook, this);
    }

    private void registerCustomSounds() {
        System.out.println("CriticalFixes: Loading CustomSounds.");
        var customSounds = new AmbientBiomeSounds(this);
        pluginManager.registerEvents(customSounds, this);
    }

    private void registerGamemodeFixes() {
        System.out.println("CriticalFixes: Loading GamemodeFixes.");
        var gamemodeFixes = new GamemodeFixes(this);
        getCommand("creative").setExecutor(gamemodeFixes);
        getCommand("survival").setExecutor(gamemodeFixes);
        getCommand("adventure").setExecutor(gamemodeFixes);
        getCommand("spectator").setExecutor(gamemodeFixes);
    }

    private void registerCauldronImprovements() {
        System.out.println("CriticalFixes: Loading CauldronImprovements.");
        var cauldronImprovements = new CauldronImprovements(this);
        pluginManager.registerEvents(cauldronImprovements, this);
    }

    private void registerChainExtensions() {
        System.out.println("CriticalFixes: Loading ChainExtensions.");
        var chainExtensions = new ChainExtensions(this);
        pluginManager.registerEvents(chainExtensions, this);
    }

    private void registerSoundFixesForReplacedBlocks() {
        System.out.println("CriticalFixes: Loading SoundFixesForReplacedBlocks.");
        var soundFixesForReplacedBlocks = new SoundFixesForReplacedBlocks(this);
        pluginManager.registerEvents(soundFixesForReplacedBlocks, this);
    }

    private void registerPathImprovement() {
        System.out.println("CriticalFixes: Loading PathImprovement.");
        var pathImprovement = new PathImprovement(this);
        pluginManager.registerEvents(pathImprovement, this);
    }

    private void registerInspectThings() {
        System.out.println("CriticalFixes: Loading InspectThings.");
        var inspectThings = new InspectThings(this);
        pluginManager.registerEvents(inspectThings, this);
    }

    private void registerHats() {
        System.out.println("CriticalFixes: Loading Hats.");
        var hats = new Hats(this);
        pluginManager.registerEvents(hats, this);
    }

    private void registerBirchLeafFix() {
        System.out.println("CriticalFixes: Loading BirchLeafFix.");
        var birchLeafFix = new BirchLeafFix(this);
        pluginManager.registerEvents(birchLeafFix, this);
    }

    private void registerWeatherCommands() {
        System.out.println("CriticalFixes: Loading WeatherCommands.");
        var weatherCommands = new WeatherCommands(this);
        getCommand("sun").setExecutor(weatherCommands);
        getCommand("rain").setExecutor(weatherCommands);
        getCommand("thunder").setExecutor(weatherCommands);
    }

    private void registerHatDetector() {

        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.NORMAL,
                List.of(PacketType.Play.Client.SETTINGS,
                        PacketType.Configuration.Client.CLIENT_INFORMATION)
        ) {
            @Override public void onPacketReceiving(PacketEvent event) {

                var packet = event.getPacket();
                var packetType = event.getPacketType();

                if (packetType == PacketType.Play.Client.SETTINGS
                || packetType == PacketType.Configuration.Client.CLIENT_INFORMATION) {

                    var structureModifier = packet.getStructures();
                    var internalStructure = structureModifier.read(0);
                    var skin = internalStructure.getIntegers().read(1);
                    boolean isHatEnabled = (skin & 0x40) != 0;

                    // Get player
                    var player = event.getPlayer();

                    KuuChat.setHatState(player, isHatEnabled);

                    // Disable the shakes.
                    EffectsLibrary.turnOffShake(player);
                }
            }

            @Override public void onPacketSending(PacketEvent event) {}

        });
    }

    private void registerBucketFix() {
        System.out.println("CriticalFixes: Loading BucketFix.");
        var bucketFix = new BucketFix(this);
        pluginManager.registerEvents(bucketFix, this);
    }

    private void registerFieldFix() {
        System.out.println("CriticalFixes: Loading FieldFix.");
        var fieldFix = new FieldFix(this);
        pluginManager.registerEvents(fieldFix, this);
    }

    private void registerDangerousMobs() {
        System.out.println("CriticalFixes: Loading DangerousMobs.");
        var dangerousMobs = new DangerousMobs(this);
        pluginManager.registerEvents(dangerousMobs, this);
    }

    private void registerTakeAllPlaceAllInventory() {
        System.out.println("CriticalFixes: Loading TakeAllPlaceAllInventory.");
        var takeAllPlaceAllInventory = new TakeAllPlaceAllInventory(this);
        pluginManager.registerEvents(takeAllPlaceAllInventory, this);
    }

    private void registerBanImprovements() {
        System.out.println("CriticalFixes: Loading BanImprovements.");
        var banImprovements = new BanImprovements(this);
        getCommand("porttikielto").setExecutor(banImprovements);
        getCommand("armahda").setExecutor(banImprovements);
        pluginManager.registerEvents(banImprovements, this);
    }

    private void registerWelcome() {
        System.out.println("CriticalFixes: Loading Welcome.");
        var welcome = new Welcome(this);
        pluginManager.registerEvents(welcome, this);
    }

    private void registerSystemNotifications() {
        System.out.println("CriticalFixes: Loading SystemNotifications.");
        var systemNotifications = new SystemNotifications(this);
        getCommand("broadcast").setExecutor(systemNotifications);
        getCommand("ilmoitus").setExecutor(systemNotifications);
        getCommand("ilmoita").setExecutor(systemNotifications);
        pluginManager.registerEvents(systemNotifications, this);
    }

    private void registerDeathInventory() {
        System.out.println("CriticalFixes: Loading DeathInventory.");
        var deathInventory = new DeathInventory(this);
        pluginManager.registerEvents(deathInventory, this);
    }

    private void registerSleepPod() {
        System.out.println("CriticalFixes: Loading SleepPod.");
        var sleepPod = new SleepPod(this);
        pluginManager.registerEvents(sleepPod, this);
    }

    private void registerEmoticons() {
        System.out.println("CriticalFixes: Loading Emoticons.");
        var emoticons = new Emoticons(this, protocolManager);
        pluginManager.registerEvents(emoticons, this);
    }

    private void registerPlayersData() {
        System.out.println("CriticalFixes: Loading PlayersData.");
        var playersData = new PlayersData(this);
        pluginManager.registerEvents(playersData, this);
    }

    private void registerDoubleDoors() {
        System.out.println("CriticalFixes: Loading DoubleDoors.");
        var doubleDoors = new DoubleDoors(this);
        pluginManager.registerEvents(doubleDoors, this);
    }


    private void registerRegenerationFix() {
        System.out.println("CriticalFixes: Loading RegenerationFix.");
        var regenerationFix = new RegenerationFix(this);
        pluginManager.registerEvents(regenerationFix, this);
    }

    private void registerHungerFix() {
        System.out.println("CriticalFixes: Loading HungerFix.");
        var hungerFix = new HungerFix(this);
        pluginManager.registerEvents(hungerFix, this);
    }

    private void registerDaySleepingFix() {
        System.out.println("CriticalFixes: Loading DaySleepingFix.");
        var daySleepingFix = new DaySleepingFix(this);
        pluginManager.registerEvents(daySleepingFix, this);
    }

    private void registerBetterTrapdoors() {
        System.out.println("CriticalFixes: Loading BetterTrapdoors.");
        var betterTrapdoors = new BetterTrapdoors(this);
        pluginManager.registerEvents(betterTrapdoors, this);
    }

    private void registerServerStatistics() {
        System.out.println("CriticalFixes: Loading ServerStatistics.");
        var serverStatistics = new ServerStatistics(this);
        pluginManager.registerEvents(serverStatistics, this);

        getCommand("tilastoja").setExecutor(serverStatistics);
        getCommand("statistics").setExecutor(serverStatistics);
    }

    private void registerCustomItems() {
        System.out.println("CriticalFixes: Loading CustomItems.");
        var customItems = new CustomItems(this);

        getCommand("anna").setExecutor(customItems);
    }

    private void registerImprovedClock() {
        System.out.println("CriticalFixes: Loading ImprovedClock.");
        var improvedClock = new ImprovedClock(this);
        pluginManager.registerEvents(improvedClock, this);
    }

    private void registerItemFrameImprovements() {
        System.out.println("CriticalFixes: Loading ItemFrameImprovements.");
        var itemFrameImprovements = new ItemFrameImprovements(this);
        pluginManager.registerEvents(itemFrameImprovements, this);
    }

    private void registerEffectsLibrary() {
        System.out.println("CriticalFixes: Loading EffectsLibrary.");
        var effectsLibrary = new EffectsLibrary(this);
        pluginManager.registerEvents(effectsLibrary, this);
    }

    private void registerSnowballImprovements() {
        System.out.println("CriticalFixes: Loading SnowballImprovements.");
        var snowballImprovements = new SnowballImprovements(this);
        pluginManager.registerEvents(snowballImprovements, this);
    }

    private void registerTagGame() {
        System.out.println("CriticalFixes: Loading TagGame.");
        var tagGame = new TagGame(this);
        pluginManager.registerEvents(tagGame, this);
    }

    private void registerTrampledSnow() {
        System.out.println("CriticalFixes: Loading TrampledSnow.");
        var trampledSnow = new TrampledSnow(this);
        pluginManager.registerEvents(trampledSnow, this);
    }

    private void registerBlockBreakSpeedFix() {
        System.out.println("CriticalFixes: Loading BlockBreakSpeedFix.");
        var blockBreakSpeedFix = new BlockBreakSpeedFix(this);
        pluginManager.registerEvents(blockBreakSpeedFix, this);
    }

    private void registerDroppedItems() {
        System.out.println("CriticalFixes: Loading DroppedItems.");
        var droppedItems = new DroppedItems(this);
        pluginManager.registerEvents(droppedItems, this);
    }

    private void registerImprovedCampfires() {
        System.out.println("CriticalFixes: Loading ImprovedCampfires.");
        var improvedCampfires = new ImprovedCampfires(this);
        pluginManager.registerEvents(improvedCampfires, this);
    }

    private void registerSeasonsManagementSystem() {
        System.out.println("CriticalFixes: Loading SeasonsManagementSystem.");
        var seasonsManagementSystem = new SeasonsManagementSystem(this);
        pluginManager.registerEvents(seasonsManagementSystem, this);
    }

    private void registerPlayerDamageHandler() {
        System.out.println("CriticalFixes: Loading PlayerDamageHandler.");
        var playerDamageHandler = new PlayerDamageHandler(this);
        pluginManager.registerEvents(playerDamageHandler, this);
    }

    private void registerBanMobs() {
        System.out.println("CriticalFixes: Loading BanMobs.");
        var bannedMobs = new BannedMobs(this);
        pluginManager.registerEvents(bannedMobs, this);
    }

    private void registerCopperFixes() {
        System.out.println("CriticalFixes: Loading CopperFixes.");
        var copperFixes = new CopperFixes(this);
        pluginManager.registerEvents(copperFixes, this);
    }

    private void registerReturnBell() {
        System.out.println("CriticalFixes: Loading ReturnBell.");
        var returnBell = new ReturnBell(this);
        pluginManager.registerEvents(returnBell, this);
    }

    private void registerBarbedWire() {
        System.out.println("CriticalFixes: Loading BarbedWire.");
        var barbedWire = new BarbedWire(this);
        pluginManager.registerEvents(barbedWire, this);
    }

    private void registerDamageKnockbackFix() {
        System.out.println("CriticalFixes: Loading DamageKnockbackFix.");
        var damageKnockbackFix = new DamageKnockbackFix(this);
        pluginManager.registerEvents(damageKnockbackFix, this);
    }

    private void registerBetterTNT() {
        System.out.println("CriticalFixes: Loading BetterTNT.");
        var betterTNT = new BetterTNT(this);
        pluginManager.registerEvents(betterTNT, this);
    }

    private void registerMobCheck() {
        System.out.println("CriticalFixes: Loading MobCheck.");
        var mobCheck = new MobCheck(this);
        getCommand("mobcheck").setExecutor(mobCheck);
    }

    private void registerPlayerTracker() {
        System.out.println("CriticalFixes: Loading PlayerTracker.");
        var playerTracker = new PlayerTracker(this);
        pluginManager.registerEvents(playerTracker, this);

        getCommand("pelaajat").setExecutor(playerTracker);
        getCommand("players").setExecutor(playerTracker);
        getCommand("top10").setExecutor(playerTracker);
        getCommand("pelaaja").setExecutor(playerTracker);
        getCommand("player").setExecutor(playerTracker);
        getCommand("seen").setExecutor(playerTracker);
        getCommand("missä").setExecutor(playerTracker);
    }

    private void registerPlayerIdleTracker() {
        System.out.println("CriticalFixes: Loading PlayerIdleTracker.");
        var playerIdleTracker = new PlayerIdleTracker(this);
        pluginManager.registerEvents(playerIdleTracker, this);
    }

    private void registerBetterRestartServer() {
        System.out.println("CriticalFixes: Loading BetterRestartServer.");
        var betterRestartServer = new BetterRestartServer(this);

        getCommand("ukk").setExecutor(betterRestartServer);
    }

    private void registerDamageEffects() {
        System.out.println("CriticalFixes: Loading DamageEffects.");
        var damageEffects = new DamageEffects(this);
        pluginManager.registerEvents(damageEffects, this);
    }

    private void registerCustomCoins() {
        System.out.println("CriticalFixes: Loading CustomCoins.");
        var customCoins = new CustomCoins(this);
        pluginManager.registerEvents(customCoins, this);
    }

    private void registerWhoWasHereWhileYouWereGone() {
        System.out.println("CriticalFixes: Loading WhoWasHereWhileYouWereGone.");
        var whoWasHereWhileYouWereGone = new WhoWasHereWhileYouWereGone(this);
        pluginManager.registerEvents(whoWasHereWhileYouWereGone, this);
    }

    private void registerCommands() {
        System.out.println("CriticalFixes: Loading Commands.");
        var commands = new Commands(this);
        getCommand("komennot").setExecutor(commands);
        getCommand("commands").setExecutor(commands);
        getCommand("help").setExecutor(commands);
        getCommand("apua").setExecutor(commands);
        getCommand("info").setExecutor(commands);
        getCommand("discord").setExecutor(commands);
        getCommand("johdanto").setExecutor(commands);
    }

    private void registerIntroduction() {
        System.out.println("CriticalFixes: Loading Introduction.");
        var introduction = new Introduction(this);
        pluginManager.registerEvents(introduction, this);
        getCommand("hyväksypelaaja").setExecutor(introduction);
    }

    private void registerCitizensIntegration() {
        System.out.println("CriticalFixes: Registering Citizens integration.");
        citizensListener = new CitizensListener();
        pluginManager.registerEvents(citizensListener, this);
    }

    private void registerPermissionManager() {
        System.out.println("CriticalFixes: Loading PermissionManager.");
        var permissionManager = new PermissionManager(this);
        pluginManager.registerEvents(permissionManager, this);
    }

    private void registerCurtains() {
        System.out.println("CriticalFixes: Loading Curtains.");
        var curtains = new Curtains(this);
        pluginManager.registerEvents(curtains, this);
    }

    private void registerResourcePackLoader() {
        System.out.println("CriticalFixes: Loading ResourcePackLoader.");
        var resourcePackLoader = new ResourcePackLoader(this);
        getCommand("resourcepack").setExecutor(resourcePackLoader);
        getCommand("respack").setExecutor(resourcePackLoader);
        getCommand("resurssipaketti").setExecutor(resourcePackLoader);
    }

    private void registerLockingMechanism() {
        System.out.println("CriticalFixes: Loading Locking.");
        var lockingMechanism = new LockingMechanism(this);
        pluginManager.registerEvents(lockingMechanism, this);
    }

    private void registerOreFixes() {
        System.out.println("CriticalFixes: Loading OreFixes.");
        var oreFixes = new OreFixes(this);
        pluginManager.registerEvents(oreFixes, this);
    }

}

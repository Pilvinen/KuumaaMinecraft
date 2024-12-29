package starandserpent.minecraft.criticalfixes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Introduction implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;
    private Map<String, Set<BukkitTask>> playerTasks = new ConcurrentHashMap<>();
    private Map<Integer, BukkitTask> taskIdMap = new ConcurrentHashMap<>();
    private AtomicInteger nextTaskId = new AtomicInteger();

    private static final String[] MESSAGES = {
            ChatColor.DARK_AQUA + "Avaruusalus Asterion halkoi kosmoksen mustaa tyhjyyttä hyperavaruuden hehkussa, kotiplaneetan läheisyys väreili kuin lupaus. Vuoden mittaisen palvelukierroksen jälkeen enää seitsemän parsekkia kotiin..." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Hyvää huomenta, kapteeni %s." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kaikki järjestelmät nominaaliset." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Saavumme kotimaailmaan aikataulussa." + ChatColor.RESET,
            ChatColor.YELLOW + "Komm: " + ChatColor.DARK_AQUA + "Odotamme edelleen yhteydenottoa." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Minä hetkenä hyvänsä..." + ChatColor.RESET,
            ChatColor.YELLOW + "Komm: " + ChatColor.DARK_AQUA + "Kommunikaatiojärjestelmä nominaalinen, kapteeni." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Asteroidiparvi ohittaa meidät pian styyrpuurin puolelta, kapteeni." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Etäisyys ei aiheuta meille vaaraa." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Ei tarvetta kurssikorjaukseen." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Ensimmäiset asteroidit ohittavat meidät." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Olemme nyt ohittaneet asteroidiparven." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kaikki järjestelmät nominaaliset." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Kapteeni, takavakaimessa vaikuttaisi olevan pieni sähköhäiriö." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Selvitän ongelmaa." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kapteeni, konehuone suosittelee vaihtamaan takavakaimen varajärjestelmään AUX-2, kunnes ongelma on saatu selvitettyä telakalla." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Pieni hetki." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Kapteeni, takavakaimen ohitus on valmis. AUX-2 varajärjestelmään siirtyminen odottaa hyväksyntäänne komentopaneelista." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kröhöm. Se on tuo paneeli, jossa on ISO punainen vilkkuva valo, kapteeni." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Milloin vain, kun olette valmiina, kapteeni." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Se on... tuo nappi tuossa." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kröhöm. Ei, ei se nappi. Vaan TUO nappi tuossa." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Ei, ei ei, ei SE nappi, hyvänen aika, älkää! TUO ISO PUNAINEN NAPPI TUOSSA." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Hyvä luoja sentään, mikä teitä vaivaa!" + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Anteeksi, olen pahoillani, kapteeni." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Milloin vain olette valmiina, se tuo nappi tuossa." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Siis, se nappi missä lukee alapuolella AUX-2 järjestelmään siirtyminen." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Ahaa... hyvin huvittavaa, kapteeni." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Sitten, kun olette valmiina, kapteeni %s." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kapteeni, hyvin huvittavaa, mutta odotamme edelleen teitä hyväksymään siirron perävakaimen AUX-2 järjestelmään." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kapteeni, teidän luvallanne, suoritan siirron itse omasta komentopaneelistani." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kiitos. Kapteeni." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Siirto varajärjestelmään valmis." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Konehuone pyytää lupaa suorittaa diagnostiikkatestin perävakaimen AUX-2 järjestelmälle." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Siinä kestää noin kaksi minuuttia." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Konehuone ilmoittaa: kaikki järjestelmät nominaaliset." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Meillä ei pitäisi olla enää ongelmia perävakaimen kanssa saapuessamme ilmakehään." + ChatColor.RESET,
            ChatColor.YELLOW + "Komm: " + ChatColor.DARK_AQUA + "Odotamme edelleen yhteydenottoa." + ChatColor.RESET,
            ChatColor.YELLOW + "Komm: " + ChatColor.DARK_AQUA + "Meidän pitäisi olla kommunikaatioetäisyydellä minä hetkenä hyvänsä." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kaikki järjestelmät nominaaliset." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Alamme nyt ohittamaan nyt Altrean tähtisumua." + ChatColor.RESET,
            ChatColor.YELLOW + "Komm: " + ChatColor.DARK_AQUA + "Kapteeni, seuraavan tunnin ajan tähtisumu voi häiritä kommunikaatioyhteyksiämme." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Sör, Teidän on turha odotella komentosillalla. Kutsumme Teitä jos tilanteessa tapahtuu muutoksia." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kapteeni %s?" + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kapteeni? Ovi aukeaa siitä keltaisesta napista." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Ei, kapteeni, se nappi käynnistää kahvinkeittimen." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Selvä, kapteeni. Voitte odottaa myös täällä, mutta täällä ei todennäköisesti tapahdu mitään seuraavaan tuntiin." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Huokaus." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Oletteko aivan varma ettette haluaisi käydä tarkistamassa vaikka konehuonetta vaihtelun vuoksi, kapteeni?" + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Tai vaikka nukkua pienet nokoset." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Selvä on, sör." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Huokaus." + ChatColor.RESET,
            ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Krhm." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Kaikki järjestelmät nominaaliset." + ChatColor.RESET,
            ChatColor.RED + "Försti: " + ChatColor.DARK_AQUA + "Syvä huokaus." + ChatColor.RESET
    };

    private static final String[] CRASHMESSAGES = {
        ChatColor.DARK_AQUA + "Herään tuskaisena. Ilma on raskasta, täynnä vieraita tuoksuja ja savua." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Aluksen kylmä lattia tuntuu epätodellisen kiinteältä selkäni alla." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Yläpuolellani taivaan tumma syvyys hohtaa tuntemattoman planeetan hehkua." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Maailma näyttää karulta, täynnä piilotettuja salaisuuksia. " + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Raahaudun jaloilleni. Aluksen monitorista erottuu kukkulan rinteellä" + ChatColor.RESET,
        ChatColor.DARK_AQUA + "primitiivisiä rakennelmia ja savupatsaita. Silmäni siristyvät epäuskosta." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Elämää. " + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Aluksen miehistö… missä he ovat? Pääni jyskyttää, mutta totuus iskee terävästi:" + ChatColor.RESET,
        ChatColor.DARK_AQUA + "He luulivat minua kuolleeksi. Jättivät minut tänne." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Skanneri näyttää horisontissa liikettä. Varjoja." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "En tiedä, ovatko ne ystäviä vai vihollisia. Yksi asia on selvä:" + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Olen yksin ja vieraassa maailmassa. Alukseni ei ole menossa minnekään." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Onko tämä kuoleman odotushuone vai uusi alku? " + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Käännän kasvoni tuntematonta kohti." + ChatColor.RESET,
        ChatColor.DARK_AQUA + "Jossain siellä, elämän ja kuoleman välissä, odottaa vastaus." + ChatColor.RESET,
    };

    private static final String advanceMessage1 = ChatColor.DARK_AQUA + "\nKonehuoneen kaiutin räsähti eloon." + ChatColor.RESET;
    private static final String advanceMessage2 = ChatColor.DARK_AQUA + "\"Konehuone: Kapteeni! Perävakauttimen kanssa on ongelmia, vaikuttaa aivan siltä kuin... krrrzzzssshhh—\"" + ChatColor.RESET;
    private static final String advanceMessage3 = ChatColor.DARK_AQUA + "Koko alus tärähti. Kipinöitä lensi ja hälytyssireenit ulvoivat." + ChatColor.RESET;
    private static final String advanceMessage4 = ChatColor.AQUA + "Navi: " + ChatColor.DARK_AQUA + "Kapteeni! Olemme törmäyskurssilla tuon tuntemattoman kuun kanssa! Ohjaus ei vastaa!\"" + ChatColor.RESET;
    private static final String advanceMessage5 = ChatColor.DARK_AQUA + "\"Energiasuojat ylös! Varautukaa törmäykseen!\" huusin, mutta sanat hukkuivat korvia raastavaan räjähdykseen. Metallin kääntyvä ulina ja sitten… tyhjyys.\n" + ChatColor.RESET;

    // 54 entries.
    private static final long[] DELAYS = {
            4L, 12L, 4L, 8L, 6L, 34L, 48L, 96L, 2L, 4L, 96L, 96L, 2L, 48L, 8L, 12L, 12L, 5L, 32L, 8L, 48L, 32L, 12L, 8L, 12L, 8L, 14L, 8L, 8L, 120L, 48L, 4L, 32L, 22L, 8L, 134L, 12L, 68L, 8L, 240L, 12L, 8L, 5L, 24L, 32L, 16L, 32L, 32L, 4L, 8L, 32L, 120L, 120L, 5L, 5L
    };

    // Hardcoded locations for explosions.
    private Location explosionLocation1;
    private Location explosionLocation2;
    private Location explosionLocation3;
    private Location explosionLocation4;
    private Location explosionLocation5;
    private Location explosionLocation6;

    // List of random locations.
    private Location[] explosionLocations;

    public Introduction(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        loopParticleEffects();

        // Hardcoded locations for explosions.
        explosionLocation1 = server.getWorld("Tyhjyys").getBlockAt(20, 300, -13).getLocation();
        explosionLocation2 = server.getWorld("Tyhjyys").getBlockAt(22, 298, -11).getLocation();
        explosionLocation3 = server.getWorld("Tyhjyys").getBlockAt(21, 298, -7).getLocation();
        explosionLocation4 = server.getWorld("Tyhjyys").getBlockAt(20, 300, -6).getLocation();
        explosionLocation5 = server.getWorld("Tyhjyys").getBlockAt(12, 300, -3).getLocation();
        explosionLocation6 = server.getWorld("Tyhjyys").getBlockAt(12, 300, -17).getLocation();

        // List of random locations.
        explosionLocations = new Location[] { explosionLocation1, explosionLocation2, explosionLocation3, explosionLocation4, explosionLocation5, explosionLocation6 };
    }

    @EventHandler public void onPlayerJoinTyhjyys(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        if (!"Tyhjyys".equals(worldName)) {
            return;
        }

        // Just in case because this sometimes bugs out, let's set time of Tyhjyys to 14000.
        player.getWorld().setTime(14000L);

        long delay = 0L;
        for (int i = 0; i < MESSAGES.length; i++) {
            String message = String.format(MESSAGES[i], player.getName());
            delay += DELAYS[i];
            BukkitTask task = sendMessageWithDelay(player, delay, message);
            addPlayerTask(player, task);
        }
    }


    private BukkitTask sendMessageWithDelay(Player player, long delaySeconds, String message) {
        return server.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && "Tyhjyys".equals(player.getWorld().getName())) {
                String chatTimestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
                player.sendMessage(ChatColor.DARK_AQUA + "[" + chatTimestamp + "] " + message + ChatColor.RESET);
                player.playSound(player, "minecraft:block.note_block.bell", 0.8f, 1.0f);
            }
        }, delaySeconds * 20L);
    }

    private BukkitTask sendCrashMessageWithDelay(Player player, long delaySeconds, String message) {
        return server.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && "Kuumaa".equals(player.getWorld().getName())) {
                String chatTimestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
                player.sendMessage(ChatColor.DARK_AQUA + "[" + chatTimestamp + "] " + message + ChatColor.RESET);
                player.playSound(player, "minecraft:block.note_block.bell", 0.8f, 1.0f);
            }
        }, delaySeconds * 20L);
    }


    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cancelAllTasksForPlayer(player);
    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Only op can use this command - or console.
        if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
            return false;
        }

        if (command.getName().equalsIgnoreCase("hyväksypelaaja")) {
            // There needs to be a single argument, the player name.
            if (args.length != 1) {
                return false;
            }
        }

        hyväksyPelaaja(sender, args[0]);

        return true;
    }

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    private void hyväksyPelaaja(@NotNull CommandSender sender, @NotNull String arg) {
        Player player = server.getPlayer(arg);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Virhe: Pelaajaa ei löytynyt.");
            return;
        }

        var playerWorld = player.getWorld();

        if (!"Tyhjyys".equals(playerWorld.getName())) {
            sender.sendMessage(ChatColor.RED + "Virhe: " + player.getName() + " on jo hyväksytty, hän on maailmassa " + playerWorld.getName() + " sijainnissa " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ".");

            // Check if player has group "pelaaja" and if not, add it.
            server.dispatchCommand(sender, "lp user " + player.getName() + " parent add pelaaja");
            sender.sendMessage(ChatColor.DARK_AQUA + "Pelaajalle " + player.getName() + " kuitenkin lisättiin ryhmä Pelaaja.");
            return;
        }

        // Cancel all tasks for the player
        cancelAllTasksForPlayer(player);

        sender.sendMessage(ChatColor.DARK_AQUA + "Pelaaja " + player.getName() + " hyväksytty.");

        // Check if player has group "pelaaja" and if not, add it.
        server.dispatchCommand(sender, "lp user " + player.getName() + " parent add pelaaja");
        sender.sendMessage(ChatColor.DARK_AQUA + "Pelaajalle " + player.getName() + " lisättiin ryhmä Pelaaja.");

        // Show effects and advance the story.
        advanceStoryInShip(player);

        // Execute /mv tp Kuumaa to move the player to spawn after a delay of 15 seconds.
        server.getScheduler().runTaskLater(plugin, () -> {
            // Apply blindness effect on player.
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, 20 * 10, 1));
            // Apply slowness effect on player.
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, 45 * 20, 2));

            server.dispatchCommand(sender, "mv tp " + player.getName() + " Kuumaa");
        }, 18L * 20L); // 18 seconds delay (20 ticks per second)

        // Show final texts after the crash.
        server.getScheduler().runTaskLater(plugin, () -> {
            // Apply dizzy effect on player.
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.NAUSEA, 20 * 10, 2));

            showCrashTexts(player);
        }, 20L * 20L); // 20 seconds delay (20 ticks per second)
    }

    private void advanceStoryInShip(Player player) {
        long delay = 0L;

        delay += 3L;
        BukkitTask task1 = sendMessageWithDelay(player, delay, advanceMessage1);
        addPlayerTask(player, task1);

        delay += 3L;
        BukkitTask task2 = sendMessageWithDelay(player, delay, advanceMessage2);
        addPlayerTask(player, task2);

        delay += 3L;
        BukkitTask task3 = sendMessageWithDelay(player, delay, advanceMessage3);
        addPlayerTask(player, task3);

        // Start the fake explosion effects every 0.5 seconds for 10 seconds.
        BukkitTask task = server.getScheduler().runTaskTimer(plugin, new Runnable() {
            private int counter = 0;

            @Override
            public void run() {
                if (counter >= 20) { // 20 times every 0.5 seconds equals 10 seconds
                    this.cancel();
                    return;
                }
                createFakeExplosionEffects();
                counter++;
            }

            private void cancel() {
                Set<BukkitTask> tasks = playerTasks.remove(player.toString());
                if (tasks != null) {
                    for (BukkitTask task : tasks) {
                        task.cancel();
                    }
                }
            }

        }, delay * 20L, 8L); // Initial delay, repeat every 0.5 seconds (10 ticks)

        addPlayerTask(player, task);

        delay += 3L;
        BukkitTask task4 = sendMessageWithDelay(player, delay, advanceMessage4);
        addPlayerTask(player, task4);

        delay += 3L;
        BukkitTask task5 = sendMessageWithDelay(player, delay, advanceMessage5);
        addPlayerTask(player, task5);
    }

    private void showCrashTexts(Player player) {
        long delay = 2L;
        for (int i = 0; i < CRASHMESSAGES.length; i++) {
            String message = CRASHMESSAGES[i];
            if (i == 0) {
                message = "\n\n" + message;
            }

            delay += 2L; // Increment delay for the next message
            BukkitTask task = sendCrashMessageWithDelay(player, delay, message);
            addPlayerTask(player, task);
        }
    }

    // Create fake explosions at random location.
    private void createFakeExplosionEffects() {
        Location location = explosionLocations[(int) (Math.random() * explosionLocations.length)];
        server.getWorld("Tyhjyys").createExplosion(location, 0.0f, false, false);
        // Show explosion particles at the same location.
        server.getWorld("Tyhjyys").spawnParticle(Particle.EXPLOSION_EMITTER, location, 20, 0.0, 0.0, 0.0, 0.0);
    }

    public int addPlayerTask(Player player, BukkitTask task) {
        String playerId = player.toString();
        int taskId = nextTaskId.incrementAndGet();
        playerTasks.computeIfAbsent(playerId, k -> new HashSet<>()).add(task);
        taskIdMap.put(taskId, task);
        return taskId;
    }

    private void cancelAllTasksForPlayer(Player player) {
        String playerId = player.toString();
        Set<BukkitTask> tasks = playerTasks.remove(playerId);
        if (tasks != null) {
            for (BukkitTask task : tasks) {
                task.cancel();

                // Find and remove the task from the taskIdMap
                taskIdMap.entrySet().removeIf(entry -> entry.getValue() == task);
            }
        }
    }


    public boolean cancelAndRemoveTask(int taskId) {
        BukkitTask task = taskIdMap.remove(taskId);
        if (task == null) {
            return false;
        }
        task.cancel();
        for (Set<BukkitTask> playerTasks : playerTasks.values()) {
            playerTasks.remove(task);
        }
        return true;
    }

    // Loop particle effects from fixed position constantly.
    private void loopParticleEffects() {
        server.getScheduler().runTaskTimer(plugin, () -> {
            var world = server.getWorld("Tyhjyys");

            var speed = 0.3;

            // Right side of the ship. Randomized Y range 301.0 to 304.0
            var rightLocation = new Location(world, 42, 295.0 + Math.random() * 9.0, -1 + Math.random() * 2.0);
            var rightLocation2 = new Location(world, 44, 295.0 + Math.random() * 9.0, -1 + Math.random() * 2.0);

            // Left side of the ship. Y range 301.0 to 304.0
            var leftLocation = new Location(world, 42, 295.0 + Math.random() * 9.0, -18 + Math.random() * 2.0);
            var leftLocation2 = new Location(world, 44, 295.0 + Math.random() * 9.0, -18 + Math.random() * 2.0);

            // Top of the ship. Randomized Z range -19.0 to +2.0
            var topLocation = new Location(world, 42, 307, -20.0 + Math.random() * 22.0);
            var topLocation2 = new Location(world, 44, 307, -20.0 + Math.random() * 22.0);

            world.spawnParticle(Particle.FIREWORK, rightLocation, 0, -20.0, 0.0, 0.0, speed, null, true);
            world.spawnParticle(Particle.FIREWORK, rightLocation2, 0, -20.0, 0.0, 0.0, speed, null, true);
            world.spawnParticle(Particle.FIREWORK, leftLocation, 0, -20.0, 0.0, 0.0, speed, null, true);
            world.spawnParticle(Particle.FIREWORK, leftLocation2, 0, -20.0, 0.0, 0.0, speed, null, true);
            world.spawnParticle(Particle.FIREWORK, topLocation, 0, -20.0, 0.0, 0.0, speed, null, true);
            world.spawnParticle(Particle.FIREWORK, topLocation2, 0, -20.0, 0.0, 0.0, speed, null, true);
        }, 0L, 1L); // Initial delay, repeat every 1 ticks
    }

}
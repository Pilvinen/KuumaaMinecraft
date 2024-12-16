package starandserpent.minecraft.criticalfixes;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    public Commands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /komennot
        if (command.getName().equalsIgnoreCase("komennot")
        || command.getName().equalsIgnoreCase("commands")
        || command.getName().equalsIgnoreCase("help")
        || command.getName().equalsIgnoreCase("apua")
        || command.getName().equalsIgnoreCase("info")) {
            commands(sender);
            return true;
        }

        if (command.getName().equalsIgnoreCase("discord")) {
            discord(sender);
            return true;
        }

        if (command.getName().equalsIgnoreCase("johdanto")) {
            johdanto(sender);
            return true;
        }

        return false;
    }


    // Show help about available commands to the player.
    private void commands(CommandSender sender) {
        StringBuilder message = new StringBuilder();
        message.append(ChatColor.YELLOW).append("Käytettävissä olevat komennot:").append(ChatColor.RESET).append("\n")
                .append(ChatColor.WHITE).append("/komennot, /commands, /help, /apua, /info").append(ChatColor.RESET).append("\n")
                .append(ChatColor.GRAY).append("Tämä komento. Näyttää käytettävissä olevat komennot.").append(ChatColor.RESET).append("\n")
                .append(ChatColor.WHITE).append("/kuumaa").append(ChatColor.RESET).append("\n")
                .append(ChatColor.GRAY).append("Tietoa Kuumaasta.").append(ChatColor.RESET).append("\n")
                .append(ChatColor.WHITE).append("/discord").append(ChatColor.RESET).append("\n")
                .append(ChatColor.GRAY).append("Kuumaan Discord palvelimen linkki.").append(ChatColor.RESET).append("\n")
                .append(ChatColor.WHITE).append("/tilastoja").append(ChatColor.RESET).append("\n")
                .append(ChatColor.GRAY).append("Näyttää palvelimen yleisiä tilastot.").append(ChatColor.RESET).append("\n")
                .append(ChatColor.WHITE).append("/top10").append(ChatColor.RESET).append("\n")
                .append(ChatColor.GRAY).append("Näyttää palvelimen kymmenen aktiivisinta pelaajaa.").append(ChatColor.RESET).append("\n")
                .append(ChatColor.WHITE).append("/missä <pelaaja>").append(ChatColor.RESET).append("\n")
                .append(ChatColor.GRAY).append("Näyttää tietoja tietystä pelaajasta.").append(ChatColor.RESET);
        sender.sendMessage(message.toString());
    }

    private void discord(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Kuumaan Discord:" + ChatColor.RESET + "\n" +
                ChatColor.YELLOW + "https://discord.gg/esmqVrPG8d" + ChatColor.RESET);
    }

    private void johdanto(CommandSender sender) {
        StringBuilder message = new StringBuilder();
        message.append(ChatColor.GRAY).append("Avaruusalus Asterion halkoi kosmoksen mustaa tyhjyyttä, kotiplaneetan läheisyys väreillen kuin lupaus.\n" +
                        "Konehuoneen kaiutin räsähti eloon." +
                        "\"Konehuone: Kapteeni! Perävakauttimen kanssa on ongelmia, vaikuttaa aivan siltä kuin... krrrzzzssshhh—\"\n" +
                        "Koko alus tärähti. Kipinöitä lensi ja hälytyssireenit ulvoivat.\n" +
                        "\"Kapteeni! Olemme törmäyskurssilla tuon tuntemattoman kuun kanssa! Ohjaus ei vastaa!\"\n" +
                        "\"Energiasuojat ylös! Varautukaa törmäykseen!\" huusin, mutta sanat hukkuivat korvia raastavaan räjähdykseen. Metallin kääntyvä ulina ja sitten… tyhjyys.\n" +
                        "Herään tuskaisena. Ilma on raskasta, täynnä vieraita tuoksuja. Kuun kylmä maa tuntuu epätodellisen kiinteältä selkäni alla. Yläpuolellani taivaan tumma syvyys hohtaa tuntemattoman planeetan hehkua. Maailma näyttää samaan aikaan elottomalta ja täynnä salaisuuksia." +
                        "Raahaudun jaloilleni. Näköpiirissä, kukkulan rinteellä, erottuu primitiivisiä rakennelmia ja savupatsaita. Silmäni siristyvät epäuskosta. Elämää." +
                        "Aluksen miehistö… missä he ovat? Pääni jyskyttää, mutta totuus iskee terävästi: he luulivat minua kuolleeksi. Jättivät minut tänne." +
                        "Näen horisontissa liikettä. Varjoja. En tiedä, ovatko ne ystäviä vai vihollisia. Yksi asia on selvä: olen yksin ja vieraassa maailmassa. Alukseni ei ole menossa minnekään. Onko tämä kuoleman odotushuone vai uusi alku?" +
                        "Käännän kasvoni tuntematonta planeettaa kohti. Jossain siellä, elämän ja kuoleman välissä, odottaa vastaus.").append(ChatColor.RESET).append("\n");

        sender.sendMessage(message.toString());
    }
}

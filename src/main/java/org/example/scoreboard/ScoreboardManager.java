package org.example.scoreboard;

import org.apfloat.Apfloat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.example.api.UtilPlayer;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;
import org.example.rankupSystem.DimensionalRift;
import org.example.rankupSystem.RiftConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {
    private static final String SCOREBOARD_TITLE = "§6§l ⛏ Prison ⛏";
    private static final String OBJECTIVE_NAME = "sb";
    private static final String OBJECTIVE_CRITERIA = "dummy";
    private final UtilPlayer utilPlayer;
    private Scoreboard bukkitScoreboard;
    private Objective bukkitObjective;
    private Map<Section, List<ScoreboardLine>> sectionLines;
    private int currentColorIndex = 0;

    public ScoreboardManager(UtilPlayer utilPlayer) {
        this.utilPlayer = utilPlayer; // Umbenannt von 'player' zu 'utilPlayer' für Klarheit
        this.sectionLines = new HashMap<>();
        setupScoreboard();
    }

    private void setupScoreboard() {
        assert Bukkit.getScoreboardManager() != null;
        this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.bukkitObjective = bukkitScoreboard.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERIA, SCOREBOARD_TITLE);
        this.bukkitObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Erhöhe die Anzahl der Zeilen in der Skyblock-Sektion
        setupSkyblockInfoSection();
        setupCurrencySection();
        setupFooter();
    }

    private void setupSkyblockInfoSection() {
        List<ScoreboardLine> lines = new ArrayList<>();
        // Wir brauchen jetzt 3 Zeilen: Leerzeile, Rift/Rank, Datum/Instanz
        lines.add(new ScoreboardLine(this, 14)); // Leerzeile
        lines.add(new ScoreboardLine(this, 13)); // Rift/Rank Info
        lines.add(new ScoreboardLine(this, 12)); // Datum/Instanz
        sectionLines.put(Section.SKYBLOCK_INFO, lines);
    }

    private void setupLocationSection() {
        List<ScoreboardLine> lines = new ArrayList<>();
        lines.add(new ScoreboardLine(this, 11)); // Diese Zeile rückt nach unten
        sectionLines.put(Section.LOCATION, lines);
    }

    private void setupCurrencySection() {
        List<ScoreboardLine> lines = new ArrayList<>();
        // Passe die Slot-Nummern an, da alles nach unten gerutscht ist
        lines.add(new ScoreboardLine(this, 10)); // Leerzeile
        lines.add(new ScoreboardLine(this, 9));
        lines.add(new ScoreboardLine(this, 8));
        lines.add(new ScoreboardLine(this, 7));
        sectionLines.put(Section.CURRENCY, lines);
    }

    private void setupFooter() {
        // ... (unverändert) ...
    }

    // --- DIE WICHTIGSTE ÄNDERUNG ---
    public void updateSkyblockInfo() {
        List<ScoreboardLine> lines = sectionLines.get(Section.SKYBLOCK_INFO);

        // Hole das Rift-Objekt vom Spieler
        DimensionalRift rift = utilPlayer.getRiftProgression();

        // Zeile 1: Leerzeile
        lines.get(0).updateText("§a"); // Leere Zeile mit unsichtbarem Farbcode

        // Zeile 2: Rift- und Rang-Informationen
        String riftIcon = "§7§l\uD83C\uDF0A";
        String layerIcon = "§7§l☄";
        // Ein funkelnder Stern als Icon
        String dimensionText = String.format("§bDimension §7%d", rift.getCurrentDimension());
        String layerText = String.format("§bLayer §7%d§7", rift.getCurrentLayer());

        // Füge alles zu einer Zeile zusammen

        // Zeile 3: Datum und Instanz (wie vorher)
        lines.get(1).updateText(String.format(" %s %s", riftIcon, dimensionText));
        lines.get(2).updateText(String.format(" %s %s", layerIcon, layerText));
    }

    public void updateCurrency() {
        List<ScoreboardLine> lines = sectionLines.get(Section.CURRENCY);

        Apfloat etokens = EconomyService.getBalance(utilPlayer.getPlayer().getUniqueId(), Currency.ETOKENS);
        Apfloat money = EconomyService.getBalance(utilPlayer.getPlayer().getUniqueId(), Currency.MONEY);

        lines.get(0).updateText("§b"); // Leerzeile
        lines.get(1).updateText("§6§lCurrency");
        lines.get(2).updateText(String.format("  §b\uD83D\uDCB0 Money: §f%s", Economy.format(money)));
        lines.get(3).updateText(String.format("  §b\uD83E\uDE99 ETokens: §f%s", Economy.format(etokens)));
    }

    public void updateScoreboard() {
        updateSkyblockInfo();
        updateCurrency();
        utilPlayer.getPlayer().setScoreboard(bukkitScoreboard);
    }

    public String generateUniqueEntry() {
        ChatColor[] colors = ChatColor.values();
        int prefixColor = currentColorIndex / colors.length;
        int suffixColor = currentColorIndex % colors.length;

        String entry = "" + colors[prefixColor] + colors[suffixColor];

        currentColorIndex++;
        return entry;
    }

    /* -------------------- GETTERS -------------------- */
    private String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    private String getInstance() {
        return " §8staging";
    }


    public UtilPlayer getPlayer() {
        return utilPlayer;
    }

    public Scoreboard getBukkitScoreboard() {
        return bukkitScoreboard;
    }

    public Objective getBukkitObjective() {
        return bukkitObjective;
    }

    public Map<Section, List<ScoreboardLine>> getSectionLines() {
        return sectionLines;
    }

    public int getCurrentColorIndex() {
        return currentColorIndex;
    }

    public enum Section {SKYBLOCK_INFO, LOCATION, CURRENCY, ACTIVITY, FOOTER}
}

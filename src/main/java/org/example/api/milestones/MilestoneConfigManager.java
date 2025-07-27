package org.example.api.milestones;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import org.example.api.milestones.Milestone; // Stelle sicher, dass der Import zu deiner Milestone-Klasse korrekt ist

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Diese Klasse verwaltet das Laden und Erstellen der milestones.yml Konfigurationsdatei.
 * Sie erstellt eine Standard-Konfiguration direkt aus dem Code heraus, wenn keine vorhanden ist.
 */
public class MilestoneConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;

    private final Map<String, List<Milestone>> milestoneCache = new HashMap<>();

    public MilestoneConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "milestones.yml");
    }

    public void initialize() {
        // Dieser Aufruf stellt sicher, dass die Datei erstellt wird, bevor wir versuchen, sie zu laden.
        createDefaultConfigIfNotExists();
        loadMilestones();
    }

    /**
     * Prüft, ob die milestones.yml im Plugin-Ordner existiert.
     * Wenn nicht, wird sie mit einem Standard-Inhalt direkt aus diesem Code erstellt.
     */
    private void createDefaultConfigIfNotExists() {
        if (!configFile.exists()) {
            plugin.getLogger().info("milestones.yml nicht gefunden. Erstelle eine neue Standardkonfiguration direkt aus dem Code...");

            // HIER IST DIE MAGIE: Anstatt eine Ressource zu kopieren, schreiben wir die Datei Zeile für Zeile.
            try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
                writer.println("# Konfiguration für die Meilensteine der Verzauberungen.");
                writer.println("# Diese Datei wurde beim ersten Start des Plugins automatisch generiert.");
                writer.println("enchants:");
                writer.println("  # Die ID muss mit der ID in der jeweiligen Enchant-Klasse übereinstimmen.");
                writer.println("  fortune_enchant:");
                writer.println("    milestones:");
                writer.println("      - level: 1");
                writer.println("        requiredProcCount: 1000");
                writer.println("        buffs:");
                writer.println("          FORTUNE_CHANCE: 0.01 # 1% höhere Chance");
                writer.println("      - level: 2");
                writer.println("        requiredProcCount: 10000");
                writer.println("        buffs:");
                writer.println("          FORTUNE_CHANCE: 0.02");
                writer.println(""); // Leere Zeile für die Lesbarkeit
                writer.println("  jack_hammer_enchant:");
                writer.println("    milestones:");
                writer.println("      - level: 1");
                writer.println("        requiredProcCount: 500");
                writer.println("        buffs:");
                writer.println("          BLOCK_BREAK_RADIUS: 1.0 # Bricht 1 Block extra");
                writer.println("      - level: 2");
                writer.println("        requiredProcCount: 2500");
                writer.println("        buffs:");
                writer.println("          BLOCK_BREAK_RADIUS: 2.0");

                plugin.getLogger().info("Standard-milestones.yml wurde erfolgreich erstellt.");

            } catch (IOException e) {
                // Falls das Erstellen fehlschlägt (z.B. wegen fehlender Berechtigungen)
                plugin.getLogger().log(Level.SEVERE, "Konnte die Standard-milestones.yml nicht erstellen!", e);
            }
        }
    }

    /**
     * Lädt die Meilensteine aus der milestones.yml Datei vom Server und speichert sie im Cache.
     * Diese Methode bleibt unverändert.
     */
    private void loadMilestones() {
        if (!configFile.exists()) {
            plugin.getLogger().warning("Laden der Meilensteine übersprungen, da die Konfigurationsdatei nicht erstellt werden konnte.");
            return;
        }

        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            // ... Der Rest dieser Methode ist identisch zur vorherigen Version ...
            Map<String, Object> data = yaml.load(inputStream);
            if (data == null || !data.containsKey("enchants")) {
                plugin.getLogger().warning("milestones.yml ist leer oder ungültig. Es wurden keine Meilensteine geladen.");
                return;
            }
            Map<String, Object> enchantsData = (Map<String, Object>) data.get("enchants");
            if (enchantsData == null) return;
            for (Map.Entry<String, Object> entry : enchantsData.entrySet()) {
                String enchantId = entry.getKey();
                List<Milestone> parsedMilestones = new ArrayList<>();
                Map<String, Object> enchantConfig = (Map<String, Object>) entry.getValue();
                if (enchantConfig == null || !enchantConfig.containsKey("milestones")) continue;
                List<Map<String, Object>> milestonesList = (List<Map<String, Object>>) enchantConfig.get("milestones");
                if (milestonesList == null) continue;
                for (Map<String, Object> milestoneData : milestonesList) {
                    int level = (int) milestoneData.get("level");
                    long requiredProcCount = ((Number) milestoneData.get("requiredProcCount")).longValue();
                    Map<String, Double> buffs = (Map<String, Double>) milestoneData.getOrDefault("buffs", Collections.emptyMap());
                    parsedMilestones.add(new Milestone(enchantId, level, requiredProcCount, buffs));
                }
                milestoneCache.put(enchantId, parsedMilestones);
            }
            plugin.getLogger().info("Erfolgreich Meilensteine für " + milestoneCache.size() + " Verzauberung(en) aus der Config geladen.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Ein Fehler ist beim Laden der milestones.yml aufgetreten:", e);
        }
    }

    public List<Milestone> getMilestonesFor(String enchantId) {
        return milestoneCache.getOrDefault(enchantId, Collections.emptyList());
    }
}
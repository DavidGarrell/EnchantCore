package org.example.api.perks;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.example.Main;
import org.example.api.perks.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PerkManager {
    private final Main plugin;
    private final List<PerkTemplate> perkTemplates = new ArrayList<>();
    private final Random random = new Random();

    public PerkManager(Main plugin) {
        this.plugin = plugin;
        loadPerks();
    }

    private void loadPerks() {
        // Erstelle die Standard-Config direkt aus dem Code, falls sie nicht existiert
        createDefaultConfigIfNotExists();

        File configFile = new File(plugin.getDataFolder(), "perks.yml");
        if (!configFile.exists()) {
            plugin.getLogger().severe("Could not load perks because perks.yml could not be created!");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection perksSection = config.getConfigurationSection("perks");
        if (perksSection == null) {
            plugin.getLogger().warning("No 'perks' section found in perks.yml!");
            return;
        }

        for (String key : perksSection.getKeys(false)) {
            ConfigurationSection perkConfig = perksSection.getConfigurationSection(key);
            if (perkConfig == null) continue;

            try {
                String name = perkConfig.getString("name", "Unnamed Perk");
                String category = perkConfig.getString("category", "§8DEFAULT PERK");
                String description = perkConfig.getString("description", "No description.");
                Rarity rarity = Rarity.valueOf(perkConfig.getString("rarity", "COMMON").toUpperCase());
                String texture = perkConfig.getString("texture", "");

                List<String> buffStrings = perkConfig.getStringList("buffs");
                List<PerkType> buffs = buffStrings.stream()
                        .map(s -> PerkType.valueOf(s.toUpperCase()))
                        .collect(Collectors.toList());

                perkTemplates.add(new PerkTemplate(name, category, description, rarity, buffs, texture));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Failed to load perk '" + key + "': Invalid rarity or buff type. Skipping.");
            }
        }
        plugin.getLogger().info("Successfully loaded " + perkTemplates.size() + " perks from perks.yml.");
    }

    /**
     * Erstellt die perks.yml direkt im Plugin-Ordner mit Standard-Werten,
     * falls die Datei noch nicht existiert.
     */
    private void createDefaultConfigIfNotExists() {
        File configFile = new File(plugin.getDataFolder(), "perks.yml");
        if (!configFile.exists()) {
            plugin.getLogger().info("perks.yml not found, creating a new default file...");

            // Stelle sicher, dass der Plugin-Ordner existiert
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
                // Schreibe den gesamten YAML-Inhalt Zeile für Zeile in die neue Datei
                writer.println("# Konfiguration für alle im Spiel verfügbaren Perks.");
                writer.println("# Du kannst hier beliebig viele Perks hinzufügen oder bestehende ändern/entfernen.");
                writer.println("perks:");
                writer.println("  # --- Common Perks ---");
                writer.println("  common_money:");
                writer.println("    name: \"Money Perk\"");
                writer.println("    category: \"§8PICKAXE PERK\"");
                writer.println("    description: \"Provides your Pickaxe with an additional Money Boost.\"");
                writer.println("    rarity: COMMON");
                writer.println("    buffs: [MONEY_BOOST]");
                writer.println("    texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQzNTllZDM2ODIyMTU4M2E4MzA3Mjg4OGQ5YjJkYjQwMWRiNzM5ZTI0NmYyN2MyOTI3YTVhYTQzMmI4ODk5NSJ9fX0=\"");
                writer.println("");
                writer.println("  common_token:");
                writer.println("    name: \"Token Perk\"");
                writer.println("    category: \"§8PICKAXE PERK\"");
                writer.println("    description: \"Provides your Pickaxe with an additional EToken Boost.\"");
                writer.println("    rarity: COMMON");
                writer.println("    buffs: [ETOKEN_BOOST]");
                writer.println("    texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA3MTA2YTUxYjI1Y2RkMmU3ZDM4NDQ2YjY4YjYyODFmYWI2Y2Y0ODE3MDcwMDIzZGYzMjMyYTkxZTY3Y2MifX19\"");
                writer.println("");
                // ... (füge hier die restlichen Perks nach dem gleichen Muster ein) ...
                writer.println("  # --- Legendary Perk ---");
                writer.println("  legendary_universal:");
                writer.println("    name: \"Universal Perk\"");
                writer.println("    category: \"§8PICKAXE PERK\"");
                writer.println("    description: \"Provides your Pickaxe with a Boost to all stats.\"");
                writer.println("    rarity: LEGENDARY");
                writer.println("    buffs: [MONEY_BOOST, ETOKEN_BOOST, XP_BOOST, ENCHANT_PROC_BOOST]");
                writer.println("    texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODhiMTI5MzNmZDY2YjEwYjM0ODMyMzJjYjU5NTRjMGE3NTg1ZjE2YjNhYjZkNzMzYWI0ZGEyN2QzZGI4ZTU4NCJ9fX0=\"");

            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create perks.yml!", e);
            }
        }
    }

    public List<PerkTemplate> getAllTemplates() {
        return Collections.unmodifiableList(perkTemplates);
    }

    public ActivePerk rollNewPerk() {
        // 1. Seltenheit auswürfeln
        double roll = random.nextDouble();
        Rarity rolledRarity = Rarity.COMMON; // Fallback
        double cumulativeProb = 0.0;
        for (Rarity r : Rarity.values()) {
            cumulativeProb += r.getProbability();
            if (roll < cumulativeProb) {
                rolledRarity = r;
                break;
            }
        }

        // 2. Einen zufälligen Perk-Typ für diese Seltenheit auswählen
        final Rarity finalRolledRarity = rolledRarity;
        List<PerkTemplate> possibleTemplates = perkTemplates.stream()
                .filter(t -> t.getRarity() == finalRolledRarity)
                .collect(Collectors.toList());
        PerkTemplate chosenTemplate = possibleTemplates.get(random.nextInt(possibleTemplates.size()));

        // 3. Ein zufälliges Level (1-5) auswürfeln
        int chosenLevel = random.nextInt(5) + 1;

        // 4. Den finalen, aktiven Perk erstellen
        return new ActivePerk(chosenTemplate, chosenLevel);
    }
}
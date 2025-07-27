package org.example.settings;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GlobalSettings {
    private final JavaPlugin plugin;
    private boolean useEnchantTokens = false; // Standardwert
    private boolean enchantProcByProcent = false;

    public GlobalSettings(JavaPlugin plugin) {
        this.plugin = plugin;
        loadSettings(); // Lädt die Einstellungen beim Erstellen der Instanz
    }

    // Lädt die Einstellungen aus der config.yml
    public void loadSettings() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        // Wenn die config.yml noch nicht existiert, erstelle sie mit Kommentaren
        if (!configFile.exists()) {
            saveDefaultConfigWithComments();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Überprüfe, ob der Eintrag vorhanden ist, ansonsten Standardwert setzen
        if (config.contains("settings.useEnchantTokens")) {
            this.useEnchantTokens = config.getBoolean("settings.useEnchantTokens");
        } else {
            config.set("settings.useEnchantTokens", this.useEnchantTokens);
            saveSettings();
        }
    }

    // Speichert die Einstellungen in die config.yml
    public void saveSettings() {
        FileConfiguration config = plugin.getConfig();
        config.set("settings.useEnchantTokens", this.useEnchantTokens);
        config.set("settings.enchantProcByProcent", this.enchantProcByProcent);
        plugin.saveConfig(); // Speichert die Änderungen in die Datei
    }

    // Erstellt die Standard-Konfigurationsdatei mit Kommentaren
    private void saveDefaultConfigWithComments() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            if (configFile.createNewFile()) {
                try (PrintWriter writer = new PrintWriter(configFile)) {
                    writer.println("# Global Settings Configuration");
                    writer.println("settings:");
                    writer.println("  # default value: true");
                    writer.println("  useEnchantTokens: true");
                    writer.println("");
                    writer.println("  # default value: false");
                    writer.println("  enchantProcByProcent: false");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUseEnchantTokens() {
        return useEnchantTokens;
    }

    public void setUseEnchantTokens(boolean enchantProcByProcent) {
        this.useEnchantTokens = useEnchantTokens;
        saveSettings(); // Speichert die Änderungen, sobald der Wert gesetzt wird
    }

    public boolean isEnchantProcByProcent() {
        return enchantProcByProcent;
    }

    public void setEnchantProcByProcent(boolean enchantProcByProcent) {
        this.enchantProcByProcent = enchantProcByProcent;
        saveSettings();
    }
}

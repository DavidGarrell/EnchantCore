package org.example.economy;

import org.apfloat.Apfloat;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Ein statischer Service, der den Zugriff auf den aktiven Economy Provider ermöglicht.
 * Dies ist der einzige Punkt, den der Rest deines Plugins ansprechen sollte.
 */
public final class EconomyService {

    private static EconomyProvider provider;

    /**
     * Initialisiert den Service mit einem spezifischen Provider.
     * Muss in onEnable() aufgerufen werden.
     */
    public static void initialize(EconomyProvider provider) {
        if (EconomyService.provider != null) {
            throw new IllegalStateException("EconomyService is already initialized.");
        }
        EconomyService.provider = provider;
    }

    // --- Fassaden-Methoden ---

    public static Apfloat getBalance(UUID uuid, Currency currency) {
        return provider.getBalance(uuid, currency);
    }

    public static void setBalance(UUID uuid, Currency currency, Apfloat amount) {
        provider.setBalance(uuid, currency, amount);
    }

    public static void addBalance(UUID uuid, Currency currency, Apfloat amount) {
        provider.addBalance(uuid, currency, amount);
    }

    public static boolean subtractBalance(UUID uuid, Currency currency, Apfloat amount) {
        return provider.subtractBalance(uuid, currency, amount);
    }

    public static boolean hasEnough(UUID uuid, Currency currency, Apfloat amount) {
        return provider.hasEnough(uuid, currency, amount);
    }

    // --- Player Join/Quit Handler ---

    public static void handlePlayerJoin(Player player) {
        provider.onPlayerJoin(player);
    }

    public static void handlePlayerQuit(Player player) {
        provider.onPlayerQuit(player);
    }

    public static void handleDisable() {
        if (provider != null) {
            provider.onDisable();
        }
    }

    // Hilfsmethode für das Formatieren, kann hier bleiben.
    public static String format(Apfloat zahl) {
        if (zahl == null) return "0";
        // ... deine Formatierungslogik ...
        return "FormattedValue";
    }
}


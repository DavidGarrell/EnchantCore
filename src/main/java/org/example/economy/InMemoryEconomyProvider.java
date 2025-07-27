package org.example.economy;

import org.apfloat.Apfloat;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class InMemoryEconomyProvider implements EconomyProvider {

    private static final Logger LOGGER = Logger.getLogger(InMemoryEconomyProvider.class.getName());
    private final Map<UUID, Map<Currency, Apfloat>> playerBalances = new ConcurrentHashMap<>();

    @Override
    public Apfloat getBalance(UUID uuid, Currency currency) {
        return playerBalances.getOrDefault(uuid, Collections.emptyMap())
                .getOrDefault(currency, Apfloat.ZERO);
    }

    @Override
    public void setBalance(UUID uuid, Currency currency, Apfloat amount) {
        playerBalances.computeIfAbsent(uuid, k -> new EnumMap<>(Currency.class))
                .put(currency, amount);
        LOGGER.info(() -> "Set balance for " + uuid + " in " + currency + " to " + amount.toString(true));
    }

    @Override
    public void addBalance(UUID uuid, Currency currency, Apfloat amount) {
        if (amount.compareTo(Apfloat.ZERO) <= 0) return;
        playerBalances.computeIfAbsent(uuid, k -> new EnumMap<>(Currency.class))
                .merge(currency, amount, Apfloat::add);
        LOGGER.info(() -> "Added " + amount.toString(true) + " to " + uuid + " in " + currency);
        LOGGER.info(() -> "New balance for " + uuid + " in " + currency + ": " + getBalance(uuid, currency).toString(true));
    }

    @Override
    public boolean subtractBalance(UUID uuid, Currency currency, Apfloat amount) {
        if (amount.compareTo(Apfloat.ZERO) < 0) return false;

        final boolean[] success = {false};
        playerBalances.computeIfPresent(uuid, (key, balanceMap) -> {
            balanceMap.compute(currency, (c, currentBalance) -> {
                if (currentBalance != null && currentBalance.compareTo(amount) >= 0) {
                    success[0] = true;
                    return currentBalance.subtract(amount);
                }
                return currentBalance; // Gib den alten Wert zurück, wenn nicht genug Geld da ist
            });
            return balanceMap;
        });

        if (success[0]) {
            LOGGER.info(() -> "Subtracted " + amount.toString(true) + " from " + uuid + " in " + currency);
        }
        return success[0];
    }

    @Override
    public boolean hasEnough(UUID uuid, Currency currency, Apfloat amount) {
        return getBalance(uuid, currency).compareTo(amount) >= 0;
    }

    @Override
    public void onPlayerJoin(Player player) {
        // Hier würde die Logik zum Laden aus einer Datenbank hinkommen.
        // Für ein reines In-Memory-System ist hier nichts zu tun.
        LOGGER.info("Player " + player.getName() + " joined, economy data ready (in-memory).");
    }

    @Override
    public void onPlayerQuit(Player player) {
        // Hier würde die Logik zum Speichern in einer Datenbank hinkommen.
        // Optional: Entferne Spielerdaten, um Speicher freizugeben.
        // playerBalances.remove(player.getUniqueId());
        LOGGER.info("Player " + player.getName() + " quit, economy data handled (in-memory).");
    }

    @Override
    public void onDisable() {
        // Hier würde die Logik zum Speichern aller Online-Spieler-Daten hinkommen.
        LOGGER.info("Plugin disabling, saving all economy data (in-memory provider).");
    }
}

package org.example.economy;

import org.apfloat.Apfloat;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface EconomyProvider {


    Apfloat getBalance(UUID uuid, Currency currency);


    void setBalance(UUID uuid, Currency currency, Apfloat amount);


    void addBalance(UUID uuid, Currency currency, Apfloat amount);


    boolean subtractBalance(UUID uuid, Currency currency, Apfloat amount);


    boolean hasEnough(UUID uuid, Currency currency, Apfloat amount);


    void onPlayerJoin(Player player);

    void onPlayerQuit(Player player);


    void onDisable();
}
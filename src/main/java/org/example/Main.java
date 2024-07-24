package org.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.example.api.Enchant;
import org.example.api.PlayerData;
import org.example.api.EnchantUtils;
import org.example.api.data.DataSource;
import org.example.enchantments.FortuneEnchant;
import org.example.enchantments.JackHammerEnchant;
import org.example.events.EnchantEventHandler;
import org.example.events.EnchantProcEvent;
import org.example.events.OpenEnchantMenu;
import org.example.events.PlayerJoinEvents;
import org.example.menu.EnchantMenu;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    public List<Enchant> enchants;

    public EnchantMenu enchantMenu;

    public PlayerData playerData;
    public DataSource dataSource;
    public EnchantUtils enchantUtils;

    public Enchant enchant;
    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new OpenEnchantMenu(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEvents(this), this);
        getServer().getPluginManager().registerEvents(new EnchantProcEvent(this), this);

        getServer().getPluginManager().registerEvents(new EnchantEventHandler(), this);


        this.enchants = new ArrayList<>();
        enchantUtils = new EnchantUtils(this);
        dataSource = new DataSource(this);

        FortuneEnchant fortuneEnchant = new FortuneEnchant();
        JackHammerEnchant jackHammerEnchant = new JackHammerEnchant();

        enchantMenu = new EnchantMenu(this);

        enchants.add(fortuneEnchant);
        enchants.add(jackHammerEnchant);


        System.out.println(fortuneEnchant.getDisplayname() + " | " + fortuneEnchant.getDescription());

        System.out.println(fortuneEnchant.calculateProcChance(100));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
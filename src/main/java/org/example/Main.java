package org.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.example.api.Enchant;
import org.example.api.Placeholders;
import org.example.api.gemstones.GemstoneBoxListener;
import org.example.api.milestones.MilestoneConfigManager;
import org.example.api.perks.PerkManager;
import org.example.commands.EnchantProgressCommand;
import org.example.commands.GemstoneBoxCommand;
import org.example.commands.PickaxeCommands;
import org.example.commands.RiftCommand;
import org.example.economy.*;
import org.example.enchantments.*;
import org.example.events.*;
import org.example.items.EnchantPickaxe;
import org.example.listener.GUIListener;
import org.example.menu.EnchantMenu;
import org.example.settings.GlobalSettings;

import java.util.List;

public class Main extends JavaPlugin {

    public List<Enchant> enchants;

    public EnchantMenu enchantMenu;


    //Enchantments
    public JackHammerEnchant jackHammerEnchant;
    public LaserEnchant laserEnchant;
    public EfficientMiner efficientMiner;
    public MeteorEnchant meteorEnchant;
    public PerkManager perkManager;
    public Placeholders placeholders;
    public String pluginName = "EnchantCore";
    public GlobalSettings globalSettings;
    public Enchant enchant;
    public EnchantPickaxe enchantPickaxe;
    public static Economy economy;
    private MilestoneConfigManager milestoneConfigManager;
    private EconomyProvider economyProvider;
    static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        try {
            System.out.println(pluginName + " load plugin...");
            this.globalSettings = new GlobalSettings(this);
            economy = new Economy(this);
            this.milestoneConfigManager = new MilestoneConfigManager(this);
            this.milestoneConfigManager.initialize();
            this.perkManager = new PerkManager(instance);
            getServer().getPluginManager().registerEvents(new OpenEnchantMenu(this), this);
            getServer().getPluginManager().registerEvents(new PlayerJoinEvents(this), this);
            getServer().getPluginManager().registerEvents(new EnchantProcEvent(this), this);
            getServer(). getPluginManager().registerEvents(new GUIListener(), this);
            getServer(). getPluginManager().registerEvents(new PlayerBalanceInitializer(economy), this);
            getServer().getPluginManager().registerEvents(new GemstoneBoxListener(this), this);



            enchantPickaxe = new EnchantPickaxe(this);
            getServer().getPluginManager().registerEvents(enchantPickaxe, this);


            getServer().getPluginManager().registerEvents(new EnchantEventHandler(), this);
            this.getCommand("pickaxe").setExecutor(new PickaxeCommands(this));
            this.getCommand("adminpickaxe").setExecutor(new PickaxeCommands(this));
            this.getCommand("tokens").setExecutor(new EconomyCommands());
            this.getCommand("gemstonebox").setExecutor(new GemstoneBoxCommand());

            RiftCommand riftCommand = new RiftCommand(this);
            getCommand("rift").setExecutor(riftCommand);
            getCommand("rift").setTabCompleter(riftCommand);

            getCommand("jackhammer").setExecutor(new EnchantProgressCommand(this));
            getCommand("laser").setExecutor(new EnchantProgressCommand(this));

            this.economyProvider = new InMemoryEconomyProvider();

            // 2. Initialisiere den statischen Service damit
            EconomyService.initialize(this.economyProvider);

            // 3. Registriere deine Events für Join/Quit
            getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);

            System.out.println(pluginName + " plugin load complete!");
        } catch (Exception e){

            System.out.println(pluginName + " not loaded");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        return instance;
    }

    public MilestoneConfigManager getMilestoneConfigManager() {
        return milestoneConfigManager;
    }
}
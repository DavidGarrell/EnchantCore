package org.example.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class CustomGUIUtils implements InventoryHolder {

    private final Inventory inventory;
    private final Map<Integer, BiConsumer<InventoryClickEvent, ClickType>> clickActions;
    private final Map<Integer, BiConsumer<Player, ItemStack>> dropActions;

    public CustomGUIUtils(String title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.clickActions = new HashMap<>();
        this.dropActions = new HashMap<>();

    }

    public void addItem(int slot, Material material, String name, String[] lore, BiConsumer<InventoryClickEvent, ClickType> onClick) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(List.of(lore));
            }
            item.setItemMeta(meta);
        }

        inventory.setItem(slot, item);

        if (onClick != null) {
            clickActions.put(slot, onClick);
        }
    }

    public void addItem(int slot, Material material, int amount, String name, String[] lore, BiConsumer<InventoryClickEvent, ClickType> onClick) {
        ItemStack item = new ItemStack(material ,amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(List.of(lore));
            }
            item.setItemMeta(meta);
        }

        inventory.setItem(slot, item);

        if (onClick != null) {
            clickActions.put(slot, onClick);
        }
    }


    public void addSkullItem(int slot, String name, String[] lore, String textureUrl, BiConsumer<InventoryClickEvent, ClickType> onClick) {
        ItemStack item = createCustomSkull(textureUrl);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(List.of(lore));
            }
            item.setItemMeta(meta);
        }

        inventory.setItem(slot, item);

        if (onClick != null) {
            clickActions.put(slot, onClick);
        }
    }
    public void addItem(int slot, ItemStack item, BiConsumer<InventoryClickEvent, ClickType> onClick) {
        // Setze das Item direkt in das Inventar
        inventory.setItem(slot, item);

        // Registriere die Klick-Aktion
        if (onClick != null) {
            clickActions.put(slot, onClick);
        }
    }

    public void addDroppableSlot(int slot, Material material, String name, String[] lore, BiConsumer<Player, ItemStack> onDrop) {
        // Platziert das Platzhalter-Item (z.B. "Leerer Slot")
        addItem(slot, material, name, lore, null); // Kein Klick-Event für den Platzhalter
        // Registriert die Drop-Aktion für diesen Slot
        if (onDrop != null) {
            dropActions.put(slot, onDrop);
        }
    }


    public void fillEmptySlots(Material material, String name) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            filler.setItemMeta(meta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, filler);
            }
        }
    }

    public void fillEmptySlotsWithPattern(Material material, String name, String patternType) {
        GUIPatternFiller.fillPattern(inventory, material, name, patternType);
    }

    public ItemStack createCustomSkull(String textureUrl) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

        if (skullMeta != null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "CustomHead");
            profile.getProperties().put("textures", new Property("textures", textureUrl));

            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            skullItem.setItemMeta(skullMeta);
        }

        return skullItem;
    }

    public void open(org.bukkit.entity.Player player) {
        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        // Wenn außerhalb eines Inventars geklickt wird, ignorieren
        if (clickedInventory == null) return;

        // Ist der Klick im oberen Inventar (unserem GUI)?
        if (clickedInventory.equals(inventory)) {
            int slot = event.getSlot();
            ItemStack cursorItem = event.getCursor();

            // Fall 1: Der Slot ist eine Drop-Zone und der Spieler will ein Item platzieren
            if (dropActions.containsKey(slot) && cursorItem != null && cursorItem.getType() != Material.AIR) {
                // Event NICHT abbrechen, damit das Item platziert werden kann.
                // Die Logik wird vom Consumer in der GemstoneMenu-Klasse übernommen.
                dropActions.get(slot).accept(player, cursorItem);
                return; // Wichtig: Hier aufhören, damit nicht abgebrochen wird.
            }

            // Fall 2: Der Slot ist ein normaler Button
            if (clickActions.containsKey(slot)) {
                event.setCancelled(true); // Verhindert, dass das Item genommen wird
                clickActions.get(slot).accept(event, event.getClick());
                return;
            }

            // Fall 3: Jeder andere Klick im GUI (z.B. auf einen Filler) wird abgebrochen
            event.setCancelled(true);
        }
        // Ist der Klick im unteren Inventar (Spieler-Inventar)?
        else {
            // Verhindere Shift-Klicks, die Items in unser GUI verschieben würden
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
        }
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getNextEmptySlot() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

    public int getNextEmptySlot(int startIndex) {
        for (int i = startIndex; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }
}


package org.example.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIPatternFiller {

    public static void fillPattern(Inventory inventory, Material material, String name, String patternType) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            filler.setItemMeta(meta);
        }

        switch (patternType.toLowerCase()) {
            case "checkerboard":
                fillCheckerboardPattern(inventory, filler);
                break;
            case "diagonal":
                fillDiagonalPattern(inventory, filler);
                break;
            case "border":
                fillBorderPattern(inventory, filler);
                break;
            case "all":
                fillAllSlots(inventory, filler);
                break;
            default:
                System.out.println("Unknown pattern type.");
                break;
        }
    }

    private static void fillCheckerboardPattern(Inventory inventory, ItemStack filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % 2 == 0 && (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)) {
                inventory.setItem(i, filler);
            }
        }
    }

    private static void fillDiagonalPattern(Inventory inventory, ItemStack filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            int row = i / 9;
            int column = i % 9;
            if (row == column && (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)) {
                inventory.setItem(i, filler);
            }
        }
    }

    private static void fillBorderPattern(Inventory inventory, ItemStack filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i >= inventory.getSize() - 9 || i % 9 == 0 || (i + 1) % 9 == 0) {
                if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                    inventory.setItem(i, filler);
                }
            }
        }
    }

    private static void fillAllSlots(Inventory inventory, ItemStack filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, filler);
            }
        }
    }
}
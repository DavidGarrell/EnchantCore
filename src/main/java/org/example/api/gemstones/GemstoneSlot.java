package org.example.api.gemstones;

import org.bukkit.entity.Player;

public class GemstoneSlot {
    private int id;

    private Gemstone gemstone;

    public GemstoneSlot(int id, Gemstone gemstone) {
        this.id = id;
        this.gemstone = gemstone;
    }

    public int getId() {
        return id;
    }


    public Gemstone getGemstone() {
        return gemstone;
    }

    public void addGemstone(Gemstone gemstone) {
        if (this.gemstone == null) {
            this.gemstone = gemstone;
        }
    }
    public void setGemstone(Gemstone gemstone) {
        this.gemstone = gemstone;
    }
    public void removeGemstone() {
        this.gemstone = null;
    }

    @Override
    public String toString() {
        return "GemstoneSlot{" +
                "id='" + id + '\'' +
                ", gemstone=" + gemstone +
                '}';
    }
}

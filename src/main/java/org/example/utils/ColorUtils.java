package org.example.utils;
import org.bukkit.ChatColor;
import java.awt.Color;

/**
 * Eine Hilfsklasse zur Erstellung von Farbverläufen (Gradients)
 * für Text in Minecraft unter Verwendung von Hex-Farbcodes (1.16+).
 */
public class ColorUtils {

    /**
     * Wendet einen Farbverlauf auf einen Text an, der von einer Start- zu einer Endfarbe übergeht.
     *
     * @param text Der Text, der eingefärbt werden soll.
     * @param start Die Startfarbe.
     * @param end Die Endfarbe.
     * @param formatCodes Alle zusätzlichen Formatierungen wie ChatColor.BOLD.
     * @return Der formatierte String mit Hex-Farbcodes.
     */
    public static String applyGradient(String text, Color start, Color end, ChatColor... formatCodes) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder gradientText = new StringBuilder();
        int length = text.length();

        // Füge die Formatierungscodes am Anfang hinzu, falls vorhanden
        for (ChatColor format : formatCodes) {
            gradientText.append(format.toString());
        }

        for (int i = 0; i < length; i++) {
            // Berechne den Fortschritt im Verlauf (von 0.0 bis 1.0)
            double progress = (double) i / (length - 1);
            if (length == 1) {
                progress = 0.5; // Zentriere die Farbe für einen einzelnen Buchstaben
            }

            // Interpoliere die RGB-Werte zwischen der Start- und Endfarbe
            int red = (int) (start.getRed() * (1 - progress) + end.getRed() * progress);
            int green = (int) (start.getGreen() * (1 - progress) + end.getGreen() * progress);
            int blue = (int) (start.getBlue() * (1 - progress) + end.getBlue() * progress);

            // Konvertiere die RGB-Farbe in einen Minecraft-Hex-Farbcode
            String hexCode = String.format("&#%02x%02x%02x", red, green, blue);

            // Füge den Farbcode und den Buchstaben zum Ergebnis hinzu
            gradientText.append(hexCode).append(text.charAt(i));
        }

        return gradientText.toString();
    }

}
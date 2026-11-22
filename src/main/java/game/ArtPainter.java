package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Faerbt die ASCII-Bilder ein. Die Bilder selbst bleiben reines ASCII (src/main/resources/ascii/),
 * die Farbe wird hier pro Zeichen nach Bild, Zeile und Spalte bestimmt. Ist {@link Ansi} aus,
 * kommen die Zeilen unveraendert zurueck.
 */
public final class ArtPainter {

    private static final int[] STONE = {150, 145, 165};
    private static final int[] DARK_STONE = {95, 90, 108};
    private static final int[] GOLD = {255, 211, 77};
    private static final int[] WOOD = {160, 105, 55};
    private static final int[] FLAME = {255, 140, 40};
    private static final int[] SKIN = {230, 195, 165};
    private static final int[] MAGE_HAT = {110, 150, 255};
    private static final int[] MAGE_ROBE = {70, 105, 200};
    private static final int[] CLOAK = {110, 125, 150};
    private static final int[] STEEL = {200, 205, 215};
    private static final int[] BRASS = {190, 150, 70};
    private static final int[] WATER = {70, 120, 200};
    private static final int[] BONE = {225, 220, 200};
    private static final int[] POISON = {140, 200, 70};
    private static final int[] CRIMSON = {170, 50, 60};
    private static final int[] BOOK = {170, 100, 80};

    private ArtPainter() {
    }

    /** Gibt die Zeilen des Bildes mit Farbe zurueck (Name wie die Datei in ascii/, ohne .txt). */
    public static List<String> paint(String name, List<String> lines) {
        if (!Ansi.isEnabled()) {
            return lines;
        }
        int width = lines.stream().mapToInt(String::length).max().orElse(0);
        List<String> result = new ArrayList<>();
        for (int row = 0; row < lines.size(); row++) {
            result.add(paintLine(name, lines.get(row), row, lines.size(), width));
        }
        return result;
    }

    /** Fasst gleichfarbige Zeichen in einem Farbcode zusammen. */
    private static String paintLine(String name, String line, int row, int rows, int width) {
        StringBuilder out = new StringBuilder();
        StringBuilder run = new StringBuilder();
        int[] runColor = null;
        for (int col = 0; col < line.length(); col++) {
            char ch = line.charAt(col);
            int[] color = ch == ' ' ? null : colorOf(name, ch, row, col, rows, width);
            if (ch == ' ') {
                // Leerzeichen brauchen keine Farbe, sie setzen den Lauf fort
                run.append(ch);
                continue;
            }
            if (runColor != null && color != runColor) {
                flush(out, run, runColor);
                runColor = null;
            }
            runColor = color;
            run.append(ch);
        }
        flush(out, run, runColor);
        return out.toString();
    }

    private static void flush(StringBuilder out, StringBuilder run, int[] color) {
        if (run.isEmpty()) {
            return;
        }
        String text = run.toString();
        out.append(color == null ? text : Ansi.rgb(color[0], color[1], color[2], text));
        run.setLength(0);
    }

    private static int[] colorOf(String name, char ch, int row, int col, int rows, int width) {
        return switch (name) {
            case "title" -> Character.isLetter(ch) ? GOLD : STONE;
            case "mage" -> mageColor(ch, row, col);
            case "rogue" -> rogueColor(row, col);
            case "gate" -> gateColor(ch, row, col, rows, width);
            default -> roomColor(name, ch);
        };
    }

    /** Gemeinsame Zeichenfarben fuer die Raumbilder: ~ Wasser, * Flamme, @ Gold, o O Knochen usw. */
    private static int[] roomColor(String name, char ch) {
        if (name.equals("library") && (ch == '[' || ch == ']')) {
            return BOOK;
        }
        return switch (ch) {
            case '~' -> WATER;
            case '*' -> FLAME;
            case '@' -> GOLD;
            case 'o', 'O' -> BONE;
            case '#' -> WOOD;
            case '%' -> POISON;
            case '=', 'T', '^' -> STEEL;
            case '&' -> CRIMSON;
            case '.', ',', ':', ';', 'v' -> DARK_STONE;
            default -> STONE;
        };
    }

    private static int[] mageColor(char ch, int row, int col) {
        if (ch == '*') {
            return GOLD;
        }
        if (col >= 13) {
            return WOOD;
        }
        if (row <= 2) {
            return MAGE_HAT;
        }
        return row == 3 ? SKIN : MAGE_ROBE;
    }

    private static int[] rogueColor(int row, int col) {
        if (row == 2 && col >= 8 && col <= 10) {
            return SKIN;
        }
        if (row == 4 && col >= 16) {
            return STEEL;
        }
        if (row == 4 && col >= 12) {
            return BRASS;
        }
        return CLOAK;
    }

    private static int[] gateColor(char ch, int row, int col, int rows, int width) {
        boolean torchArea = col < 10 || col >= width - 10;
        if (row <= 2 && torchArea) {
            return FLAME;
        }
        if (ch == '#') {
            return WOOD;
        }
        if (ch == '@') {
            return GOLD;
        }
        return row == rows - 1 ? DARK_STONE : STONE;
    }
}

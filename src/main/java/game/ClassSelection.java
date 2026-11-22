package game;

import character.CharacterClass;
import character.Player;
import character.PlayerName;
import java.util.ArrayList;
import java.util.List;
import story.AsciiArt;

public class ClassSelection {

    private static final int CARD_WIDTH = 38;
    private static final int COLUMN_GAP = 4;
    private static final int SCREEN_WIDTH = CARD_WIDTH * 2 + COLUMN_GAP;
    private static final String SCREEN_LINE = "=".repeat(SCREEN_WIDTH);
    private static final String CARD_LINE = "=".repeat(CARD_WIDTH);
    private static final int MAX_ATTRIBUTE_BAR = 5;

    private final ConsoleIO io;

    public ClassSelection(ConsoleIO io) {
        this.io = io;
    }

    /** Klasse waehlen, bestaetigen, Namen eingeben. Gibt den fertigen Spieler zurueck. */
    public Player run() {
        while (true) {
            CharacterClass chosen = chooseClass();
            if (confirm(chosen)) {
                return new Player(askName(), chosen);
            }
        }
    }

    private CharacterClass chooseClass() {
        CharacterClass[] classes = CharacterClass.values();
        io.clearScreen();
        io.println(Ansi.gold(SCREEN_LINE));
        io.println(Ansi.gold(center("WAEHLE DEINE KLASSE", SCREEN_WIDTH)));
        io.println(Ansi.gold(SCREEN_LINE));

        List<List<String>> columns = new ArrayList<>();
        for (CharacterClass characterClass : classes) {
            columns.add(cardLines(characterClass));
        }
        // Alle Karten gleich hoch machen, damit die Auswahlzeile in beiden Spalten auf einer Hoehe steht
        int height = columns.stream().mapToInt(List::size).max().orElse(0);
        for (int i = 0; i < columns.size(); i++) {
            List<String> card = columns.get(i);
            while (card.size() < height) {
                card.add("");
            }
            card.add("");
            card.add(Ansi.number(i + 1) + " " + classes[i].getDisplayName());
        }
        printSideBySide(columns);

        io.println();
        int choice = io.askChoice("Deine Wahl: ", 1, classes.length);
        return classes[choice - 1];
    }

    private boolean confirm(CharacterClass chosen) {
        io.clearScreen();
        for (String line : cardLines(chosen)) {
            io.println(line);
        }
        io.println();
        io.println("Bist du sicher? Du waehlst: " + chosen.getDisplayName());
        io.println(Ansi.number(1) + " Ja");
        io.println(Ansi.number(2) + " Nein, zurueck zur Auswahl");
        io.println();
        return io.askChoice("Deine Wahl: ", 1, 2) == 1;
    }

    private String askName() {
        io.clearScreen();
        io.println(Ansi.gold(SCREEN_LINE));
        io.println(Ansi.gold(center("WIE HEISST DEIN CHARAKTER?", SCREEN_WIDTH)));
        io.println(Ansi.gold(SCREEN_LINE));
        io.println("1 bis " + PlayerName.MAX_LENGTH + " Zeichen, nur Buchstaben und Zahlen, keine Leerzeichen.");
        io.println();
        while (true) {
            String name = io.readLine("Name: ");
            if (PlayerName.isValid(name)) {
                return name;
            }
            io.println("Ungueltiger Name. Erlaubt sind 1 bis " + PlayerName.MAX_LENGTH
                    + " Buchstaben oder Zahlen ohne Leerzeichen und Sonderzeichen.");
        }
    }

    /** Eine Karte als Liste von Zeilen, jede hoechstens CARD_WIDTH Zeichen breit. */
    private List<String> cardLines(CharacterClass characterClass) {
        List<String> lines = new ArrayList<>();
        lines.add(Ansi.gold(CARD_LINE));
        lines.add(Ansi.gold(characterClass.getDisplayName().toUpperCase()));
        lines.add(Ansi.gold(CARD_LINE));
        lines.addAll(ArtPainter.paint(characterClass.getArtName(), AsciiArt.load(characterClass.getArtName())));
        lines.add("");
        lines.add(attributeLine("Staerke", characterClass.getStrength()));
        lines.add(attributeLine("Geschick", characterClass.getDexterity()));
        lines.add(attributeLine("Intelligenz", characterClass.getIntelligence()));
        lines.add(String.format("%-13s", "Lebenspunkte") + Ansi.red(String.valueOf(characterClass.getMaxHp())));
        lines.add(String.format("%-13s", "Ruestungskl.") + Ansi.cyan(String.valueOf(characterClass.getArmorClass())));
        lines.add(Ansi.grey("Faehigkeiten:"));
        lines.addAll(wrap(characterClass.getDescription(), CARD_WIDTH, "  "));
        return lines;
    }

    private String attributeLine(String label, int value) {
        int filled = Math.min(value, MAX_ATTRIBUTE_BAR);
        String bar = Ansi.gold("#".repeat(filled)) + Ansi.grey(".".repeat(MAX_ATTRIBUTE_BAR - filled));
        return String.format("%-13s", label) + bar + " " + value;
    }

    private void printSideBySide(List<List<String>> columns) {
        int height = columns.stream().mapToInt(List::size).max().orElse(0);
        for (int row = 0; row < height; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < columns.size(); col++) {
                List<String> column = columns.get(col);
                String text = row < column.size() ? column.get(row) : "";
                if (col > 0) {
                    line.append(" ".repeat(COLUMN_GAP));
                }
                line.append(Ansi.padRight(text, CARD_WIDTH));
            }
            io.println(line.toString().stripTrailing());
        }
    }

    private List<String> wrap(String text, int width, String indent) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder(indent);
        for (String word : text.split(" ")) {
            boolean lineHasText = current.length() > indent.length();
            if (lineHasText && current.length() + 1 + word.length() > width) {
                lines.add(current.toString());
                current = new StringBuilder(indent);
                lineHasText = false;
            }
            if (lineHasText) {
                current.append(' ');
            }
            current.append(word);
        }
        lines.add(current.toString());
        return lines;
    }

    private String center(String text, int width) {
        return " ".repeat(Math.max(0, (width - text.length()) / 2)) + text;
    }
}

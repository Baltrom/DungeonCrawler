package game;

import character.CharacterClass;
import character.Player;
import character.PlayerName;
import story.AsciiArt;

public class ClassSelection {

    private static final String LINE = "==================================================";
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
        io.println(LINE);
        io.println("              WAEHLE DEINE KLASSE");
        io.println(LINE);
        for (CharacterClass characterClass : classes) {
            printCard(characterClass);
        }
        for (int i = 0; i < classes.length; i++) {
            io.println("[" + (i + 1) + "] " + classes[i].getDisplayName());
        }
        io.println();
        int choice = io.askChoice("Deine Wahl: ", 1, classes.length);
        return classes[choice - 1];
    }

    private boolean confirm(CharacterClass chosen) {
        io.clearScreen();
        io.println(LINE);
        printCard(chosen);
        io.println("Bist du sicher? Du waehlst: " + chosen.getDisplayName());
        io.println("[1] Ja");
        io.println("[2] Nein, zurueck zur Auswahl");
        io.println();
        return io.askChoice("Deine Wahl: ", 1, 2) == 1;
    }

    private String askName() {
        io.clearScreen();
        io.println(LINE);
        io.println("            WIE HEISST DEIN CHARAKTER?");
        io.println(LINE);
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

    private void printCard(CharacterClass characterClass) {
        io.println(LINE);
        io.println(characterClass.getDisplayName().toUpperCase());
        io.println(LINE);
        for (String line : AsciiArt.load(characterClass.getArtName())) {
            io.println(line);
        }
        io.println();
        io.println(attributeLine("Staerke", characterClass.getStrength()));
        io.println(attributeLine("Geschick", characterClass.getDexterity()));
        io.println(attributeLine("Intelligenz", characterClass.getIntelligence()));
        io.println(String.format("%-13s%d", "Lebenspunkte", characterClass.getMaxHp()));
        io.println(String.format("%-13s%d", "Ruestungskl.", characterClass.getArmorClass()));
        io.println("Faehigkeiten: " + characterClass.getDescription());
        io.println();
    }

    private String attributeLine(String label, int value) {
        int filled = Math.min(value, MAX_ATTRIBUTE_BAR);
        String bar = "#".repeat(filled) + ".".repeat(MAX_ATTRIBUTE_BAR - filled);
        return String.format("%-13s%s %d", label, bar, value);
    }
}

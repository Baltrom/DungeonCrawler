package game;

import character.Player;

public class Main {

    public static final String TITLE = "DUNGEON CRAWLER";

    public static void main(String[] args) {
        ConsoleIO io = new ConsoleIO();
        io.println("==============================");
        io.println("   " + TITLE);
        io.println("==============================");

        Player player = new ClassSelection(io).run();

        io.clearScreen();
        io.println("Willkommen, " + player.getName() + " der " + player.getCharacterClass().getDisplayName() + "!");
        io.println("Das Abenteuer ist noch nicht gebaut.");
    }
}

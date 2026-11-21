package game;

import character.Player;
import story.AsciiArt;

public class MainMenu {

    private static final int NEW_GAME = 1;
    private static final int LOAD_GAME = 2;
    private static final int HIGHSCORES = 3;
    private static final int QUIT = 4;

    private final ConsoleIO io;

    public MainMenu(ConsoleIO io) {
        this.io = io;
    }

    public void run() {
        boolean running = true;
        while (running) {
            show();
            int choice = io.askChoice("Deine Wahl: ", NEW_GAME, QUIT);
            switch (choice) {
                case NEW_GAME -> startNewGame();
                case LOAD_GAME -> notAvailableYet("Spiel laden");
                case HIGHSCORES -> notAvailableYet("Rangliste");
                case QUIT -> running = false;
                default -> throw new IllegalStateException("Unexpected menu choice: " + choice);
            }
        }
        io.println();
        io.println("Auf Wiedersehen!");
    }

    private void show() {
        io.clearScreen();
        for (String line : AsciiArt.load("title")) {
            io.println(line);
        }
        io.println();
        io.println("[" + NEW_GAME + "] Neues Spiel");
        io.println("[" + LOAD_GAME + "] Spiel laden");
        io.println("[" + HIGHSCORES + "] Rangliste anzeigen");
        io.println("[" + QUIT + "] Beenden");
        io.println();
    }

    private void startNewGame() {
        Player player = new ClassSelection(io).run();
        io.clearScreen();
        io.println("Willkommen, " + player.getName() + " der " + player.getCharacterClass().getDisplayName() + "!");
        io.println("Das Abenteuer ist noch nicht gebaut.");
        io.println();
        io.readLine("Weiter mit Enter...");
    }

    private void notAvailableYet(String feature) {
        io.println();
        io.println(feature + " ist noch nicht verfuegbar.");
        io.println();
        io.readLine("Weiter mit Enter...");
    }
}

package game;

import character.Player;
import dice.Dice;
import dungeon.DungeonGenerator;
import java.util.Random;
import story.AsciiArt;
import story.StoryText;

public class MainMenu {

    private static final int NEW_GAME = 1;
    private static final int LOAD_GAME = 2;
    private static final int HIGHSCORES = 3;
    private static final int QUIT = 4;

    private final ConsoleIO io;
    private final GameLauncher launcher;

    public MainMenu(ConsoleIO io) {
        this(io, player -> new Game(io, new Dice(), player,
                DungeonGenerator.generate(new Random().nextLong())).run());
    }

    public MainMenu(ConsoleIO io, GameLauncher launcher) {
        this.io = io;
        this.launcher = launcher;
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
        GameResult result = launcher.play(player);
        showResult(player, result);
    }

    private void showResult(Player player, GameResult result) {
        io.clearScreen();
        io.println("==================================================");
        switch (result.outcome()) {
            case VICTORY -> {
                io.println("SIEG");
                StoryText.load("victory").forEach(io::println);
            }
            case DEATH -> {
                io.println("DU BIST GESTORBEN");
                StoryText.load("death").forEach(io::println);
            }
            case SAVED -> io.println("SPIEL GESPEICHERT");
        }
        io.println("==================================================");
        io.println(player.getName() + " der " + player.getCharacterClass().getDisplayName()
                + " erreichte Raum " + result.roomsReached() + ".");
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

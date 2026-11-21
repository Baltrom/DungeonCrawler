package game;

import character.Player;
import dice.Dice;
import dungeon.DungeonGenerator;
import highscore.HighscoreEntry;
import highscore.Highscores;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import story.AsciiArt;
import story.StoryText;

public class MainMenu {

    private static final int NEW_GAME = 1;
    private static final int LOAD_GAME = 2;
    private static final int HIGHSCORES = 3;
    private static final int QUIT = 4;

    private static final String LINE = "==================================================";

    private final ConsoleIO io;
    private final GameLauncher launcher;
    private final Highscores highscores;

    public MainMenu(ConsoleIO io) {
        this(io, player -> new Game(io, new Dice(), player,
                DungeonGenerator.generate(new Random().nextLong())).run(),
                new Highscores(Path.of("highscores.txt")));
    }

    public MainMenu(ConsoleIO io, GameLauncher launcher, Highscores highscores) {
        this.io = io;
        this.launcher = launcher;
        this.highscores = highscores;
    }

    public void run() {
        boolean running = true;
        while (running) {
            show();
            int choice = io.askChoice("Deine Wahl: ", NEW_GAME, QUIT);
            switch (choice) {
                case NEW_GAME -> startNewGame();
                case LOAD_GAME -> notAvailableYet("Spiel laden");
                case HIGHSCORES -> showHighscores();
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

    /** Zeigt das Ende des Spiels. Nach Tod oder Sieg kommt der Eintrag in die Rangliste. */
    private void showResult(Player player, GameResult result) {
        io.clearScreen();
        io.println(LINE);
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
        io.println(LINE);
        io.println(player.getName() + " der " + player.getCharacterClass().getDisplayName()
                + " erreichte Raum " + result.roomsReached() + ".");

        if (result.outcome() != GameOutcome.SAVED) {
            Highscores.AddResult added = highscores.add(
                    new HighscoreEntry(player.getName(), player.getCharacterClass(), result.roomsReached()));
            io.println(added.rank() > 0
                    ? "Platz " + added.rank() + " in der Rangliste!"
                    : "Das reicht nicht fuer die Top " + Highscores.MAX_ENTRIES + ".");
        }
        io.println();
        io.readLine("Weiter mit Enter...");
    }

    private void showHighscores() {
        List<HighscoreEntry> entries = highscores.load();
        io.clearScreen();
        io.println(LINE);
        io.println("RANGLISTE - Top " + Highscores.MAX_ENTRIES);
        io.println(LINE);
        if (entries.isEmpty()) {
            io.println("Noch keine Eintraege. Wer schafft es am weitesten in die Gruft?");
        }
        for (int i = 0; i < entries.size(); i++) {
            io.println(String.format("%2d. %s", i + 1, entries.get(i).describe()));
        }
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

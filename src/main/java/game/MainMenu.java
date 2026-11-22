package game;

import character.CharacterClass;
import character.Player;
import highscore.HighscoreEntry;
import highscore.Highscores;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import save.SaveGame;
import save.SaveManager;
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
    private final SaveManager saveManager;

    public MainMenu(ConsoleIO io) {
        this(io, new SaveManager(Path.of("savegame.txt")), new Highscores(Path.of("highscores.txt")));
    }

    private MainMenu(ConsoleIO io, SaveManager saveManager, Highscores highscores) {
        this(io, new DefaultGameLauncher(io, saveManager), highscores, saveManager);
    }

    public MainMenu(ConsoleIO io, GameLauncher launcher, Highscores highscores, SaveManager saveManager) {
        this.io = io;
        this.launcher = launcher;
        this.highscores = highscores;
        this.saveManager = saveManager;
    }

    public void run() {
        boolean running = true;
        while (running) {
            show();
            int choice = io.askChoice("Deine Wahl: ", NEW_GAME, QUIT);
            switch (choice) {
                case NEW_GAME -> startNewGame();
                case LOAD_GAME -> loadGame();
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
        for (String line : ArtPainter.paint("title", AsciiArt.load("title"))) {
            io.println(line);
        }
        io.println();
        io.println(Ansi.number(NEW_GAME) + " Neues Spiel");
        io.println(Ansi.number(LOAD_GAME) + " Spiel laden");
        io.println(Ansi.number(HIGHSCORES) + " Rangliste anzeigen");
        io.println(Ansi.number(QUIT) + " Beenden");
        io.println();
    }

    private void startNewGame() {
        Player player = new ClassSelection(io).run();
        GameResult result = launcher.play(player);
        finishGame(player.getName(), player.getCharacterClass(), result);
    }

    /** Laedt den Spielstand. Der alte Spielstand wird dabei geloescht, damit es kein Savescumming gibt. */
    private void loadGame() {
        if (!saveManager.exists()) {
            message("Es gibt keinen gespeicherten Spielstand.");
            return;
        }
        Optional<SaveGame> save = saveManager.load();
        if (save.isEmpty()) {
            message("Der Spielstand ist beschaedigt und kann nicht geladen werden.");
            return;
        }
        saveManager.delete();
        SaveGame loaded = save.get();
        GameResult result = launcher.resume(loaded);
        finishGame(loaded.name(), loaded.characterClass(), result);
    }

    /**
     * Zeigt das Ende des Spiels. Nach Tod oder Sieg kommt zuerst der Eintrag in die Rangliste,
     * danach wird der Spielstand geloescht.
     */
    private void finishGame(String name, CharacterClass characterClass, GameResult result) {
        io.clearScreen();
        io.println(Ansi.gold(LINE));
        switch (result.outcome()) {
            case VICTORY -> {
                io.println(Ansi.green("SIEG"));
                StoryText.load("victory").forEach(io::println);
            }
            case DEATH -> {
                io.println(Ansi.red("DU BIST GESTORBEN"));
                StoryText.load("death").forEach(io::println);
            }
            case SAVED -> io.println("SPIEL GESPEICHERT");
        }
        io.println(Ansi.gold(LINE));
        io.println(name + " der " + characterClass.getDisplayName() + " erreichte Raum "
                + result.roomsReached() + ".");

        if (result.outcome() != GameOutcome.SAVED) {
            Highscores.AddResult added = highscores.add(
                    new HighscoreEntry(name, characterClass, result.roomsReached()));
            io.println(added.rank() > 0
                    ? "Platz " + added.rank() + " in der Rangliste!"
                    : "Das reicht nicht fuer die Top " + Highscores.MAX_ENTRIES + ".");
            saveManager.delete();
        }
        io.println();
        io.readLine("Weiter mit Enter...");
    }

    private void showHighscores() {
        List<HighscoreEntry> entries = highscores.load();
        io.clearScreen();
        io.println(Ansi.gold(LINE));
        io.println(Ansi.gold("RANGLISTE - Top " + Highscores.MAX_ENTRIES));
        io.println(Ansi.gold(LINE));
        if (entries.isEmpty()) {
            io.println("Noch keine Eintraege. Wer schafft es am weitesten in die Gruft?");
        }
        for (int i = 0; i < entries.size(); i++) {
            io.println(String.format("%2d. %s", i + 1, entries.get(i).describe()));
        }
        io.println();
        io.readLine("Weiter mit Enter...");
    }

    private void message(String text) {
        io.println();
        io.println(text);
        io.println();
        io.readLine("Weiter mit Enter...");
    }
}

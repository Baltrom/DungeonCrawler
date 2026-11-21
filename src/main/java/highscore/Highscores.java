package highscore;

import character.CharacterClass;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Top 10 nach erreichten Raeumen, gespeichert in einer Textdatei (eine Zeile pro Eintrag:
 * Name;KLASSE;Raeume). Bei Gleichstand steht der fruehere Eintrag hoeher.
 */
public class Highscores {

    public static final int MAX_ENTRIES = 10;

    /** Ergebnis von add: die neue Liste und der Platz des neuen Eintrags (1-10) oder -1, wenn er nicht reicht. */
    public record AddResult(List<HighscoreEntry> entries, int rank) {
    }

    private final Path file;

    public Highscores(Path file) {
        this.file = file;
    }

    public List<HighscoreEntry> load() {
        if (!Files.exists(file)) {
            return List.of();
        }
        List<HighscoreEntry> entries = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                HighscoreEntry entry = parse(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return List.copyOf(entries);
    }

    public AddResult add(HighscoreEntry newEntry) {
        List<HighscoreEntry> entries = new ArrayList<>(load());
        // Hinter allen Eintraegen mit gleich vielen oder mehr Raeumen einsortieren: wer zuerst da war, steht hoeher
        int position = 0;
        while (position < entries.size() && entries.get(position).rooms() >= newEntry.rooms()) {
            position++;
        }
        entries.add(position, newEntry);
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        save(entries);
        return new AddResult(List.copyOf(entries), position < MAX_ENTRIES ? position + 1 : -1);
    }

    private void save(List<HighscoreEntry> entries) {
        List<String> lines = new ArrayList<>();
        for (HighscoreEntry entry : entries) {
            lines.add(entry.name() + ";" + entry.characterClass().name() + ";" + entry.rooms());
        }
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Liest eine Zeile. Kaputte Zeilen werden ignoriert, damit eine beschaedigte Datei das Spiel nicht stoppt. */
    private static HighscoreEntry parse(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new HighscoreEntry(parts[0], CharacterClass.valueOf(parts[1]), Integer.parseInt(parts[2].trim()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

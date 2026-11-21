package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConsoleIO {

    // Die IntelliJ-Konsole laesst sich nicht leeren, deshalb Leerzeilen
    private static final int CLEAR_LINES = 50;

    private final BufferedReader reader;
    private final PrintStream out;
    private final List<String> recentResults = new ArrayList<>();

    public ConsoleIO() {
        this(System.in, System.out);
    }

    public ConsoleIO(InputStream in, PrintStream out) {
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.out = out;
    }

    /**
     * Leert den Bildschirm mit Leerzeilen. Danach stehen als erster Textblock die Ergebnisse,
     * die seit dem letzten Leeren mit report() gemeldet wurden, damit man sieht, was passiert ist.
     */
    public void clearScreen() {
        clearScreenOnly();
        showRecentResults();
    }

    /** Leert nur den Bildschirm. Die Ergebnisse zeigt der Aufrufer spaeter selbst mit showRecentResults(). */
    public void clearScreenOnly() {
        for (int i = 0; i < CLEAR_LINES; i++) {
            out.println();
        }
    }

    /** Gibt den Block "Was zuletzt geschah" aus (falls es Ergebnisse gibt) und vergisst sie dann. */
    public void showRecentResults() {
        if (!recentResults.isEmpty()) {
            out.println("--- Was zuletzt geschah ---");
            for (String line : recentResults) {
                out.println(line);
            }
            out.println("---------------------------");
            out.println();
            recentResults.clear();
        }
    }

    /**
     * Meldet ein Ergebnis (Wurf, Treffer, Fund, ...). Es wird sofort ausgegeben und nach dem naechsten
     * clearScreen() noch einmal ganz oben gezeigt.
     */
    public void report(String text) {
        out.println(text);
        recentResults.add(text);
    }

    public void println() {
        out.println();
    }

    public void println(String text) {
        out.println(text);
    }

    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new IllegalStateException("Input ended");
            }
            return line.trim();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Fragt so lange, bis eine Zahl zwischen min und max (inklusive) eingegeben wurde. */
    public int askChoice(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // faellt unten in die Fehlermeldung
            }
            println("Bitte eine Zahl von " + min + " bis " + max + " eingeben.");
        }
    }
}

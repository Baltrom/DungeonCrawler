package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class ConsoleIO {

    // Die IntelliJ-Konsole laesst sich nicht leeren, deshalb Leerzeilen
    private static final int CLEAR_LINES = 50;

    private final BufferedReader reader;
    private final PrintStream out;

    public ConsoleIO() {
        this(System.in, System.out);
    }

    public ConsoleIO(InputStream in, PrintStream out) {
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.out = out;
    }

    public void clearScreen() {
        for (int i = 0; i < CLEAR_LINES; i++) {
            out.println();
        }
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

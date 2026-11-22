package game;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {

    /**
     * Startet das Spiel. Die Ausgabe laeuft in UTF-8 mit ANSI-Farben (gedacht fuer das Terminal-Tab in
     * IntelliJ oder Windows Terminal). Mit dem Argument --plain gibt es keine Farben.
     */
    public static void main(String[] args) {
        boolean plain = Arrays.asList(args).contains("--plain");
        Ansi.setEnabled(!plain);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        new MainMenu(new ConsoleIO(System.in, out)).run();
    }
}

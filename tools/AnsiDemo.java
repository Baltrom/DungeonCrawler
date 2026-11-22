import game.Ansi;
import game.ArtPainter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import story.AsciiArt;

/**
 * Bilder-Ansicht: zeigt die farbigen ASCII-Bilder des Spiels nacheinander, mit Enter geht es weiter.
 * Benutzt dieselbe Einfaerbung wie das Spiel (game.ArtPainter), nur zum Ansehen.
 *
 * Vorher in IntelliJ "Build Project", dann im Terminal-Tab:
 *   java -cp target/classes tools/AnsiDemo.java
 */
public class AnsiDemo {

    // Namen der Dateien in src/main/resources/ascii/ (ohne .txt)
    private static final List<String> IMAGES = List.of("title", "mage", "rogue",
            "gate", "library", "crypt", "statue_hall", "trap_corridor", "alchemy_lab",
            "prison", "cistern", "armory", "throne");

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        Ansi.setEnabled(true);

        for (int i = 0; i < IMAGES.size(); i++) {
            String name = IMAGES.get(i);
            for (int k = 0; k < 40; k++) {
                out.println();
            }
            out.println(Ansi.grey("Bild " + (i + 1) + " von " + IMAGES.size() + ": " + name));
            out.println();
            for (String line : ArtPainter.paint(name, AsciiArt.load(name))) {
                out.println(line);
            }
            out.println();
            boolean last = i == IMAGES.size() - 1;
            out.print(Ansi.grey(last ? "Enter zum Beenden..." : "Enter fuer das naechste Bild..."));
            out.flush();
            if (in.readLine() == null) {
                return;
            }
        }
    }
}

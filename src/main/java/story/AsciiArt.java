package story;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Laedt ASCII-Bilder aus src/main/resources/ascii/, damit Backslashes nicht im Code maskiert werden muessen. */
public final class AsciiArt {

    private AsciiArt() {
    }

    public static List<String> load(String name) {
        String path = "/ascii/" + name + ".txt";
        try (InputStream in = AsciiArt.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("ASCII art not found: " + path);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return reader.lines().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

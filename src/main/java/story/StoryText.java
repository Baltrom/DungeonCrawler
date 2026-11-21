package story;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Laedt Story-Texte aus src/main/resources/story/, damit Texte und Spiellogik getrennt bleiben. */
public final class StoryText {

    private StoryText() {
    }

    public static List<String> load(String name) {
        String path = "/story/" + name + ".txt";
        try (InputStream in = StoryText.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Story text not found: " + path);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return reader.lines().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

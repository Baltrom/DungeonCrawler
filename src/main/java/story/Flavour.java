package story;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Random;

/**
 * Stimmungstexte aus src/main/resources/story/flavour.properties. Zu jedem Schluessel gibt es eine oder
 * mehrere Varianten (key.1, key.2, ...), eine davon wird zufaellig gewaehlt. Der Zufall hier ist getrennt
 * von den Wuerfen, damit Texte keine Wuerfe verbrauchen.
 */
public class Flavour {

    private static final Properties TEXTS = load();

    private final Random random;

    public Flavour() {
        this(new Random());
    }

    public Flavour(Random random) {
        this.random = random;
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Flavour.class.getResourceAsStream("/story/flavour.properties")) {
            if (in == null) {
                throw new IllegalStateException("flavour.properties not found");
            }
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }

    public boolean has(String key) {
        return TEXTS.containsKey(key + ".1");
    }

    /** Waehlt eine Variante, z. B. pick("combat.hit.mage"). */
    public String pick(String key) {
        int variants = 0;
        while (TEXTS.containsKey(key + "." + (variants + 1))) {
            variants++;
        }
        if (variants == 0) {
            throw new IllegalArgumentException("No flavour text for: " + key);
        }
        return TEXTS.getProperty(key + "." + (1 + random.nextInt(variants)));
    }

    /** Wie pick, ersetzt zusaetzlich Platzhalter wie {enemy} durch Werte (Paare aus Platzhalter und Wert). */
    public String pick(String key, String... replacements) {
        String text = pick(key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        return text;
    }

    /** Alle Schluessel, fuer Tests. */
    public static java.util.Set<String> keys() {
        return TEXTS.stringPropertyNames();
    }
}

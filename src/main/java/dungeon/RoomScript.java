package dungeon;

import character.Attribute;
import character.CharacterClass;
import item.Item;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Die Texte und die Struktur eines Raums, gelesen aus src/main/resources/story/rooms/NAME.properties.
 * So bleiben Story-Texte aus dem Code heraus. Welche Werte die Checks bekommen, wuerfelt der
 * Dungeon-Generator aus dem Seed.
 *
 * @param description Beschreibung des Raums (Stimmung), wird in jedem Menue des Raums gezeigt
 * @param freeLabel   Menuepunkt, wenn der Durchgang frei ist (null bei Endgegner-Raum)
 * @param freeText    Text beim Weitergehen
 * @param obstacleLabel Menuepunkt zum Untersuchen des Hindernisses, wenn der Durchgang versperrt ist
 * @param obstacleText  Text dazu
 * @param ways        Ansaetze, das Hindernis zu bezwingen
 * @param interactions Dinge zum Untersuchen im Raum
 * @param extra       weitere Texte (z. B. potion.text im Endgegner-Raum)
 */
public record RoomScript(String name, List<String> description, String freeLabel, String freeText,
                         String obstacleLabel, String obstacleText, List<WayDef> ways,
                         List<InteractionDef> interactions, Map<String, String> extra) {

    private static final Map<String, RoomScript> CACHE = new ConcurrentHashMap<>();

    public static RoomScript load(String name) {
        return CACHE.computeIfAbsent(name, RoomScript::read);
    }

    private static RoomScript read(String name) {
        String path = "/story/rooms/" + name + ".properties";
        Properties p = new Properties();
        try (InputStream in = RoomScript.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Room script not found: " + path);
            }
            p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        List<String> description = new ArrayList<>();
        for (int i = 1; p.containsKey("description." + i); i++) {
            description.add(p.getProperty("description." + i));
        }
        List<WayDef> ways = new ArrayList<>();
        for (int i = 1; p.containsKey("way." + i + ".label"); i++) {
            String prefix = "way." + i;
            ways.add(new WayDef(required(p, name, prefix + ".label"),
                    Attribute.valueOf(required(p, name, prefix + ".attribute")),
                    Boolean.parseBoolean(p.getProperty(prefix + ".harder", "false")),
                    required(p, name, prefix + ".success"),
                    required(p, name, prefix + ".failure")));
        }
        List<InteractionDef> interactions = new ArrayList<>();
        for (int i = 1; p.containsKey("poi." + i + ".label"); i++) {
            interactions.add(readInteraction(p, name, "poi." + i, "poi" + i, true));
        }
        Map<String, String> extra = new java.util.HashMap<>();
        for (String key : p.stringPropertyNames()) {
            if (key.startsWith("extra.")) {
                extra.put(key.substring("extra.".length()), p.getProperty(key));
            }
        }
        return new RoomScript(required(p, name, "name"), List.copyOf(description),
                p.getProperty("free.label"), p.getProperty("free.text"),
                p.getProperty("obstacle.label"), p.getProperty("obstacle.text"),
                List.copyOf(ways), List.copyOf(interactions), Map.copyOf(extra));
    }

    private static InteractionDef readInteraction(Properties p, String file, String prefix, String id,
                                                  boolean allowFollow) {
        String attributeText = p.getProperty(prefix + ".attribute");
        Attribute attribute = attributeText == null ? null : Attribute.valueOf(attributeText);
        String success = p.getProperty(prefix + ".success");
        String failure = p.getProperty(prefix + ".failure");
        if (attribute != null && (success == null || failure == null)) {
            throw new IllegalArgumentException(file + ": " + prefix + " has a check but no success/failure text");
        }
        String itemText = p.getProperty(prefix + ".item");
        String classText = p.getProperty(prefix + ".class");
        InteractionDef follow = null;
        if (allowFollow && p.containsKey(prefix + ".follow.label")) {
            follow = readInteraction(p, file, prefix + ".follow", id + ".follow", false);
        }
        return new InteractionDef(id,
                required(p, file, prefix + ".label"),
                required(p, file, prefix + ".text"),
                attribute,
                Boolean.parseBoolean(p.getProperty(prefix + ".easier", "false")),
                Effect.valueOf(p.getProperty(prefix + ".effect", "NONE")),
                itemText == null ? null : Item.valueOf(itemText),
                classText == null ? null : CharacterClass.valueOf(classText),
                success, failure,
                Boolean.parseBoolean(p.getProperty(prefix + ".damage", "false")),
                follow);
    }

    private static String required(Properties p, String file, String key) {
        String value = p.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException(file + ": missing key " + key);
        }
        return value;
    }
}

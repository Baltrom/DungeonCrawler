package save;

import character.CharacterClass;
import item.Item;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Liest und schreibt den einen Spielstand als Textdatei (eine Zeile pro Wert: schluessel=wert). */
public class SaveManager {

    private final Path file;

    public SaveManager(Path file) {
        this.file = file;
    }

    public boolean exists() {
        return Files.exists(file);
    }

    public void save(SaveGame save) {
        List<String> lines = new ArrayList<>();
        lines.add("seed=" + save.seed());
        lines.add("name=" + save.name());
        lines.add("class=" + save.characterClass().name());
        lines.add("room=" + save.roomIndex());
        lines.add("hp=" + save.currentHp());
        lines.add("charges=" + save.spellCharges());
        lines.add("items=" + String.join(",", save.items().stream().map(Item::name).toList()));
        lines.add("lootTried=" + save.lootTried());
        lines.add("specialDone=" + save.specialDone());
        lines.add("ordinaryDefeated=" + save.ordinaryDefeated());
        lines.add("arcaneSight=" + save.arcaneSight());
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Gibt den Spielstand zurueck, oder leer, wenn es keinen gibt oder die Datei beschaedigt ist. */
    public Optional<SaveGame> load() {
        if (!exists()) {
            return Optional.empty();
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        try {
            SaveGame save = parse(lines);
            save.toPlayer(); // prueft, dass Lebenspunkte, Ladungen und Items zusammenpassen
            return Optional.of(save);
        } catch (RuntimeException corrupt) {
            return Optional.empty();
        }
    }

    public void delete() {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static SaveGame parse(List<String> lines) {
        Map<String, String> values = new HashMap<>();
        for (String line : lines) {
            int separator = line.indexOf('=');
            if (separator > 0) {
                values.put(line.substring(0, separator), line.substring(separator + 1));
            }
        }
        List<Item> items = new ArrayList<>();
        String itemText = value(values, "items");
        if (!itemText.isEmpty()) {
            for (String itemName : itemText.split(",")) {
                items.add(Item.valueOf(itemName));
            }
        }
        return new SaveGame(
                Long.parseLong(value(values, "seed")),
                value(values, "name"),
                CharacterClass.valueOf(value(values, "class")),
                Integer.parseInt(value(values, "room")),
                Integer.parseInt(value(values, "hp")),
                Integer.parseInt(value(values, "charges")),
                items,
                bool(values, "lootTried"),
                bool(values, "specialDone"),
                bool(values, "ordinaryDefeated"),
                bool(values, "arcaneSight"));
    }

    private static String value(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing key: " + key);
        }
        return value;
    }

    private static boolean bool(Map<String, String> values, String key) {
        String value = value(values, key);
        if (!value.equals("true") && !value.equals("false")) {
            throw new IllegalArgumentException("Not a boolean: " + key);
        }
        return Boolean.parseBoolean(value);
    }
}

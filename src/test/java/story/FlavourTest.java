package story;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import item.Item;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FlavourTest {

    /** Alle Schluessel, die das Spiel benutzt (ohne die Endung .1, .2, ...). */
    private static final List<String> USED_KEYS = List.of(
            "trap.hit", "trap.fumble", "trap.dead", "trap.survive", "loot.empty", "key.use",
            "potion.heal", "potion.charge", "spell.arcane",
            "encounter.ordinary", "encounter.boss", "encounter.sneak.rogue", "encounter.sneak.other",
            "encounter.spy", "encounter.noticed",
            "statue.label", "statue.look", "statue.success", "statue.fail", "statue.defeated",
            "hint.boss", "hint.ordinary", "hint.special", "hint.room", "hint.blocked", "hint.free",
            "combat.hit.mage", "combat.miss.mage", "combat.hit.rogue", "combat.miss.rogue",
            "combat.ambush", "combat.fireball", "combat.shield", "combat.enemyhit", "combat.enemymiss",
            "combat.win.skeleton", "combat.win.gargoyle", "combat.win.king", "combat.win.default",
            "combat.lose");

    private final Flavour flavour = new Flavour(new Random(1));

    @Test
    void everyKeyTheGameUsesHasAtLeastOneText() {
        for (String key : USED_KEYS) {
            assertTrue(flavour.has(key), "Missing flavour text: " + key);
        }
        for (Item item : Item.values()) {
            assertTrue(flavour.has("item.found." + item.name()), "Missing found text for " + item);
        }
    }

    @Test
    void everyTextIsShortAsciiAndUsesOnlyKnownPlaceholders() {
        for (String key : Flavour.keys()) {
            String text = new Flavour(new Random(1)).pick(key.substring(0, key.lastIndexOf('.')));
            assertTrue(text.chars().allMatch(c -> c < 128), "Non-ASCII character in " + key + ": " + text);
            assertTrue(text.length() <= 240, key + " is too long");
            String withoutKnown = text.replace("{enemy}", "").replace("{room}", "");
            assertFalse(withoutKnown.contains("{"), "Unknown placeholder in " + key + ": " + text);
        }
    }

    @Test
    void placeholdersArePresentWhereTheyAreNeeded() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            seen.add(flavour.pick("encounter.ordinary", "{enemy}", "Skelett"));
        }
        assertTrue(seen.stream().allMatch(text -> text.contains("Skelett") && !text.contains("{enemy}")));
        assertTrue(flavour.pick("hint.room", "{room}", "Die Bibliothek").contains("Die Bibliothek"));
    }

    @Test
    void severalVariantsAreUsedOverTime() {
        Set<String> variants = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            variants.add(flavour.pick("combat.hit.mage"));
        }
        assertEquals(3, variants.size());
    }

    @Test
    void theSameSeedGivesTheSameChoices() {
        Flavour a = new Flavour(new Random(7));
        Flavour b = new Flavour(new Random(7));
        for (int i = 0; i < 30; i++) {
            assertEquals(a.pick("combat.enemyhit", "{enemy}", "X"), b.pick("combat.enemyhit", "{enemy}", "X"));
        }
    }

    @Test
    void anUnknownKeyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> flavour.pick("does.not.exist"));
    }
}

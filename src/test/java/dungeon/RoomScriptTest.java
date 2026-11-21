package dungeon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoomScriptTest {

    private static void assertGoodText(String text, String where) {
        assertNotNull(text, where + " is missing");
        assertFalse(text.isBlank(), where + " is empty");
        assertTrue(text.chars().allMatch(c -> c < 128), "Non-ASCII character in " + where + ": " + text);
        assertTrue(text.length() <= 240, where + " is too long (" + text.length() + "): " + text);
    }

    private static List<InteractionDef> allDefs(RoomScript script) {
        List<InteractionDef> defs = new ArrayList<>();
        for (InteractionDef def : script.interactions()) {
            defs.add(def);
            if (def.follow() != null) {
                defs.add(def.follow());
            }
        }
        return defs;
    }

    private static void assertGoodInteraction(InteractionDef def, String where) {
        assertGoodText(def.label(), where + " label");
        assertGoodText(def.text(), where + " text");
        if (def.attribute() != null) {
            assertGoodText(def.successText(), where + " success");
            assertGoodText(def.failureText(), where + " failure");
        } else {
            assertFalse(def.failureDamage(), where + " has damage without a check");
        }
    }

    @Test
    void everyRoomTemplateScriptIsComplete() {
        for (RoomTemplate template : RoomTemplate.values()) {
            RoomScript script = template.getScript();
            String name = template.name();
            assertGoodText(script.name(), name + " name");
            assertTrue(script.description().size() >= 4, name + " needs an atmospheric description");
            script.description().forEach(line -> assertGoodText(line, name + " description"));
            assertGoodText(script.freeLabel(), name + " free label");
            assertGoodText(script.freeText(), name + " free text");
            assertGoodText(script.obstacleLabel(), name + " obstacle label");
            assertGoodText(script.obstacleText(), name + " obstacle text");

            assertEquals(2, script.ways().size(), name + " needs two ways");
            for (WayDef way : script.ways()) {
                assertGoodText(way.label(), name + " way label");
                assertGoodText(way.successText(), name + " way success");
                assertGoodText(way.failureText(), name + " way failure");
            }
            assertTrue(script.ways().stream().anyMatch(WayDef::harder), name + " needs one harder way");
            assertTrue(script.ways().stream().anyMatch(way -> !way.harder()), name + " needs one easier way");

            assertTrue(script.interactions().size() >= 3, name + " needs at least three things to examine");
            for (InteractionDef def : allDefs(script)) {
                assertGoodInteraction(def, name + " " + def.id());
            }
        }
    }

    @Test
    void everyRoomTemplateOffersTheFullMixOfInteractions() {
        for (RoomTemplate template : RoomTemplate.values()) {
            RoomScript script = template.getScript();
            List<InteractionDef> defs = allDefs(script);
            String name = template.name();
            assertEquals(1, defs.stream().filter(d -> d.effect() == Effect.ITEM).count(),
                    name + " needs exactly one item spot");
            assertTrue(defs.stream().anyMatch(d -> d.effect() == Effect.HINT), name + " needs a hint");
            assertTrue(defs.stream().anyMatch(d -> d.effect() == Effect.INSIGHT), name + " needs an insight");
            assertTrue(script.interactions().stream().anyMatch(d -> d.follow() != null),
                    name + " needs a branching interaction");
            assertTrue(defs.stream().anyMatch(InteractionDef::failureDamage), name + " needs a small trap");
            assertTrue(defs.stream().anyMatch(d -> d.attribute() == null), name + " needs pure atmosphere");
        }
    }

    @Test
    void interactionIdsAreUniqueInEveryRoom() {
        for (RoomTemplate template : RoomTemplate.values()) {
            List<String> ids = allDefs(template.getScript()).stream().map(InteractionDef::id).toList();
            assertEquals(ids.size(), ids.stream().distinct().count(), template + " has duplicate ids");
        }
    }

    @Test
    void theEntranceOffersStartHelpForBothClassesAndAFreeGate() {
        RoomScript script = RoomScript.load("room0");
        assertGoodText(script.name(), "room0 name");
        assertTrue(script.description().size() >= 4);
        assertGoodText(script.freeLabel(), "room0 free label");
        assertGoodText(script.freeText(), "room0 free text");
        assertTrue(script.ways().isEmpty());
        for (InteractionDef def : allDefs(script)) {
            assertGoodInteraction(def, "room0 " + def.id());
        }
        assertTrue(script.interactions().stream().anyMatch(d -> d.onlyFor() == CharacterClass.MAGE
                && d.fixedItem() != null));
        assertTrue(script.interactions().stream().anyMatch(d -> d.onlyFor() == CharacterClass.ROGUE
                && d.fixedItem() != null));
    }

    @Test
    void theBossRoomHasADescriptionAndAPotionText() {
        RoomScript script = RoomScript.load("room5");
        assertGoodText(script.name(), "room5 name");
        assertTrue(script.description().size() >= 3);
        assertGoodText(script.extra().get("potion.text"), "room5 potion text");
    }

    @Test
    void anUnknownScriptIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RoomScript.load("does-not-exist"));
    }

    @Test
    void roomNamesAreUnique() {
        List<String> names = new ArrayList<>();
        for (RoomTemplate template : RoomTemplate.values()) {
            names.add(template.getDisplayName());
        }
        assertEquals(names.size(), names.stream().distinct().count());
    }
}

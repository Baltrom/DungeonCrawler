package story;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import dungeon.RoomTemplate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StoryTextTest {

    private void assertAsciiText(String name) {
        List<String> lines = StoryText.load(name);
        assertFalse(lines.isEmpty(), name + " is empty");
        for (String line : lines) {
            assertTrue(line.chars().allMatch(c -> c < 128), "Non-ASCII character in " + name + ": " + line);
        }
    }

    @Test
    void everyRoomTemplateHasADescription() {
        for (RoomTemplate template : RoomTemplate.values()) {
            assertAsciiText(template.getTextName());
        }
    }

    @Test
    void everyClassHasAnIntro() {
        for (CharacterClass characterClass : CharacterClass.values()) {
            assertAsciiText("intro_" + characterClass.getArtName());
        }
    }

    @Test
    void entranceBossRoomAndEndingsHaveTexts() {
        List<String> names = new ArrayList<>(List.of("room0", "room5", "victory", "death"));
        names.forEach(this::assertAsciiText);
    }

    @Test
    void unknownTextIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> StoryText.load("does-not-exist"));
    }
}

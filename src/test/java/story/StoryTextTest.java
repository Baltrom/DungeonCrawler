package story;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import java.util.List;
import org.junit.jupiter.api.Test;

class StoryTextTest {

    private void assertAsciiText(String name) {
        List<String> lines = StoryText.load(name);
        assertFalse(lines.isEmpty(), name + " is empty");
        for (String line : lines) {
            assertTrue(line.chars().noneMatch(Character::isISOControl), "Control character in " + name + ": " + line);
        }
    }

    @Test
    void everyClassHasAnIntro() {
        for (CharacterClass characterClass : CharacterClass.values()) {
            assertAsciiText("intro_" + characterClass.getArtName());
        }
    }

    @Test
    void theEndingsHaveTexts() {
        assertAsciiText("victory");
        assertAsciiText("death");
    }

    @Test
    void unknownTextIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> StoryText.load("does-not-exist"));
    }
}

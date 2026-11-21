package story;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import java.util.List;
import org.junit.jupiter.api.Test;

class AsciiArtTest {

    @Test
    void everyClassHasAnAsciiOnlyPicture() {
        for (CharacterClass characterClass : CharacterClass.values()) {
            List<String> lines = AsciiArt.load(characterClass.getArtName());
            assertFalse(lines.isEmpty(), characterClass + " has no picture");
            for (String line : lines) {
                assertTrue(line.chars().allMatch(c -> c < 128), "Non-ASCII character in: " + line);
            }
        }
    }

    @Test
    void titlePictureIsAsciiOnly() {
        List<String> lines = AsciiArt.load("title");
        assertFalse(lines.isEmpty());
        for (String line : lines) {
            assertTrue(line.chars().allMatch(c -> c < 128), "Non-ASCII character in: " + line);
        }
    }

    @Test
    void unknownPictureIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AsciiArt.load("does-not-exist"));
    }
}

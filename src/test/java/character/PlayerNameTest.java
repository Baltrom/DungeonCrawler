package character;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerNameTest {

    @Test
    void lettersAndDigitsAreValid() {
        assertTrue(PlayerName.isValid("Ria"));
        assertTrue(PlayerName.isValid("Max123"));
        assertTrue(PlayerName.isValid("7"));
    }

    @Test
    void fifteenCharactersAreValidSixteenAreNot() {
        assertTrue(PlayerName.isValid("a".repeat(15)));
        assertFalse(PlayerName.isValid("a".repeat(16)));
    }

    @Test
    void emptyAndNullAreInvalid() {
        assertFalse(PlayerName.isValid(""));
        assertFalse(PlayerName.isValid(null));
    }

    @Test
    void spacesAndSpecialCharactersAreInvalid() {
        assertFalse(PlayerName.isValid("Max Muster"));
        assertFalse(PlayerName.isValid(" "));
        assertFalse(PlayerName.isValid("a_b"));
        assertFalse(PlayerName.isValid("Ria!"));
        assertFalse(PlayerName.isValid("Mädchen"));
    }
}

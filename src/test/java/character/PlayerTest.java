package character;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void startsWithFullHpOfItsClass() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        assertEquals(14, mage.getCurrentHp());
        assertEquals(14, mage.getMaxHp());
        assertEquals(16, rogue.getCurrentHp());
        assertEquals(16, rogue.getMaxHp());
    }

    @Test
    void attributesComeFromTheClass() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        assertEquals(1, mage.getAttribute(Attribute.STRENGTH));
        assertEquals(2, mage.getAttribute(Attribute.DEXTERITY));
        assertEquals(5, mage.getAttribute(Attribute.INTELLIGENCE));
    }

    @Test
    void keepsNameAndClass() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        assertEquals("Ria", player.getName());
        assertEquals(CharacterClass.MAGE, player.getCharacterClass());
    }

    @Test
    void damageReducesHpButNotBelowZero() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        player.takeDamage(5);
        assertEquals(9, player.getCurrentHp());
        assertTrue(player.isAlive());
        player.takeDamage(100);
        assertEquals(0, player.getCurrentHp());
        assertFalse(player.isAlive());
    }

    @Test
    void healingIsCappedAtMaxHp() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        player.takeDamage(8);
        player.heal(6);
        assertEquals(12, player.getCurrentHp());
        player.heal(6);
        assertEquals(14, player.getCurrentHp());
    }

    @Test
    void negativeDamageAndHealingAreRejected() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        assertThrows(IllegalArgumentException.class, () -> player.takeDamage(-1));
        assertThrows(IllegalArgumentException.class, () -> player.heal(-1));
    }

    @Test
    void restoreStateSetsHpAndSpellCharges() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.restoreState(5, 1);
        assertEquals(5, mage.getCurrentHp());
        assertEquals(1, mage.getSpellCharges());
    }

    @Test
    void restoreStateRejectsValuesOutsideTheValidRange() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        assertThrows(IllegalArgumentException.class, () -> mage.restoreState(0, 1));
        assertThrows(IllegalArgumentException.class, () -> mage.restoreState(15, 1));
        assertThrows(IllegalArgumentException.class, () -> mage.restoreState(5, 4));
        assertThrows(IllegalArgumentException.class, () -> mage.restoreState(5, -1));
    }

    @Test
    void invalidNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Player("Max Muster", CharacterClass.ROGUE));
        assertThrows(IllegalArgumentException.class, () -> new Player("", CharacterClass.ROGUE));
    }
}

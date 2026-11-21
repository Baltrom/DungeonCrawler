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
        assertEquals(12, mage.getCurrentHp());
        assertEquals(12, mage.getMaxHp());
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
        assertEquals(7, player.getCurrentHp());
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
        assertEquals(10, player.getCurrentHp());
        player.heal(6);
        assertEquals(12, player.getCurrentHp());
    }

    @Test
    void negativeDamageAndHealingAreRejected() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        assertThrows(IllegalArgumentException.class, () -> player.takeDamage(-1));
        assertThrows(IllegalArgumentException.class, () -> player.heal(-1));
    }

    @Test
    void invalidNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Player("Max Muster", CharacterClass.ROGUE));
        assertThrows(IllegalArgumentException.class, () -> new Player("", CharacterClass.ROGUE));
    }
}

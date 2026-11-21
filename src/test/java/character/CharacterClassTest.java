package character;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dice.Dice;
import org.junit.jupiter.api.Test;

// Pruefen die festgelegten Werte aus Ideen.md
class CharacterClassTest {

    @Test
    void mageHasTheAgreedValues() {
        CharacterClass mage = CharacterClass.MAGE;
        assertEquals("Magier", mage.getDisplayName());
        assertEquals(1, mage.getStrength());
        assertEquals(2, mage.getDexterity());
        assertEquals(5, mage.getIntelligence());
        assertEquals(12, mage.getMaxHp());
        assertEquals(11, mage.getArmorClass());
        assertEquals(Dice.D6, mage.getDamageDie());
    }

    @Test
    void rogueHasTheAgreedValues() {
        CharacterClass rogue = CharacterClass.ROGUE;
        assertEquals("Schurke", rogue.getDisplayName());
        assertEquals(2, rogue.getStrength());
        assertEquals(5, rogue.getDexterity());
        assertEquals(1, rogue.getIntelligence());
        assertEquals(16, rogue.getMaxHp());
        assertEquals(13, rogue.getArmorClass());
        assertEquals(Dice.D10, rogue.getDamageDie());
    }
}

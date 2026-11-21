package character;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import item.Inventory;
import item.Item;
import org.junit.jupiter.api.Test;

class PlayerItemTest {

    @Test
    void passiveItemsWorkJustByBeingInTheInventory() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.RING_OF_WISDOM);
        mage.addItem(Item.PROTECTION_AMULET);
        assertEquals(6, mage.getAttribute(Attribute.INTELLIGENCE));
        assertEquals(12, mage.getArmorClass());
    }

    @Test
    void attributesCanGoAboveFive() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.RING_OF_WISDOM);
        mage.addItem(Item.RING_OF_WISDOM);
        assertEquals(7, mage.getAttribute(Attribute.INTELLIGENCE));
    }

    @Test
    void ringOfLifeRaisesMaxHpAbove20AndHealsTheExtraPoints() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        for (int i = 0; i < 2; i++) {
            rogue.addItem(Item.RING_OF_LIFE);
        }
        assertEquals(22, rogue.getMaxHp());
        assertEquals(22, rogue.getCurrentHp());
    }

    @Test
    void ringOfLifeDoesNotHealExistingDamage() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(7);
        mage.addItem(Item.RING_OF_LIFE);
        assertEquals(17, mage.getMaxHp());
        assertEquals(10, mage.getCurrentHp());
    }

    @Test
    void droppingTheRingLowersMaxHpAndCapsCurrentHp() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.RING_OF_LIFE);
        mage.removeItem(Item.RING_OF_LIFE);
        assertEquals(14, mage.getMaxHp());
        assertEquals(14, mage.getCurrentHp());
        assertFalse(mage.hasItem(Item.RING_OF_LIFE));
    }

    @Test
    void attackBonusUsesTheMainAttributeOfTheClassPlusItems() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        assertEquals(5, mage.getAttackBonus());
        assertEquals(5, rogue.getAttackBonus());
        rogue.addItem(Item.MAGIC_DAGGER);
        rogue.addItem(Item.BOOTS_OF_SHADOWS);
        assertEquals(7, rogue.getAttackBonus());
        assertEquals(1, rogue.getDamageBonus());
    }

    @Test
    void addingToAFullInventoryIsRefused() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        for (int i = 0; i < Inventory.CAPACITY; i++) {
            assertTrue(player.addItem(Item.IRON_KEY));
        }
        assertTrue(player.isInventoryFull());
        assertFalse(player.addItem(Item.RING_OF_LIFE));
    }

    @Test
    void healingPotionHealsSixAndIsConsumed() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        player.takeDamage(8);
        player.addItem(Item.HEALING_POTION);
        player.drink(Item.HEALING_POTION);
        assertEquals(12, player.getCurrentHp());
        assertFalse(player.hasItem(Item.HEALING_POTION));
    }

    @Test
    void chargePotionRefillsTheMagesSpellCharges() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.useSpellCharge();
        mage.useSpellCharge();
        assertEquals(1, mage.getSpellCharges());
        mage.addItem(Item.CHARGE_POTION);
        mage.drink(Item.CHARGE_POTION);
        assertEquals(3, mage.getSpellCharges());
    }

    @Test
    void rogueCannotDrinkTheChargePotion() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        rogue.addItem(Item.CHARGE_POTION);
        assertFalse(rogue.canDrink(Item.CHARGE_POTION));
        assertThrows(IllegalStateException.class, () -> rogue.drink(Item.CHARGE_POTION));
        assertTrue(rogue.hasItem(Item.CHARGE_POTION));
    }

    @Test
    void cannotDrinkWhatYouDoNotHave() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        assertFalse(player.canDrink(Item.HEALING_POTION));
    }

    @Test
    void spellChargesAreUsedUpOneByOne() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        assertEquals(3, mage.getSpellCharges());
        assertTrue(mage.useSpellCharge());
        assertTrue(mage.useSpellCharge());
        assertTrue(mage.useSpellCharge());
        assertFalse(mage.useSpellCharge());
        assertEquals(0, mage.getSpellCharges());
    }

    @Test
    void rogueHasNoSpells() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        assertFalse(rogue.hasSpells());
        assertFalse(rogue.useSpellCharge());
    }
}

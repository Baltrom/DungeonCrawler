package item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void healingPotionIsNotPartOfTheRandomLootPool() {
        assertFalse(Item.lootPool().contains(Item.HEALING_POTION));
        assertTrue(Item.lootPool().contains(Item.CHARGE_POTION));
        assertTrue(Item.lootPool().contains(Item.IRON_KEY));
    }

    @Test
    void healingPotionHealsSix() {
        assertEquals(6, Item.HEALING_POTION_AMOUNT);
    }

    @Test
    void ringOfLifeIsRarerThanTheOtherItems() {
        assertTrue(Item.RING_OF_LIFE.getLootWeight() < Item.RING_OF_WISDOM.getLootWeight());
    }

    @Test
    void itemTypesMatchTheirRole() {
        assertEquals(ItemType.POTION, Item.HEALING_POTION.getType());
        assertEquals(ItemType.POTION, Item.CHARGE_POTION.getType());
        assertEquals(ItemType.KEY, Item.IRON_KEY.getType());
        assertEquals(ItemType.PASSIVE, Item.RING_OF_LIFE.getType());
        assertEquals(ItemType.PASSIVE, Item.MAGIC_DAGGER.getType());
    }

    @Test
    void displayNamesArePrintable() {
        for (Item item : Item.values()) {
            assertTrue((item.getDisplayName() + item.getDescription()).chars().noneMatch(Character::isISOControl),
                    "Control character in " + item);
        }
    }
}

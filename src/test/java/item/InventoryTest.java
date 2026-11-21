package item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.Attribute;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    void holdsAtMostFiveItems() {
        Inventory inventory = new Inventory();
        for (int i = 0; i < Inventory.CAPACITY; i++) {
            assertTrue(inventory.add(Item.HEALING_POTION));
        }
        assertTrue(inventory.isFull());
        assertFalse(inventory.add(Item.IRON_KEY));
        assertEquals(5, inventory.getItems().size());
    }

    @Test
    void everyItemTakesOneSlotEvenIfItIsTheSameKind() {
        Inventory inventory = new Inventory();
        inventory.add(Item.CHARGE_POTION);
        inventory.add(Item.CHARGE_POTION);
        assertEquals(2, inventory.getItems().size());
    }

    @Test
    void removedItemsAreGoneAndFreeTheSlot() {
        Inventory inventory = new Inventory();
        inventory.add(Item.IRON_KEY);
        assertTrue(inventory.remove(Item.IRON_KEY));
        assertFalse(inventory.contains(Item.IRON_KEY));
        assertFalse(inventory.remove(Item.IRON_KEY));
    }

    @Test
    void bonusesStack() {
        Inventory inventory = new Inventory();
        inventory.add(Item.RING_OF_LIFE);
        inventory.add(Item.RING_OF_LIFE);
        inventory.add(Item.RING_OF_WISDOM);
        inventory.add(Item.PROTECTION_AMULET);
        inventory.add(Item.MAGIC_DAGGER);
        assertEquals(6, inventory.maxHpBonus());
        assertEquals(1, inventory.attributeBonus(Attribute.INTELLIGENCE));
        assertEquals(0, inventory.attributeBonus(Attribute.STRENGTH));
        assertEquals(1, inventory.armorBonus());
        assertEquals(1, inventory.hitBonus());
        assertEquals(1, inventory.damageBonus());
    }

    @Test
    void potionsAndKeysGiveNoBonuses() {
        Inventory inventory = new Inventory();
        inventory.add(Item.HEALING_POTION);
        inventory.add(Item.CHARGE_POTION);
        inventory.add(Item.IRON_KEY);
        assertEquals(0, inventory.maxHpBonus());
        assertEquals(0, inventory.armorBonus());
        assertEquals(0, inventory.hitBonus());
    }
}

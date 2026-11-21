package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import character.Spell;
import item.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlayerActionsTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private PlayerActions actionsFor(Player player, String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        return new PlayerActions(io, player);
    }

    @Test
    void drinkingAHealingPotionHealsAndUsesItUp() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        player.takeDamage(8);
        player.addItem(Item.HEALING_POTION);
        PlayerActions actions = actionsFor(player, "1");
        assertTrue(actions.canDrinkAnything());
        assertTrue(actions.drinkPotion());
        assertEquals(12, player.getCurrentHp());
        assertFalse(actions.canDrinkAnything());
    }

    @Test
    void goingBackFromThePotionMenuKeepsThePotion() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        player.addItem(Item.HEALING_POTION);
        PlayerActions actions = actionsFor(player, "2");
        assertFalse(actions.drinkPotion());
        assertTrue(player.hasItem(Item.HEALING_POTION));
    }

    @Test
    void rogueCannotUseTheChargePotion() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        rogue.addItem(Item.CHARGE_POTION);
        assertFalse(actionsFor(rogue).canDrinkAnything());
    }

    @Test
    void castingOutsideCombatOffersOnlyArcaneSightAndUsesACharge() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        PlayerActions actions = actionsFor(mage, "1");
        assertEquals(Spell.ARCANE_SIGHT, actions.chooseSpell(false));
        assertEquals(2, mage.getSpellCharges());
        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Arkane Sicht"));
        assertFalse(text.contains("Feuerball"));
    }

    @Test
    void castingInCombatOffersFireballAndShield() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        PlayerActions actions = actionsFor(mage, "2");
        assertEquals(Spell.SHIELD, actions.chooseSpell(true));
        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Feuerball"));
        assertFalse(text.contains("Arkane Sicht"));
    }

    @Test
    void goingBackFromTheSpellMenuKeepsTheCharge() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        PlayerActions actions = actionsFor(mage, "3");
        assertNull(actions.chooseSpell(true));
        assertEquals(3, mage.getSpellCharges());
    }

    @Test
    void noSpellWithoutCharges() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.useSpellCharge();
        mage.useSpellCharge();
        mage.useSpellCharge();
        assertNull(actionsFor(mage).chooseSpell(true));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("keine Zauberladungen"));
    }

    @Test
    void statusShowsHpArmorChargesAndInventory() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.RING_OF_LIFE);
        List<String> lines = actionsFor(mage).statusLines();
        assertTrue(lines.get(0).contains("Ria der Magier"));
        assertTrue(lines.get(0).contains("HP 17/17"));
        assertTrue(lines.get(0).contains("RK 11"));
        assertTrue(lines.get(0).contains("Ladungen 3/3"));
        assertTrue(lines.get(1).contains("Inventar (1/5): Ring des Lebens"));
    }

    @Test
    void rogueStatusHasNoChargesAndShowsAnEmptyInventory() {
        List<String> lines = actionsFor(new Player("Bob", CharacterClass.ROGUE)).statusLines();
        assertFalse(lines.get(0).contains("Ladungen"));
        assertTrue(lines.get(1).contains("Inventar (0/5): leer"));
    }
}

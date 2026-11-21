package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import combat.Enemy;
import dice.Dice;
import dice.FixedDice;
import item.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CombatTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private Combat combat(Dice dice, Player player, Enemy enemy, String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        return new Combat(io, dice, player, enemy, new PlayerActions(io, player));
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void enemyValuesMatchTheBalancedValues() {
        Enemy skeleton = Enemy.skeleton();
        assertEquals(6, skeleton.maxHp());
        assertEquals(9, skeleton.armorClass());
        assertEquals(1, skeleton.attackBonus());
        assertEquals(Dice.D6, skeleton.damageDie());
        Enemy gargoyle = Enemy.gargoyle();
        assertEquals(10, gargoyle.maxHp());
        assertEquals(11, gargoyle.armorClass());
        assertEquals(2, gargoyle.attackBonus());
        Enemy king = Enemy.deadKing();
        assertEquals(18, king.maxHp());
        assertEquals(13, king.armorClass());
        assertEquals(2, king.attackBonus());
        assertEquals(Dice.D6, king.damageDie());
    }

    @Test
    void thePlayerWinsByReducingTheEnemyToZero() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Spieler trifft (15+5=20, 3 Schaden), Gegner verfehlt (2+1=3), Spieler trifft (3) -> 6 >= 6
        Combat combat = combat(new FixedDice(15, 3, 2, 15, 3), mage, Enemy.skeleton(), "1", "1");
        assertTrue(combat.run(true, false));
        assertEquals(14, mage.getCurrentHp());
        assertTrue(printed().contains("Du hast Skelett besiegt!"));
    }

    @Test
    void theEnemyCanStartAndHit() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Gegner trifft zuerst (19+1=20 gegen RK 11, 4 Schaden), Spieler 3, Gegner verfehlt, Spieler 3
        Combat combat = combat(new FixedDice(19, 4, 15, 3, 3, 15, 3), mage, Enemy.skeleton(), "1", "1");
        assertTrue(combat.run(false, false));
        assertEquals(10, mage.getCurrentHp());
    }

    @Test
    void aOneAlwaysMissesAndATwentyAlwaysHits() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Spieler 1 (verfehlt trotz +5), Gegner 1, Spieler 20 trifft (3), Gegner 1, Spieler 20 trifft (3)
        Combat combat = combat(new FixedDice(1, 1, 20, 3, 1, 20, 3), mage, Enemy.skeleton(), "1", "1", "1");
        assertTrue(combat.run(true, false));
        assertEquals(14, mage.getCurrentHp());
        assertTrue(printed().contains("VERFEHLT"));
        assertTrue(printed().contains("TREFFER"));
    }

    @Test
    void aTwentyHitsEvenAgainstAnImpossibleArmorClass() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Enemy tank = new Enemy("Fels", 6, 40, 0, Dice.D6);
        Combat combat = combat(new FixedDice(20, 6), mage, tank, "1");
        assertTrue(combat.run(true, false));
    }

    @Test
    void thePlayerLosesWhenHpReachesZero() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(12);
        // Gegner trifft immer (20) mit 6 Schaden, Mage hat 2 HP
        Combat combat = combat(new FixedDice(20, 6), mage, Enemy.skeleton());
        assertFalse(combat.run(false, false));
        assertFalse(mage.isAlive());
        assertTrue(printed().contains("hat dich besiegt"));
    }

    @Test
    void fireballAlwaysHitsForD10AndCostsACharge() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Zauber wirken (2), Feuerball (1), W10 = 8 -> Skelett (6 HP) tot
        Combat combat = combat(new FixedDice(8), mage, Enemy.skeleton(), "2", "1");
        assertTrue(combat.run(true, false));
        assertEquals(2, mage.getSpellCharges());
    }

    @Test
    void shieldRaisesTheArmorClassForTheRestOfTheFight() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Schild wirken (kostet den Zug). Gegner: 10+1=11 gegen RK 14 -> verfehlt (ohne Schild: Treffer).
        // Spieler 20/3, Gegner 1, Spieler 20/3 -> tot
        Combat combat = combat(new FixedDice(10, 20, 3, 1, 20, 3), mage, Enemy.skeleton(),
                "2", "2", "1", "1");
        assertTrue(combat.run(true, false));
        assertEquals(14, mage.getCurrentHp());
        assertEquals(2, mage.getSpellCharges());
        assertTrue(printed().contains("gegen deine RK 14"));
    }

    @Test
    void drinkingAPotionUsesUpTheTurn() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(8);
        mage.addItem(Item.HEALING_POTION);
        // Trank (2), Heiltrank (1): Zug verbraucht, 6 -> 12 HP. Gegner verfehlt (2), Spieler 20/3, Gegner 1, Spieler 20/3
        Combat combat = combat(new FixedDice(2, 20, 3, 1, 20, 3), mage, Enemy.skeleton(),
                "2", "1", "1", "1");
        assertTrue(combat.run(true, false));
        assertEquals(12, mage.getCurrentHp());
        assertFalse(mage.hasItem(Item.HEALING_POTION));
    }

    @Test
    void goingBackFromTheSpellMenuDoesNotUseUpTheTurn() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Zauber (2), Zurueck (3), dann Angriff (1): Spieler 20/3, Gegner 1, Spieler 20/3 -> tot
        Combat combat = combat(new FixedDice(20, 3, 1, 20, 3), mage, Enemy.skeleton(), "2", "3", "1", "1");
        assertTrue(combat.run(true, false));
        assertEquals(3, mage.getSpellCharges());
    }

    @Test
    void theAmbushDoublesTheDamageOfTheFirstHitOnly() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        // Spieler verfehlt (1), Gegner verfehlt (1), Spieler trifft mit 2 Wuerfeln 3+4 = 7 >= 6 -> tot
        Combat combat = combat(new FixedDice(1, 1, 20, 3, 4), rogue, Enemy.skeleton(), "1", "1");
        assertTrue(combat.run(true, true));
        assertTrue(printed().contains("Hinterhalt"));
    }

    @Test
    void withoutAnAmbushTheRogueRollsOneD10() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        // Zwei normale Treffer: 4 und 2 -> erst nach dem zweiten tot (Skelett 6 HP), Gegner verfehlt dazwischen
        Combat combat = combat(new FixedDice(20, 4, 1, 20, 2), rogue, Enemy.skeleton(), "1", "1");
        assertTrue(combat.run(true, false));
        assertFalse(printed().contains("Hinterhalt"));
    }

    @Test
    void theMagicDaggerAddsOneToHitAndOneDamage() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        rogue.addItem(Item.MAGIC_DAGGER);
        // D10 = 5, +1 Dolch = 6 Schaden -> Skelett (6 HP) in einem Treffer tot
        Combat combat = combat(new FixedDice(20, 5), rogue, Enemy.skeleton(), "1");
        assertTrue(combat.run(true, false));
    }

    @Test
    void theMagicDaggerRaisesTheHitBonus() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        rogue.addItem(Item.MAGIC_DAGGER);
        // Wurf 7 + 5 (Geschick) + 1 (Dolch) = 13 gegen RK 13 -> Treffer (ohne Dolch: 12, verfehlt)
        Enemy dummy = new Enemy("Puppe", 6, 13, 0, Dice.D6);
        Combat combat = combat(new FixedDice(7, 5), rogue, dummy, "1");
        assertTrue(combat.run(true, false));
        assertTrue(printed().contains("= 13 gegen RK 13 -> TREFFER"));
    }

    @Test
    void thePlayerCannotFlee() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Combat combat = combat(new FixedDice(20, 3, 1, 20, 3), mage, Enemy.skeleton(), "1", "1");
        combat.run(true, false);
        assertFalse(printed().toLowerCase().contains("fliehen"));
    }
}

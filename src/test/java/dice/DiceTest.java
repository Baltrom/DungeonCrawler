package dice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DiceTest {

    private static final int ROLLS = 2000;

    @Test
    void d6StaysBetween1And6AndHitsEveryFace() {
        Dice dice = new Dice(42);
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < ROLLS; i++) {
            int result = dice.rollD6();
            assertTrue(result >= 1 && result <= 6, "D6 out of range: " + result);
            seen.add(result);
        }
        assertEquals(6, seen.size());
    }

    @Test
    void d10StaysBetween1And10AndHitsEveryFace() {
        Dice dice = new Dice(42);
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < ROLLS; i++) {
            int result = dice.rollD10();
            assertTrue(result >= 1 && result <= 10, "D10 out of range: " + result);
            seen.add(result);
        }
        assertEquals(10, seen.size());
    }

    @Test
    void d20StaysBetween1And20AndHitsEveryFace() {
        Dice dice = new Dice(42);
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < ROLLS; i++) {
            int result = dice.rollD20();
            assertTrue(result >= 1 && result <= 20, "D20 out of range: " + result);
            seen.add(result);
        }
        assertEquals(20, seen.size());
    }

    @Test
    void sameSeedGivesSameRolls() {
        Dice first = new Dice(7);
        Dice second = new Dice(7);
        for (int i = 0; i < 100; i++) {
            assertEquals(first.rollD20(), second.rollD20());
        }
    }

    @Test
    void rollWithCustomSidesStaysInRange() {
        Dice dice = new Dice(1);
        for (int i = 0; i < ROLLS; i++) {
            int result = dice.roll(4);
            assertTrue(result >= 1 && result <= 4, "D4 out of range: " + result);
        }
    }

    @Test
    void dieWithFewerThanTwoSidesIsRejected() {
        Dice dice = new Dice(1);
        assertThrows(IllegalArgumentException.class, () -> dice.roll(1));
        assertThrows(IllegalArgumentException.class, () -> dice.roll(0));
        assertThrows(IllegalArgumentException.class, () -> dice.roll(-6));
    }

    @Test
    void gameDiceWithoutSeedStillRollsInRange() {
        Dice dice = new Dice();
        for (int i = 0; i < 100; i++) {
            int result = dice.rollD20();
            assertTrue(result >= 1 && result <= 20);
        }
    }
}

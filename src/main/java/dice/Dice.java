package dice;

import java.util.Random;

public class Dice {

    public static final int D6 = 6;
    public static final int D10 = 10;
    public static final int D20 = 20;

    private final Random random;

    /** Spiel-Wuerfel: jeder Wurf faellt bei jedem Spiel neu aus. */
    public Dice() {
        this.random = new Random();
    }

    /** Nur fuer Tests: fester Seed, damit die Wuerfe reproduzierbar sind. */
    public Dice(long seed) {
        this.random = new Random(seed);
    }

    public int roll(int sides) {
        if (sides < 2) {
            throw new IllegalArgumentException("A die needs at least 2 sides, got " + sides);
        }
        return random.nextInt(sides) + 1;
    }

    public int rollD6() {
        return roll(D6);
    }

    public int rollD10() {
        return roll(D10);
    }

    public int rollD20() {
        return roll(D20);
    }
}

package dice;

/** Nur fuer Tests: liefert vorgegebene Wuerfe der Reihe nach, damit Ergebnisse vorhersagbar sind. */
public class FixedDice extends Dice {

    private final int[] values;
    private int next = 0;

    public FixedDice(int... values) {
        this.values = values;
    }

    @Override
    public int roll(int sides) {
        if (next >= values.length) {
            throw new IllegalStateException("FixedDice has no more values");
        }
        int value = values[next++];
        if (value < 1 || value > sides) {
            throw new IllegalStateException("Fixed value " + value + " does not fit a D" + sides);
        }
        return value;
    }
}

package dice;

/** Nur fuer Tests: jeder Wurf zeigt die hoechste Zahl (D20 = 20, Muenze = 2). */
public class MaxDice extends Dice {

    @Override
    public int roll(int sides) {
        return sides;
    }
}

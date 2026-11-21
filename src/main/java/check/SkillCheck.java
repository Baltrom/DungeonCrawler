package check;

import dice.Dice;

public final class SkillCheck {

    private SkillCheck() {
    }

    /** D20 + Bonus gegen den Zielwert. Eine 1 misslingt immer, eine 20 gelingt immer. */
    public static CheckResult roll(Dice dice, int bonus, int target) {
        int roll = dice.rollD20();
        boolean success = roll == 20 || (roll != 1 && roll + bonus >= target);
        return new CheckResult(roll, bonus, target, success);
    }
}

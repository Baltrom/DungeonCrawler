package combat;

import dice.Dice;

/** Ein Gegner mit festen Werten (nach dem ersten Durchspielen per Simulation leichter gemacht). */
public record Enemy(String name, int maxHp, int armorClass, int attackBonus, int damageDie) {

    public static Enemy skeleton() {
        return new Enemy("Skelett", 6, 9, 1, Dice.D6);
    }

    public static Enemy gargoyle() {
        return new Enemy("Gargoyle", 10, 11, 2, Dice.D6);
    }

    /** Der Endgegner ist in Version 1 einfach nur zaeher. */
    public static Enemy deadKing() {
        return new Enemy("Der tote Koenig", 18, 13, 2, Dice.D6);
    }
}

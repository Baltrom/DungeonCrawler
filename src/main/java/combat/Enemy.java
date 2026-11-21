package combat;

import dice.Dice;

/** Ein Gegner mit festen Werten (Startwerte, werden spaeter im Test geprueft). */
public record Enemy(String name, int maxHp, int armorClass, int attackBonus, int damageDie) {

    public static Enemy skeleton() {
        return new Enemy("Skelett", 8, 10, 2, Dice.D6);
    }

    public static Enemy gargoyle() {
        return new Enemy("Gargoyle", 12, 12, 3, Dice.D6);
    }

    /** Der Endgegner ist in Version 1 einfach nur zaeher. */
    public static Enemy deadKing() {
        return new Enemy("Der tote Koenig", 20, 13, 3, Dice.D6);
    }
}

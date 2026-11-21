package item;

import character.Attribute;

/** Passive Boni eines Items. Sie wirken, solange das Item im Inventar ist. */
public record Bonuses(int strength, int dexterity, int intelligence, int armorClass,
                      int maxHp, int hit, int damage) {

    public static final Bonuses NONE = new Bonuses(0, 0, 0, 0, 0, 0, 0);

    public static Bonuses strength(int value) {
        return new Bonuses(value, 0, 0, 0, 0, 0, 0);
    }

    public static Bonuses dexterity(int value) {
        return new Bonuses(0, value, 0, 0, 0, 0, 0);
    }

    public static Bonuses intelligence(int value) {
        return new Bonuses(0, 0, value, 0, 0, 0, 0);
    }

    public static Bonuses armorClass(int value) {
        return new Bonuses(0, 0, 0, value, 0, 0, 0);
    }

    public static Bonuses maxHp(int value) {
        return new Bonuses(0, 0, 0, 0, value, 0, 0);
    }

    public static Bonuses hitAndDamage(int value) {
        return new Bonuses(0, 0, 0, 0, 0, value, value);
    }

    public int attribute(Attribute attribute) {
        return switch (attribute) {
            case STRENGTH -> strength;
            case DEXTERITY -> dexterity;
            case INTELLIGENCE -> intelligence;
        };
    }
}

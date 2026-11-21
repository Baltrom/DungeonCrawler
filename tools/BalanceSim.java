// Balance-Werkzeug: spielt den Dungeon mit einem einfachen Bot tausendfach durch und misst die Siegchance.
// Nicht Teil des Maven-Builds. Uebersetzen und starten siehe Uebergabe.md, Abschnitt 7.
// Die Spielwerte (Gegner, Fallen, Lebenspunkte) sind hier als Zahlen eingetragen und muessen bei Aenderungen nachgezogen werden.
import character.Attribute;
import character.CharacterClass;
import dungeon.Dungeon;
import dungeon.DungeonGenerator;
import dungeon.Effect;
import dungeon.EnemyKind;
import dungeon.Interaction;
import dungeon.Room;
import dungeon.WayOption;
import item.Item;
import java.util.Random;

/** Simulation fuer die neue Raumstruktur (freie Raeume, Fallen 1W6, Patzer 2W6, P4-Werte). */
public class BalanceSim {

    static Random rnd = new Random(2024);

    static int d(int sides) { return rnd.nextInt(sides) + 1; }

    static class S {
        CharacterClass cls;
        int hp, maxHp, ac, charges, str, dex, intel, hit, dmg, heals;
        boolean chargePotion, key;
    }

    static int attr(S s, Attribute a) {
        return switch (a) { case STRENGTH -> s.str; case DEXTERITY -> s.dex; case INTELLIGENCE -> s.intel; };
    }

    static boolean fight(S s, int hp, int ac, int atk, boolean first, boolean ambush, boolean boss) {
        boolean playerTurn = first;
        while (s.hp > 0 && hp > 0) {
            if (playerTurn) {
                if (s.heals > 0 && s.hp <= 6 && boss) {
                    s.heals--;
                    s.hp = Math.min(s.maxHp, s.hp + 6);
                } else if (s.cls == CharacterClass.MAGE && s.charges > 0) {
                    s.charges--;
                    hp -= d(10);
                } else {
                    int a = (s.cls == CharacterClass.MAGE ? s.intel : s.dex) + s.hit;
                    int roll = d(20);
                    if (roll == 20 || (roll != 1 && roll + a >= ac)) {
                        int die = s.cls == CharacterClass.MAGE ? 6 : 10;
                        int dmg = d(die);
                        if (ambush) { dmg += d(die); ambush = false; }
                        hp -= dmg + s.dmg;
                    }
                }
            } else {
                int roll = d(20);
                if (roll == 20 || (roll != 1 && roll + atk >= s.ac)) s.hp -= d(6);
            }
            playerTurn = !playerTurn;
        }
        return s.hp > 0;
    }

    static boolean encounter(S s, int hp, int ac, int atk, int target, boolean boss) {
        boolean sneaking = s.cls == CharacterClass.ROGUE;
        int bonus = sneaking ? s.dex : s.intel;
        int roll = d(20);
        boolean ok = roll == 20 || (roll != 1 && roll + bonus >= target);
        boolean first = ok || d(2) == 1;
        return fight(s, hp, ac, atk, first, ok && sneaking, boss);
    }

    static void apply(S s, Item item) {
        switch (item) {
            case RING_OF_LIFE -> { s.maxHp += 3; s.hp += 3; }
            case RING_OF_WISDOM -> s.intel++;
            case BOOTS_OF_SHADOWS -> s.dex++;
            case STRENGTH_BELT -> s.str++;
            case PROTECTION_AMULET -> s.ac++;
            case MAGIC_DAGGER -> { s.hit++; s.dmg++; }
            case CHARGE_POTION -> s.chargePotion = true;
            case IRON_KEY -> s.key = true;
            default -> { }
        }
    }

    static boolean check(int bonus, int target) {
        int roll = d(20);
        return roll == 20 || (roll != 1 && roll + bonus >= target);
    }

    static String run(CharacterClass cls, boolean loot, boolean exploreMore) {
        S s = new S();
        s.cls = cls;
        s.maxHp = cls == CharacterClass.MAGE ? 14 : 16;
        s.hp = s.maxHp;
        s.ac = cls == CharacterClass.MAGE ? 11 : 13;
        s.charges = cls == CharacterClass.MAGE ? 3 : 0;
        s.str = cls.getStrength(); s.dex = cls.getDexterity(); s.intel = cls.getIntelligence();

        Dungeon dungeon = DungeonGenerator.generate(rnd.nextLong());
        // Raum 0: Starthilfe der eigenen Klasse
        for (Interaction it : dungeon.room(0).interactions()) {
            if (it.def().onlyFor() == cls && it.def().effect() == Effect.ITEM) {
                if (check(attr(s, it.def().attribute()), it.target())) apply(s, it.item());
            }
        }
        for (int i = 1; i <= 4; i++) {
            Room room = dungeon.room(i);
            if (room.enemy() == EnemyKind.ORDINARY) {
                if (!encounter(s, 6, 9, 1, room.encounterTarget(), false)) return "Skelett";
            }
            // Untersuchen: Beute-Stelle, bei exploreMore zusaetzlich die Stellen mit kleiner Falle (1W4)
            for (Interaction it : room.interactions()) {
                boolean itemSpot = it.def().effect() == Effect.ITEM;
                boolean trapSpot = it.def().failureDamage();
                if ((loot && itemSpot) || (exploreMore && trapSpot)) {
                    boolean ok = check(attr(s, it.def().attribute()), it.target());
                    if (ok && itemSpot && it.item() != null) apply(s, it.item());
                    if (!ok && trapSpot) {
                        s.hp -= d(4);
                        if (s.hp <= 0) return "Untersuchen";
                    }
                }
            }
            if (s.chargePotion && s.charges == 0 && cls == CharacterClass.MAGE) { s.charges = 3; s.chargePotion = false; }
            if (!room.blocked()) continue;
            if (s.key) { s.key = false; continue; }
            WayOption best = null;
            int bestMargin = Integer.MIN_VALUE;
            for (WayOption w : room.wayOptions()) {
                int margin = attr(s, w.def().attribute()) - w.target();
                if (margin > bestMargin) { bestMargin = margin; best = w; }
            }
            int roll = d(20);
            int bonus = attr(s, best.def().attribute());
            boolean ok = roll == 20 || (roll != 1 && roll + bonus >= best.target());
            if (!ok) {
                int dmg = d(6);
                if (roll == 1) dmg += d(6);
                s.hp -= dmg;
                if (s.hp <= 0) return "Falle";
            }
        }
        s.heals += 1;
        if (!encounter(s, 18, 13, 2, dungeon.room(5).encounterTarget(), true)) return "Endgegner";
        return "SIEG";
    }

    static void report(String name, boolean loot, boolean more) {
        int runs = 60000;
        for (CharacterClass cls : CharacterClass.values()) {
            java.util.Map<String, Integer> c = new java.util.TreeMap<>();
            for (int i = 0; i < runs; i++) c.merge(run(cls, loot, more), 1, Integer::sum);
            System.out.printf("%-38s %-8s SIEG %5.1f%% | Falle %4.1f%% | Untersuchen %4.1f%% | Skelett %4.1f%% | Endgegner %4.1f%%%n",
                    name, cls.getDisplayName(), 100.0 * c.getOrDefault("SIEG", 0) / runs,
                    100.0 * c.getOrDefault("Falle", 0) / runs, 100.0 * c.getOrDefault("Untersuchen", 0) / runs,
                    100.0 * c.getOrDefault("Skelett", 0) / runs, 100.0 * c.getOrDefault("Endgegner", 0) / runs);
        }
    }

    public static void main(String[] args) {
        report("Neu, nur Beute suchen", true, false);
        report("Neu, ohne Beute", false, false);
        report("Neu, Beute + Fallen-Stellen (mehr Risiko)", true, true);
    }
}

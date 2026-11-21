package game;

import character.CharacterClass;
import character.Player;
import character.Spell;
import combat.Enemy;
import dice.Dice;
import story.Flavour;

/**
 * Rundenbasierter Kampf. Treffer: D20 + Bonus gegen die Ruestungsklasse (1 verfehlt immer, 20 trifft immer).
 * Fliehen ist nicht moeglich. Trank und Zauber kosten den Zug.
 */
class Combat {

    private static final String LINE = "--------------------------------------------------";

    private final ConsoleIO io;
    private final Dice dice;
    private final Player player;
    private final Enemy enemy;
    private final PlayerActions actions;

    private final Flavour flavour;

    private int enemyHp;
    private int shieldBonus;
    private boolean ambushReady;

    Combat(ConsoleIO io, Dice dice, Player player, Enemy enemy, PlayerActions actions) {
        this(io, dice, player, enemy, actions, new Flavour());
    }

    Combat(ConsoleIO io, Dice dice, Player player, Enemy enemy, PlayerActions actions, Flavour flavour) {
        this.io = io;
        this.dice = dice;
        this.player = player;
        this.enemy = enemy;
        this.actions = actions;
        this.flavour = flavour;
        this.enemyHp = enemy.maxHp();
    }

    private String enemyKey() {
        return switch (enemy.name()) {
            case "Skelett" -> "skeleton";
            case "Gargoyle" -> "gargoyle";
            case "Der tote Koenig" -> "king";
            default -> "default";
        };
    }

    private String classKey() {
        return player.getCharacterClass() == CharacterClass.MAGE ? "mage" : "rogue";
    }

    /**
     * Kaempft bis einer faellt.
     *
     * @param playerFirst ob der Spieler den ersten Zug hat
     * @param ambush      ob der erste Treffer doppelten Schaden macht (Schurke nach gelungenem Schleichen)
     * @return true, wenn der Spieler gewonnen hat
     */
    boolean run(boolean playerFirst, boolean ambush) {
        this.ambushReady = ambush;
        io.report("Der Kampf gegen " + enemy.name() + " beginnt!");

        boolean playerTurn = playerFirst;
        while (player.isAlive() && enemyHp > 0) {
            if (playerTurn) {
                playerTurn();
            } else {
                enemyTurn();
            }
            playerTurn = !playerTurn;
        }

        if (player.isAlive()) {
            io.report(flavour.pick("combat.win." + enemyKey()));
            io.report("Du hast " + enemy.name() + " besiegt!");
            return true;
        }
        io.report(flavour.pick("combat.lose"));
        io.report(enemy.name() + " hat dich besiegt.");
        return false;
    }

    // --- Zug des Spielers ---

    private void playerTurn() {
        while (true) {
            // Der Bildschirm wird geleert, ganz oben stehen die Ergebnisse der letzten Zuege
            io.clearScreen();
            io.println(LINE);
            io.println("KAMPF: " + enemy.name());
            io.println(LINE);
            for (String line : actions.statusLines()) {
                io.println(line);
            }
            io.println("Gegner: " + enemy.name() + " | HP " + enemyHp + "/" + enemy.maxHp()
                    + " | RK " + enemy.armorClass());
            if (shieldBonus > 0) {
                io.println("Schutzschild aktiv (+" + shieldBonus + " Ruestungsklasse).");
            }
            io.println();
            io.println("Dein Zug:");
            io.println("[1] Angreifen");
            int next = 2;
            int potionNumber = -1;
            int spellNumber = -1;
            if (actions.canDrinkAnything()) {
                potionNumber = next++;
                io.println("[" + potionNumber + "] Trank trinken");
            }
            if (player.hasSpells()) {
                spellNumber = next++;
                io.println("[" + spellNumber + "] Zauber wirken");
            }
            int choice = io.askChoice("Deine Wahl: ", 1, next - 1);

            if (choice == 1) {
                attack();
                return;
            }
            if (choice == potionNumber) {
                if (actions.drinkPotion()) {
                    return;
                }
            } else if (choice == spellNumber) {
                Spell spell = actions.chooseSpell(true);
                if (spell != null) {
                    cast(spell);
                    return;
                }
            }
        }
    }

    private void attack() {
        int roll = dice.rollD20();
        int bonus = player.getAttackBonus();
        boolean hit = hits(roll, bonus, enemy.armorClass());
        io.report(flavour.pick((hit ? "combat.hit." : "combat.miss.") + classKey()));
        io.report("Dein Angriff: Wurf " + roll + " + " + bonus + " = " + (roll + bonus)
                + " gegen RK " + enemy.armorClass() + " -> " + (hit ? "TREFFER" : "VERFEHLT"));
        if (!hit) {
            return;
        }
        int damage = dice.roll(player.getCharacterClass().getDamageDie());
        if (ambushReady) {
            ambushReady = false;
            damage += dice.roll(player.getCharacterClass().getDamageDie());
            io.report(flavour.pick("combat.ambush"));
            io.report("Hinterhalt: doppelter Schaden!");
        }
        damage += player.getDamageBonus();
        damageEnemy(damage);
    }

    private void cast(Spell spell) {
        switch (spell) {
            case FIREBALL -> {
                io.report(flavour.pick("combat.fireball"));
                damageEnemy(dice.rollD10());
            }
            case SHIELD -> {
                shieldBonus += Spell.SHIELD_BONUS;
                io.report(flavour.pick("combat.shield"));
                io.report("Dein Schutzschild gibt dir +" + Spell.SHIELD_BONUS + " Ruestungsklasse.");
            }
            default -> throw new IllegalStateException("Spell not usable in combat: " + spell);
        }
    }

    private void damageEnemy(int damage) {
        enemyHp = Math.max(0, enemyHp - damage);
        io.report(enemy.name() + " nimmt " + damage + " Schaden.");
    }

    // --- Zug des Gegners ---

    private void enemyTurn() {
        int roll = dice.rollD20();
        int armorClass = player.getArmorClass() + shieldBonus;
        boolean hit = hits(roll, enemy.attackBonus(), armorClass);
        io.report(flavour.pick(hit ? "combat.enemyhit" : "combat.enemymiss", "{enemy}", enemy.name()));
        io.report(enemy.name() + " greift an: Wurf " + roll + " + " + enemy.attackBonus() + " = "
                + (roll + enemy.attackBonus()) + " gegen deine RK " + armorClass
                + " -> " + (hit ? "TREFFER" : "VERFEHLT"));
        if (!hit) {
            return;
        }
        int damage = dice.roll(enemy.damageDie());
        player.takeDamage(damage);
        io.report("Du nimmst " + damage + " Schaden.");
    }

    /** Eine 1 verfehlt immer, eine 20 trifft immer, sonst zaehlt Wurf + Bonus gegen die Ruestungsklasse. */
    private static boolean hits(int roll, int bonus, int armorClass) {
        return roll == 20 || (roll != 1 && roll + bonus >= armorClass);
    }
}

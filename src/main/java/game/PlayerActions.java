package game;

import character.Player;
import character.Spell;
import item.Inventory;
import item.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import story.Flavour;

/** Aktionen des Spielers, die in jedem Auswahlmenue vorkommen: Trank trinken und Zauber wirken. */
class PlayerActions {

    private final ConsoleIO io;
    private final Player player;
    private final Flavour flavour;

    PlayerActions(ConsoleIO io, Player player) {
        this(io, player, new Flavour());
    }

    PlayerActions(ConsoleIO io, Player player, Flavour flavour) {
        this.io = io;
        this.player = player;
        this.flavour = flavour;
    }

    boolean canDrinkAnything() {
        return Arrays.stream(Item.values()).anyMatch(player::canDrink);
    }

    /** Untermenue zum Trank trinken. Gibt true zurueck, wenn ein Trank getrunken wurde. */
    boolean drinkPotion() {
        List<Item> options = Arrays.stream(Item.values()).filter(player::canDrink).toList();
        if (options.isEmpty()) {
            io.report("Du hast keinen Trank, den du trinken kannst.");
            return false;
        }
        io.println();
        io.println("Welchen Trank trinkst du?");
        for (int i = 0; i < options.size(); i++) {
            io.println(Ansi.number(i + 1) + " " + options.get(i).getDisplayName()
                    + " - " + options.get(i).getDescription());
        }
        io.println(Ansi.number(options.size() + 1) + " Zurueck");
        int choice = io.askChoice("Deine Wahl: ", 1, options.size() + 1);
        if (choice > options.size()) {
            return false;
        }
        Item potion = options.get(choice - 1);
        int hpBefore = player.getCurrentHp();
        player.drink(potion);
        if (potion == Item.HEALING_POTION) {
            io.report(flavour.pick("potion.heal"));
            io.report(Ansi.gold("Du trinkst den Heiltrank und heilst " + (player.getCurrentHp() - hpBefore)
                    + " Lebenspunkte."));
        } else {
            io.report(flavour.pick("potion.charge"));
            io.report(Ansi.blue("Du trinkst den Ladungstrank. Deine Zauberladungen sind wieder voll."));
        }
        return true;
    }

    /**
     * Untermenue zum Zauber wirken. Verbraucht die Ladung und gibt den gewaehlten Zauber zurueck,
     * oder null, wenn man zurueckgeht oder keine Ladung mehr hat.
     */
    Spell chooseSpell(boolean inCombat) {
        if (player.getSpellCharges() <= 0) {
            io.report("Du hast keine Zauberladungen mehr.");
            return null;
        }
        List<Spell> options = Arrays.stream(Spell.values())
                .filter(spell -> inCombat ? spell.isUsableInCombat() : spell.isUsableOutsideCombat())
                .toList();
        io.println();
        io.println("Welchen Zauber wirkst du? (Ladungen: " + player.getSpellCharges() + ")");
        for (int i = 0; i < options.size(); i++) {
            io.println(Ansi.number(i + 1) + " " + Ansi.blue(options.get(i).getDisplayName())
                    + " - " + options.get(i).getDescription());
        }
        io.println(Ansi.number(options.size() + 1) + " Zurueck");
        int choice = io.askChoice("Deine Wahl: ", 1, options.size() + 1);
        if (choice > options.size()) {
            return null;
        }
        player.useSpellCharge();
        return options.get(choice - 1);
    }

    /** Statuszeilen fuer jedes Menue: Lebenspunkte, Ruestungsklasse, Ladungen und Items. */
    List<String> statusLines() {
        List<String> lines = new ArrayList<>();
        StringBuilder stats = new StringBuilder();
        stats.append(player.getName()).append(" der ").append(player.getCharacterClass().getDisplayName());
        stats.append(" | HP ").append(player.getCurrentHp()).append("/").append(player.getMaxHp());
        stats.append(" | RK ").append(Ansi.cyan(String.valueOf(player.getArmorClass())));
        if (player.hasSpells()) {
            stats.append(" | Ladungen ").append(Ansi.blue(player.getSpellCharges() + "/"
                    + player.getCharacterClass().getSpellCharges()));
        }
        lines.add(stats.toString());
        String items = player.getItems().isEmpty() ? "leer"
                : String.join(", ", player.getItems().stream().map(Item::getDisplayName).toList());
        lines.add("Inventar (" + player.getItems().size() + "/" + Inventory.CAPACITY + "): " + items);
        return lines;
    }
}

package game;

import character.Attribute;
import character.CharacterClass;
import character.Player;
import character.Spell;
import check.CheckResult;
import check.Difficulty;
import check.SkillCheck;
import combat.Enemy;
import dice.Dice;
import dungeon.Dungeon;
import dungeon.EnemyKind;
import dungeon.LootSpot;
import dungeon.Room;
import dungeon.RoomKind;
import dungeon.WayOption;
import item.Inventory;
import item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import story.StoryText;

/** Ein laufendes Spiel: fuehrt den Spieler Raum fuer Raum durch den Dungeon. */
public class Game {

    enum RoomResult {
        NEXT,
        SAVED,
        DEAD,
        WON
    }

    private static final String LINE = "==================================================";

    private final ConsoleIO io;
    private final Dice dice;
    private final Player player;
    private final Dungeon dungeon;
    private final PlayerActions actions;
    private final boolean showIntro;
    private int roomIndex;
    private int roomsReached;
    private RoomProgress progress;
    private boolean arcaneSight;

    /** Neues Spiel: beginnt mit der Einleitung in Raum 0. */
    public Game(ConsoleIO io, Dice dice, Player player, Dungeon dungeon) {
        this(io, dice, player, dungeon, 0, new RoomProgress(), false, true);
    }

    Game(ConsoleIO io, Dice dice, Player player, Dungeon dungeon, int roomIndex,
         RoomProgress progress, boolean arcaneSight, boolean showIntro) {
        this.io = io;
        this.dice = dice;
        this.player = player;
        this.dungeon = dungeon;
        this.actions = new PlayerActions(io, player);
        this.roomIndex = roomIndex;
        this.roomsReached = roomIndex;
        this.progress = progress;
        this.arcaneSight = arcaneSight;
        this.showIntro = showIntro;
    }

    public GameResult run() {
        if (showIntro) {
            showIntro();
        }
        while (true) {
            RoomResult result = playRoom();
            switch (result) {
                case NEXT -> {
                    roomIndex++;
                    progress = new RoomProgress();
                }
                case SAVED -> {
                    return new GameResult(GameOutcome.SAVED, roomsReached);
                }
                case DEAD -> {
                    return new GameResult(GameOutcome.DEATH, roomsReached);
                }
                case WON -> {
                    return new GameResult(GameOutcome.VICTORY, roomsReached);
                }
            }
        }
    }

    // --- Raeume ---

    private void showIntro() {
        io.clearScreen();
        io.println(LINE);
        for (String line : StoryText.load("intro_" + player.getCharacterClass().getArtName())) {
            io.println(line);
        }
        io.println();
        io.readLine("Weiter mit Enter...");
    }

    RoomResult playRoom() {
        Room room = dungeon.room(roomIndex);
        roomsReached = Math.max(roomsReached, roomIndex);
        showRoom(room);
        if (room.kind() == RoomKind.BOSS) {
            return playBossRoom(room);
        }
        if (room.enemy() == EnemyKind.ORDINARY && !progress.ordinaryDefeated) {
            if (!encounter(Enemy.skeleton(), room.encounterTarget(), Difficulty.MEDIUM)) {
                return RoomResult.DEAD;
            }
            progress.ordinaryDefeated = true;
        }
        return roomMenu(room);
    }

    private void showRoom(Room room) {
        String title;
        String textName;
        switch (room.kind()) {
            case ENTRANCE -> {
                title = "RAUM 0 - Vor dem Tor";
                textName = "room0";
            }
            case BOSS -> {
                title = "RAUM " + room.index() + " - Der Thronsaal";
                textName = "room5";
            }
            default -> {
                title = "RAUM " + room.index() + " - " + room.template().getDisplayName();
                textName = room.template().getTextName();
            }
        }
        io.clearScreen();
        io.println(LINE);
        io.println(title);
        io.println(LINE);
        for (String line : StoryText.load(textName)) {
            io.println(line);
        }
    }

    private RoomResult roomMenu(Room room) {
        while (true) {
            List<String> labels = new ArrayList<>();
            List<Supplier<RoomResult>> handlers = new ArrayList<>();

            LootSpot loot = lootOf(room);
            if (loot != null && !progress.lootTried) {
                labels.add(loot.label() + " (" + describe(loot.attribute(), loot.difficulty()) + ")");
                handlers.add(() -> {
                    tryLoot(loot);
                    return null;
                });
            }
            if (room.enemy() == EnemyKind.SPECIAL && !progress.specialDone) {
                labels.add("Die Gargoyle-Statue untersuchen (" + describe(Attribute.INTELLIGENCE,
                        Difficulty.MEDIUM) + ")");
                handlers.add(() -> examineStatue(room));
            }
            if (room.kind() == RoomKind.ENTRANCE) {
                labels.add("Das Gitter aufstossen und die Gruft betreten");
                handlers.add(() -> RoomResult.NEXT);
            } else {
                for (WayOption way : room.wayOptions()) {
                    labels.add(way.approach().label() + " ("
                            + describe(way.approach().attribute(), way.difficulty()) + ")");
                    handlers.add(() -> attemptWay(way));
                }
                if (player.hasItem(Item.IRON_KEY)) {
                    labels.add("Den Eisernen Schluessel benutzen (kein Wurf noetig)");
                    handlers.add(this::useKey);
                }
            }

            int index = chooseAction("Was tust du?", labels);
            RoomResult result = handlers.get(index).get();
            if (result != null) {
                return result;
            }
        }
    }

    private LootSpot lootOf(Room room) {
        return switch (room.kind()) {
            case ENTRANCE -> dungeon.startHelp(player.getCharacterClass());
            case NORMAL -> room.loot();
            case BOSS -> null;
        };
    }

    private String describe(Attribute attribute, Difficulty difficulty) {
        return attribute.getDisplayName() + ", " + difficulty.getDisplayName();
    }

    private RoomResult playBossRoom(Room room) {
        io.println();
        io.println("Am Eingang der Halle steht ein kleiner Trank, als haette ihn jemand fuer dich bereitgestellt.");
        receiveItem(Item.HEALING_POTION);
        boolean won = encounter(Enemy.deadKing(), room.encounterTarget(), Difficulty.HARD);
        return won ? RoomResult.WON : RoomResult.DEAD;
    }

    // --- Checks, Beute und Fallen ---

    /** Wuerfelt einen Check. Arkane Sicht gibt einmalig +3 auf einen Intelligenz-Check. */
    CheckResult check(Attribute attribute, int target) {
        int bonus = player.getAttribute(attribute);
        String label = attribute.getDisplayName();
        if (attribute == Attribute.INTELLIGENCE && arcaneSight) {
            bonus += Spell.ARCANE_SIGHT_BONUS;
            label += " + Arkane Sicht";
            arcaneSight = false;
        }
        CheckResult result = SkillCheck.roll(dice, bonus, target);
        io.println(result.describe(label));
        return result;
    }

    void tryLoot(LootSpot spot) {
        progress.lootTried = true;
        io.println();
        CheckResult result = check(spot.attribute(), spot.target());
        if (!result.success()) {
            io.println("Es gelingt dir nicht. Zum Glueck passiert nichts.");
            return;
        }
        if (spot.item() == null) {
            io.println("Das Versteck ist leer. Jemand war vor dir hier.");
            return;
        }
        receiveItem(spot.item());
    }

    /** Nimmt ein Item auf. Ist das Inventar voll, entscheidet der Spieler, was weg soll. */
    void receiveItem(Item item) {
        io.println("Du findest: " + item.getDisplayName() + " (" + item.getDescription() + ")");
        if (player.addItem(item)) {
            io.println(item.getDisplayName() + " kommt in dein Inventar.");
            return;
        }
        io.println("Dein Inventar ist voll (" + Inventory.CAPACITY + "/" + Inventory.CAPACITY
                + "). Was soll weg?");
        List<Item> items = List.copyOf(player.getItems());
        for (int i = 0; i < items.size(); i++) {
            io.println("[" + (i + 1) + "] " + items.get(i).getDisplayName() + " wegwerfen");
        }
        int leave = items.size() + 1;
        io.println("[" + leave + "] " + item.getDisplayName() + " liegen lassen");
        int choice = io.askChoice("Deine Wahl: ", 1, leave);
        if (choice == leave) {
            io.println("Du laesst " + item.getDisplayName() + " liegen.");
            return;
        }
        Item dropped = items.get(choice - 1);
        player.removeItem(dropped);
        player.addItem(item);
        io.println("Du wirfst " + dropped.getDisplayName() + " weg und nimmst " + item.getDisplayName() + ".");
    }

    RoomResult attemptWay(WayOption way) {
        io.println();
        CheckResult result = check(way.approach().attribute(), way.target());
        if (result.success()) {
            io.println("Du kommst durch.");
            return RoomResult.NEXT;
        }
        return triggerTrap(result.isNaturalOne());
    }

    /** Eine Falle: bei einem Patzer (1) toedlich, sonst 2xD6 Schaden. */
    RoomResult triggerTrap(boolean deadly) {
        io.println("Eine Falle schnappt zu!");
        if (deadly) {
            io.println("Du bist voellig unvorbereitet. Die Falle ist toedlich.");
            player.takeDamage(player.getCurrentHp());
            return RoomResult.DEAD;
        }
        int damage = dice.rollD6() + dice.rollD6();
        player.takeDamage(damage);
        io.println("Du nimmst " + damage + " Schaden (2xD6).");
        if (!player.isAlive()) {
            io.println("Die Verletzungen sind zu schwer.");
            return RoomResult.DEAD;
        }
        io.println("Verletzt kaempfst du dich weiter.");
        return RoomResult.NEXT;
    }

    RoomResult useKey() {
        player.removeItem(Item.IRON_KEY);
        io.println("Der Schluessel dreht sich knirschend im Schloss und zerbricht.");
        return RoomResult.NEXT;
    }

    // --- Gegner ---

    private RoomResult examineStatue(Room room) {
        progress.specialDone = true;
        io.println();
        CheckResult result = check(Attribute.INTELLIGENCE, room.encounterTarget());
        boolean playerFirst;
        if (result.success()) {
            io.println("Du erkennst die Zeichen auf dem Sockel und weisst, wann die Statue erwacht.");
            playerFirst = true;
        } else {
            io.println("Die Statue erwacht, kaum dass du sie beruehrst!");
            playerFirst = coinToss();
        }
        if (!fight(Enemy.gargoyle(), playerFirst, false)) {
            return RoomResult.DEAD;
        }
        io.println("Die Gargoyle zerfaellt zu Geroell.");
        receiveItem(room.enemyItem());
        return null;
    }

    /** Begegnung mit einem Gegner: Schleichen oder Spaehen verschaffen den ersten Zug, sonst Muenzwurf. */
    private boolean encounter(Enemy enemy, int target, Difficulty difficulty) {
        io.println();
        io.println("Ein Gegner: " + enemy.name() + "!");
        String level = difficulty.getDisplayName();
        List<String> labels = List.of(
                "Schleichen (Geschick, " + level + ")",
                "Spaehen (Intelligenz, " + level + ")",
                "Sofort angreifen (Muenzwurf entscheidet, wer beginnt)");
        int choice = chooseAction("Wie gehst du vor?", labels);

        boolean playerFirst;
        boolean ambush = false;
        if (choice == 2) {
            playerFirst = coinToss();
        } else {
            boolean sneaking = choice == 0;
            Attribute attribute = sneaking ? Attribute.DEXTERITY : Attribute.INTELLIGENCE;
            if (check(attribute, target).success()) {
                playerFirst = true;
                if (sneaking && player.getCharacterClass() == CharacterClass.ROGUE) {
                    ambush = true;
                    io.println("Du schleichst dich von hinten an. Dein erster Treffer wird doppelten Schaden machen.");
                } else {
                    io.println("Unbemerkt gewinnst du den ersten Zug.");
                }
            } else {
                io.println("Du wirst bemerkt.");
                playerFirst = coinToss();
            }
        }
        return fight(enemy, playerFirst, ambush);
    }

    boolean coinToss() {
        boolean playerFirst = dice.roll(2) == 1;
        io.println("Muenzwurf: " + (playerFirst ? "Du beginnst." : "Der Gegner beginnt."));
        return playerFirst;
    }

    boolean fight(Enemy enemy, boolean playerFirst, boolean ambush) {
        return new Combat(io, dice, player, enemy, actions).run(playerFirst, ambush);
    }

    // --- Menue mit Trank und Zauber ---

    /** Zeigt ein Auswahlmenue. Trank trinken und Zauber wirken stehen in jedem Menue zusaetzlich zur Wahl. */
    int chooseAction(String title, List<String> labels) {
        while (true) {
            io.println();
            for (String line : actions.statusLines()) {
                io.println(line);
            }
            if (arcaneSight) {
                io.println("Arkane Sicht ist aktiv (+3 auf deinen naechsten Intelligenz-Check).");
            }
            io.println();
            io.println(title);
            for (int i = 0; i < labels.size(); i++) {
                io.println("[" + (i + 1) + "] " + labels.get(i));
            }
            int next = labels.size() + 1;
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
            if (choice <= labels.size()) {
                return choice - 1;
            }
            if (choice == potionNumber) {
                actions.drinkPotion();
            } else if (choice == spellNumber) {
                castExplorationSpell();
            }
        }
    }

    private void castExplorationSpell() {
        if (arcaneSight) {
            io.println("Arkane Sicht wirkt bereits.");
            return;
        }
        Spell spell = actions.chooseSpell(false);
        if (spell == Spell.ARCANE_SIGHT) {
            arcaneSight = true;
            io.println("Die Runen leuchten auf. Dein naechster Intelligenz-Check bekommt +"
                    + Spell.ARCANE_SIGHT_BONUS + ".");
        }
    }

    // --- Zugriff fuer Speichern, Tests ---

    int getRoomIndex() {
        return roomIndex;
    }

    int getRoomsReached() {
        return roomsReached;
    }

    RoomProgress getProgress() {
        return progress;
    }

    boolean isArcaneSight() {
        return arcaneSight;
    }
}

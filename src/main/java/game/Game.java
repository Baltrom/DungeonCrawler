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
import dungeon.DungeonGenerator;
import dungeon.EnemyKind;
import dungeon.Interaction;
import dungeon.InteractionDef;
import dungeon.Room;
import dungeon.RoomKind;
import dungeon.RoomScript;
import dungeon.WayOption;
import item.Inventory;
import item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import save.SaveGame;
import save.SaveManager;
import story.AsciiArt;
import story.Flavour;
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
    private static final int INSIGHT_BONUS = 2;
    private static final int INTERACTION_TRAP_DIE = 4;

    private final ConsoleIO io;
    private final Dice dice;
    private final Player player;
    private final Dungeon dungeon;
    private final PlayerActions actions;
    private final Flavour flavour;
    private final boolean showIntro;
    private final SaveManager saveManager;
    private int roomIndex;
    private int roomsReached;
    private RoomProgress progress;
    private boolean arcaneSight;
    private String header = "";
    private List<String> description = List.of();
    private String artName;
    private boolean showArt;

    /** Neues Spiel: beginnt mit der Einleitung in Raum 0. */
    public Game(ConsoleIO io, Dice dice, Player player, Dungeon dungeon, SaveManager saveManager) {
        this(io, dice, player, dungeon, 0, new RoomProgress(), false, true, saveManager);
    }

    /** Spielstand laden: der Dungeon wird aus dem gespeicherten Seed neu erzeugt, es gibt keine Einleitung mehr. */
    static Game fromSave(ConsoleIO io, Dice dice, SaveGame save, SaveManager saveManager) {
        return new Game(io, dice, save.toPlayer(), DungeonGenerator.generate(save.seed()), save.roomIndex(),
                new RoomProgress(java.util.Set.copyOf(save.doneInteractions()), save.obstacleExamined(),
                        save.specialDone(), save.ordinaryDefeated(), save.insightBonus()),
                save.arcaneSight(), false, saveManager);
    }

    Game(ConsoleIO io, Dice dice, Player player, Dungeon dungeon, int roomIndex,
         RoomProgress progress, boolean arcaneSight, boolean showIntro, SaveManager saveManager) {
        this.io = io;
        this.dice = dice;
        this.player = player;
        this.dungeon = dungeon;
        this.saveManager = saveManager;
        this.flavour = new Flavour();
        this.actions = new PlayerActions(io, player, flavour);
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

    private void showIntro() {
        io.clearScreen();
        io.println(Ansi.gold(LINE));
        for (String line : StoryText.load("intro_" + player.getCharacterClass().getArtName())) {
            io.println(line);
        }
        io.println();
        io.readLine("Weiter mit Enter...");
    }

    // --- Raeume ---

    RoomResult playRoom() {
        Room room = dungeon.room(roomIndex);
        roomsReached = Math.max(roomsReached, roomIndex);
        RoomScript script = room.script();
        header = "RAUM " + room.index() + " - " + script.name();
        description = script.description();
        artName = script.extra().get("art");
        showArt = artName != null; // das Bild erscheint nur beim Betreten des Raums

        if (room.kind() == RoomKind.BOSS) {
            return playBossRoom(room, script);
        }
        if (room.enemy() == EnemyKind.ORDINARY && !progress.ordinaryDefeated) {
            if (!encounter(Enemy.skeleton(), room.encounterTarget(), Difficulty.MEDIUM, "ordinary")) {
                return RoomResult.DEAD;
            }
            progress.ordinaryDefeated = true;
        }
        return roomMenu(room, script);
    }

    private RoomResult roomMenu(Room room, RoomScript script) {
        while (true) {
            List<String> labels = new ArrayList<>();
            List<Supplier<RoomResult>> handlers = new ArrayList<>();

            for (Interaction interaction : availableInteractions(room)) {
                labels.add(labelOf(interaction));
                handlers.add(() -> interact(interaction, room));
            }
            if (room.enemy() == EnemyKind.SPECIAL && !progress.specialDone) {
                labels.add(flavour.pick("statue.label") + " (" + describe(Attribute.INTELLIGENCE,
                        Difficulty.MEDIUM) + ")");
                handlers.add(() -> examineStatue(room));
            }
            if (!room.blocked()) {
                labels.add(script.freeLabel());
                handlers.add(() -> walkOn(script));
            } else if (!progress.obstacleExamined) {
                labels.add(script.obstacleLabel());
                handlers.add(() -> examineObstacle(script));
            } else {
                for (WayOption way : room.wayOptions()) {
                    labels.add(way.def().label() + " (" + describe(way.def().attribute(), way.difficulty()) + ")");
                    handlers.add(() -> attemptWay(way));
                }
                if (player.hasItem(Item.IRON_KEY)) {
                    labels.add("Den Eisernen Schluessel benutzen (kein Wurf noetig)");
                    handlers.add(this::useKey);
                }
            }
            labels.add("Speichern und zum Hauptmenue");
            handlers.add(this::saveAndExit);

            int index = chooseAction("Was tust du?", labels);
            RoomResult result = handlers.get(index).get();
            if (result != null) {
                return result;
            }
        }
    }

    /** Die Interaktionen, die man gerade waehlen kann. Eine Folge-Interaktion erscheint nach ihrer Vorgaengerin. */
    List<Interaction> availableInteractions(Room room) {
        List<Interaction> available = new ArrayList<>();
        for (Interaction interaction : room.interactions()) {
            if (!appliesToPlayer(interaction.def())) {
                continue;
            }
            if (!progress.done.contains(interaction.id())) {
                available.add(interaction);
            } else if (interaction.follow() != null && !progress.done.contains(interaction.follow().id())) {
                available.add(interaction.follow());
            }
        }
        return available;
    }

    private boolean appliesToPlayer(InteractionDef def) {
        return def.onlyFor() == null || def.onlyFor() == player.getCharacterClass();
    }

    private String labelOf(Interaction interaction) {
        InteractionDef def = interaction.def();
        if (def.attribute() == null) {
            return def.label();
        }
        return def.label() + " (" + describe(def.attribute(), interaction.difficulty()) + ")";
    }

    private String describe(Attribute attribute, Difficulty difficulty) {
        return attribute.getDisplayName() + ", " + difficulty.getDisplayName();
    }

    private RoomResult playBossRoom(Room room, RoomScript script) {
        io.report(script.extra().get("potion.text"));
        receiveItem(Item.HEALING_POTION);
        boolean won = encounter(Enemy.deadKing(), room.encounterTarget(), Difficulty.HARD, "boss");
        return won ? RoomResult.WON : RoomResult.DEAD;
    }

    // --- Untersuchen, Erkenntnis und Hinweise ---

    /** Untersuchen, durchsuchen, oeffnen: Stimmung immer, dann ggf. ein Check mit Erfolg oder Fehlschlag. */
    RoomResult interact(Interaction interaction, Room room) {
        InteractionDef def = interaction.def();
        progress.done.add(def.id());
        io.report(def.text());

        if (def.attribute() == null) {
            if (def.successText() != null) {
                io.report(def.successText());
            }
            applyEffect(interaction, room);
            return null;
        }
        CheckResult result = check(def.attribute(), interaction.target());
        if (result.success()) {
            io.report(def.successText());
            applyEffect(interaction, room);
            return null;
        }
        io.report(def.failureText());
        if (def.failureDamage()) {
            int damage = dice.roll(INTERACTION_TRAP_DIE);
            player.takeDamage(damage);
            io.report(Ansi.red("Du nimmst " + damage + " Schaden (1W" + INTERACTION_TRAP_DIE + ")."));
            if (!player.isAlive()) {
                io.report(flavour.pick("trap.dead"));
                return RoomResult.DEAD;
            }
        }
        return null;
    }

    private void applyEffect(Interaction interaction, Room room) {
        switch (interaction.def().effect()) {
            case ITEM -> {
                if (interaction.item() == null) {
                    io.report(flavour.pick("loot.empty"));
                } else {
                    receiveItem(interaction.item());
                }
            }
            case HINT -> io.report(hintAbout(room));
            case INSIGHT -> {
                progress.insightBonus = INSIGHT_BONUS;
                io.report("Erkenntnis: Dein naechster Check in diesem Raum bekommt +" + INSIGHT_BONUS + ".");
            }
            case NONE -> {
            }
        }
    }

    /** Ein Hinweis auf den naechsten Raum: was dort wartet und ob der Durchgang versperrt ist. */
    String hintAbout(Room room) {
        Room next = dungeon.room(room.index() + 1);
        if (next.kind() == RoomKind.BOSS) {
            return flavour.pick("hint.boss");
        }
        String content = switch (next.enemy()) {
            case ORDINARY -> flavour.pick("hint.ordinary");
            case SPECIAL -> flavour.pick("hint.special");
            default -> flavour.pick("hint.room", "{room}", next.script().name());
        };
        return content + " " + flavour.pick(next.blocked() ? "hint.blocked" : "hint.free");
    }

    private RoomResult examineObstacle(RoomScript script) {
        progress.obstacleExamined = true;
        io.report(script.obstacleText());
        return null;
    }

    private RoomResult walkOn(RoomScript script) {
        io.report(script.freeText());
        return RoomResult.NEXT;
    }

    // --- Checks, Funde und Fallen ---

    /** Wuerfelt einen Check. Arkane Sicht und Erkenntnis geben einmalig einen Bonus. */
    CheckResult check(Attribute attribute, int target) {
        int bonus = player.getAttribute(attribute);
        String label = attribute.getDisplayName();
        if (attribute == Attribute.INTELLIGENCE && arcaneSight) {
            bonus += Spell.ARCANE_SIGHT_BONUS;
            label += " + Arkane Sicht";
            arcaneSight = false;
        }
        if (progress.insightBonus > 0) {
            bonus += progress.insightBonus;
            label += " + Erkenntnis";
            progress.insightBonus = 0;
        }
        CheckResult result = SkillCheck.roll(dice, bonus, target);
        String line = result.describe(label);
        io.report(result.success() ? Ansi.green(line) : line);
        return result;
    }

    /** Nimmt ein Item auf. Ist das Inventar voll, entscheidet der Spieler, was weg soll. */
    void receiveItem(Item item) {
        io.report(flavour.pick("item.found." + item.name()));
        io.report(Ansi.gold("Du findest: " + item.getDisplayName() + " (" + item.getDescription() + ")"));
        if (player.addItem(item)) {
            io.report(item.getDisplayName() + " kommt in dein Inventar.");
            return;
        }
        io.println("Dein Inventar ist voll (" + Inventory.CAPACITY + "/" + Inventory.CAPACITY
                + "). Was soll weg?");
        List<Item> items = List.copyOf(player.getItems());
        for (int i = 0; i < items.size(); i++) {
            io.println(Ansi.number(i + 1) + " " + items.get(i).getDisplayName() + " wegwerfen");
        }
        int leave = items.size() + 1;
        io.println(Ansi.number(leave) + " " + item.getDisplayName() + " liegen lassen");
        int choice = io.askChoice("Deine Wahl: ", 1, leave);
        if (choice == leave) {
            io.report("Du laesst " + item.getDisplayName() + " liegen.");
            return;
        }
        Item dropped = items.get(choice - 1);
        player.removeItem(dropped);
        player.addItem(item);
        io.report("Du wirfst " + dropped.getDisplayName() + " weg und nimmst " + item.getDisplayName() + ".");
    }

    RoomResult attemptWay(WayOption way) {
        CheckResult result = check(way.def().attribute(), way.target());
        if (result.success()) {
            io.report(way.def().successText());
            return RoomResult.NEXT;
        }
        io.report(way.def().failureText());
        return triggerTrap(result.isNaturalOne());
    }

    /** Eine Falle: 1W6 Schaden, bei einem Patzer (1) doppelt so viel (2W6). */
    RoomResult triggerTrap(boolean fumble) {
        int diceCount = fumble ? 2 : 1;
        int damage = 0;
        for (int i = 0; i < diceCount; i++) {
            damage += dice.rollD6();
        }
        player.takeDamage(damage);
        io.report(flavour.pick(fumble ? "trap.fumble" : "trap.hit"));
        io.report(Ansi.red("Du nimmst " + damage + " Schaden (" + diceCount + "W6)."));
        if (!player.isAlive()) {
            io.report(flavour.pick("trap.dead"));
            return RoomResult.DEAD;
        }
        io.report(flavour.pick("trap.survive"));
        return RoomResult.NEXT;
    }

    RoomResult useKey() {
        player.removeItem(Item.IRON_KEY);
        io.report(flavour.pick("key.use"));
        return RoomResult.NEXT;
    }

    // --- Gegner ---

    private RoomResult examineStatue(Room room) {
        progress.specialDone = true;
        io.report(flavour.pick("statue.look"));
        CheckResult result = check(Attribute.INTELLIGENCE, room.encounterTarget());
        boolean playerFirst;
        if (result.success()) {
            io.report(flavour.pick("statue.success"));
            playerFirst = true;
        } else {
            io.report(flavour.pick("statue.fail"));
            playerFirst = coinToss();
        }
        if (!fight(Enemy.gargoyle(), playerFirst, false)) {
            return RoomResult.DEAD;
        }
        io.report(flavour.pick("statue.defeated"));
        receiveItem(room.enemyItem());
        return null;
    }

    /** Begegnung mit einem Gegner: Schleichen oder Spaehen verschaffen den ersten Zug, sonst Muenzwurf. */
    private boolean encounter(Enemy enemy, int target, Difficulty difficulty, String kind) {
        String level = difficulty.getDisplayName();
        List<String> labels = List.of(
                "Schleichen (Geschick, " + level + ")",
                "Spaehen (Intelligenz, " + level + ")",
                "Sofort angreifen (Muenzwurf entscheidet, wer beginnt)");
        String scene = flavour.pick("encounter." + kind, "{enemy}", enemy.name());
        int choice = chooseAction("Wie gehst du vor?", labels, List.of("", scene));

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
                    io.report(flavour.pick("encounter.sneak.rogue"));
                    io.report("Dein erster Treffer macht doppelten Schaden.");
                } else if (sneaking) {
                    io.report(flavour.pick("encounter.sneak.other"));
                    io.report("Du gewinnst den ersten Zug.");
                } else {
                    io.report(flavour.pick("encounter.spy"));
                    io.report("Du gewinnst den ersten Zug.");
                }
            } else {
                io.report(flavour.pick("encounter.noticed"));
                playerFirst = coinToss();
            }
        }
        return fight(enemy, playerFirst, ambush);
    }

    boolean coinToss() {
        boolean playerFirst = dice.roll(2) == 1;
        io.report("Muenzwurf: " + (playerFirst ? "Du beginnst." : "Der Gegner beginnt."));
        return playerFirst;
    }

    boolean fight(Enemy enemy, boolean playerFirst, boolean ambush) {
        return new Combat(io, dice, player, enemy, actions, flavour).run(playerFirst, ambush);
    }

    // --- Menue mit Trank und Zauber ---

    /** Zeigt ein Auswahlmenue. Trank trinken und Zauber wirken stehen in jedem Menue zusaetzlich zur Wahl. */
    int chooseAction(String title, List<String> labels) {
        return chooseAction(title, labels, List.of());
    }

    /**
     * Jedes Menue beginnt mit geleertem Bildschirm. Zuerst kommt der Raum, danach die Ergebnisse
     * der letzten Aktion, dann der Status und die Auswahl.
     */
    int chooseAction(String title, List<String> labels, List<String> sceneLines) {
        while (true) {
            io.clearScreenOnly();
            io.println(Ansi.gold(LINE));
            io.println(Ansi.gold(header));
            io.println(Ansi.gold(LINE));
            if (showArt) {
                for (String line : ArtPainter.paint(artName, AsciiArt.load(artName))) {
                    io.println(line);
                }
                showArt = false;
            }
            for (String line : description) {
                io.println(Ansi.grey(line));
            }
            for (String line : sceneLines) {
                io.println(line);
            }
            io.println();
            io.showRecentResults();
            for (String line : actions.statusLines()) {
                io.println(line);
            }
            if (arcaneSight) {
                io.println("Arkane Sicht ist aktiv (+3 auf deinen naechsten Intelligenz-Check).");
            }
            if (progress.insightBonus > 0) {
                io.println("Erkenntnis ist aktiv (+" + progress.insightBonus + " auf deinen naechsten Check).");
            }
            io.println();
            io.println(title);
            for (int i = 0; i < labels.size(); i++) {
                io.println(Ansi.number(i + 1) + " " + labels.get(i));
            }
            int next = labels.size() + 1;
            int potionNumber = -1;
            int spellNumber = -1;
            if (actions.canDrinkAnything()) {
                potionNumber = next++;
                io.println(Ansi.number(potionNumber) + " Trank trinken");
            }
            if (player.hasSpells()) {
                spellNumber = next++;
                io.println(Ansi.number(spellNumber) + " " + Ansi.blue("Zauber wirken"));
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
            io.report("Arkane Sicht wirkt bereits.");
            return;
        }
        Spell spell = actions.chooseSpell(false);
        if (spell == Spell.ARCANE_SIGHT) {
            arcaneSight = true;
            io.report(flavour.pick("spell.arcane"));
            io.report(Ansi.blue("Dein naechster Intelligenz-Check bekommt +" + Spell.ARCANE_SIGHT_BONUS + "."));
        }
    }

    // --- Speichern ---

    private RoomResult saveAndExit() {
        saveManager.save(snapshot());
        io.println("Spiel gespeichert.");
        return RoomResult.SAVED;
    }

    SaveGame snapshot() {
        return new SaveGame(dungeon.seed(), player.getName(), player.getCharacterClass(), roomIndex,
                player.getCurrentHp(), player.getSpellCharges(), player.getItems(),
                List.copyOf(progress.done), progress.obstacleExamined, progress.specialDone,
                progress.ordinaryDefeated, progress.insightBonus, arcaneSight);
    }

    // --- Zugriff fuer Tests ---

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

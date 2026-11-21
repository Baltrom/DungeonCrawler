package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.Attribute;
import character.CharacterClass;
import character.Player;
import check.Difficulty;
import dice.Dice;
import dice.FixedDice;
import dice.MaxDice;
import dungeon.Dungeon;
import dungeon.DungeonGenerator;
import dungeon.Effect;
import dungeon.EnemyKind;
import dungeon.Interaction;
import dungeon.InteractionDef;
import dungeon.Room;
import dungeon.RoomKind;
import dungeon.RoomTemplate;
import dungeon.WayDef;
import dungeon.WayOption;
import game.Game.RoomResult;
import item.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import save.SaveGame;
import save.SaveManager;

class GameTest {

    @TempDir
    Path dir;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Dungeon generated = DungeonGenerator.generate(1);

    // --- Hilfen ---

    private SaveManager saves() {
        return new SaveManager(dir.resolve("savegame.txt"));
    }

    private ConsoleIO io(String... lines) {
        String input = String.join("\n", lines) + "\n";
        return new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private Game gameAt(Dungeon dungeon, int room, Dice dice, Player player, ConsoleIO io) {
        return new Game(io, dice, player, dungeon, room, new RoomProgress(), false, false, saves());
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private static InteractionDef def(String id, Attribute attribute, Effect effect, boolean damage) {
        return new InteractionDef(id, "Label " + id, "Text " + id, attribute, false, effect, null, null,
                attribute == null ? null : "Erfolg " + id, attribute == null ? null : "Misserfolg " + id,
                damage, null);
    }

    private static Interaction interaction(String id, Attribute attribute, Effect effect, boolean damage,
                                           int target, Item item) {
        return new Interaction(def(id, attribute, effect, damage),
                attribute == null ? null : Difficulty.MEDIUM, target, item, null);
    }

    /** Ein Raum mit der Skriptvorlage "Die Bibliothek", aber mit frei gewaehlten Inhalten. */
    private static Room room(int index, boolean blocked, List<Interaction> interactions, EnemyKind enemy,
                             Item enemyItem, int encounterTarget) {
        List<WayOption> ways = blocked ? List.of(
                new WayOption(new WayDef("Weg A", Attribute.STRENGTH, false, "Weg A klappt", "Weg A scheitert"),
                        Difficulty.MEDIUM, 14),
                new WayOption(new WayDef("Weg B", Attribute.INTELLIGENCE, true, "Weg B klappt", "Weg B scheitert"),
                        Difficulty.HARD, 16)) : List.of();
        return new Room(index, RoomKind.NORMAL, RoomTemplate.LIBRARY, blocked, ways, interactions, enemy,
                enemyItem, encounterTarget);
    }

    private Dungeon with(Room room) {
        List<Room> rooms = new ArrayList<>(generated.rooms());
        rooms.set(room.index(), room);
        return new Dungeon(generated.seed(), rooms);
    }

    private static List<String> ids(List<Interaction> interactions) {
        return interactions.stream().map(Interaction::id).toList();
    }

    private static Interaction byId(Room room, String id) {
        return room.interactions().stream().filter(i -> i.id().equals(id)).findFirst().orElseThrow();
    }

    // --- Raum 0: Starthilfe und Untersuchen ---

    @Test
    void mageFindsAChargePotionAtTheGate() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Menue: Runen (1), Innehalten (2), Fusspuren (3), Tor (4); nach den Runen: Tor (3)
        Game game = gameAt(generated, 0, new FixedDice(20), mage, io("1", "3"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(mage.hasItem(Item.CHARGE_POTION));
    }

    @Test
    void rogueFindsAProtectionAmuletAtTheGate() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        Game game = gameAt(generated, 0, new FixedDice(20), rogue, io("1", "3"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(rogue.hasItem(Item.PROTECTION_AMULET));
        assertEquals(14, rogue.getArmorClass());
    }

    @Test
    void aFailedInteractionChangesNothingButStillTellsWhatHappened() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(generated, 0, new FixedDice(1), mage, io("1", "3"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(mage.getItems().isEmpty());
        assertEquals(14, mage.getCurrentHp());
        assertTrue(printed().contains("Fach bleibt verschlossen"));
    }

    @Test
    void everyInteractionShowsItsAtmosphereText() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(generated, 0, new FixedDice(20), mage, io("1", "3"));
        game.playRoom();
        assertTrue(printed().contains("In das Gitter sind Zeichen geaetzt"));
        assertTrue(printed().contains("Du verstehst sie"));
    }

    @Test
    void classSpecificInteractionsAreHiddenFromTheOtherClass() {
        Room entrance = generated.room(0);
        Game mage = gameAt(generated, 0, new Dice(), new Player("Ria", CharacterClass.MAGE), io());
        Game rogue = gameAt(generated, 0, new Dice(), new Player("Bob", CharacterClass.ROGUE), io());
        assertEquals(List.of("poi2", "poi3", "poi4"), ids(mage.availableInteractions(entrance)));
        assertEquals(List.of("poi1", "poi3", "poi4"), ids(rogue.availableInteractions(entrance)));
    }

    @Test
    void aFollowUpAppearsAfterTheFirstInteractionAndEachIsDoneOnlyOnce() {
        Room entrance = generated.room(0);
        Game game = gameAt(generated, 0, new FixedDice(20), new Player("Ria", CharacterClass.MAGE), io());
        assertEquals(List.of("poi2", "poi3", "poi4"), ids(game.availableInteractions(entrance)));

        game.interact(byId(entrance, "poi4"), entrance);
        assertEquals(List.of("poi2", "poi3", "poi4.follow"), ids(game.availableInteractions(entrance)));

        game.interact(byId(entrance, "poi4").follow(), entrance);
        assertEquals(List.of("poi2", "poi3"), ids(game.availableInteractions(entrance)));
        assertTrue(printed().contains("Du liest, wohin sie fuehrten"));
    }

    @Test
    void takingAMomentGivesAnInsightThatTheNextCheckUsesUp() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Innehalten (2), Runen (1), Tor (2)
        Game game = gameAt(generated, 0, new FixedDice(20), mage, io("2", "1", "2"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(printed().contains("+ 7 Intelligenz + Erkenntnis"));
        assertEquals(0, game.getProgress().insightBonus);
    }

    // --- Interaktionen im Detail ---

    @Test
    void anInteractionWithoutACheckAlwaysWorksAndGivesItsEffect() {
        Room room = room(1, false, List.of(interaction("a", null, Effect.INSIGHT, false, 0, null)),
                EnemyKind.NONE, null, 0);
        Game game = gameAt(with(room), 1, new Dice(), new Player("Ria", CharacterClass.MAGE), io());
        assertNull(game.interact(room.interactions().get(0), room));
        assertEquals(2, game.getProgress().insightBonus);
        assertTrue(printed().contains("Text a"));
    }

    @Test
    void aSuccessfulItemInteractionGivesTheItem() {
        Room room = room(1, false,
                List.of(interaction("a", Attribute.STRENGTH, Effect.ITEM, false, 8, Item.RING_OF_WISDOM)),
                EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(20), mage, io());
        assertNull(game.interact(room.interactions().get(0), room));
        assertTrue(mage.hasItem(Item.RING_OF_WISDOM));
        assertTrue(printed().contains("Erfolg a"));
    }

    @Test
    void anEmptyHidingPlaceGivesNothing() {
        Room room = room(1, false, List.of(interaction("a", Attribute.STRENGTH, Effect.ITEM, false, 8, null)),
                EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(20), mage, io());
        game.interact(room.interactions().get(0), room);
        assertTrue(mage.getItems().isEmpty());
        assertTrue(printed().contains("leer") || printed().contains("Nichts, nur Staub"));
    }

    @Test
    void aFailedCheckWithASmallTrapCostsOneD4() {
        Room room = room(1, false, List.of(interaction("a", Attribute.STRENGTH, Effect.NONE, true, 14, null)),
                EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(2, 3), mage, io());
        assertNull(game.interact(room.interactions().get(0), room));
        assertEquals(11, mage.getCurrentHp());
        assertTrue(printed().contains("Misserfolg a"));
        assertTrue(printed().contains("Du nimmst 3 Schaden (1W4)."));
    }

    @Test
    void aFailedCheckWithoutATrapCostsNothing() {
        Room room = room(1, false, List.of(interaction("a", Attribute.STRENGTH, Effect.NONE, false, 14, null)),
                EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(2), mage, io());
        assertNull(game.interact(room.interactions().get(0), room));
        assertEquals(14, mage.getCurrentHp());
    }

    @Test
    void aSmallTrapCanStillKillAnAlmostDeadCharacter() {
        Room room = room(1, false, List.of(interaction("a", Attribute.STRENGTH, Effect.NONE, true, 14, null)),
                EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(12);
        Game game = gameAt(with(room), 1, new FixedDice(2, 4), mage, io());
        assertEquals(RoomResult.DEAD, game.interact(room.interactions().get(0), room));
        assertFalse(mage.isAlive());
    }

    @Test
    void anInsightGivesPlusTwoToTheNextCheckOnly() {
        Room room = room(1, false, List.of(interaction("a", null, Effect.INSIGHT, false, 0, null)),
                EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(10, 10), mage, io());
        game.interact(room.interactions().get(0), room);
        // Die Staerke des Magiers ist 1, mit der Erkenntnis +2 = 3, Wurf 10 -> 13
        assertEquals(13, game.check(Attribute.STRENGTH, 5).total());
        assertEquals(0, game.getProgress().insightBonus);
        assertEquals(11, game.check(Attribute.STRENGTH, 5).total());
    }

    // --- Hinweise ---

    @Test
    void theLastRoomBeforeTheBossHintsAtTheBoss() {
        Game game = gameAt(generated, 4, new Dice(), new Player("Ria", CharacterClass.MAGE), io());
        assertTrue(game.hintAbout(generated.room(4)).contains("letzte Raum"));
    }

    @Test
    void aHintTellsAboutAnOrdinaryEnemyAndABlockedPassage() {
        Room next = room(2, true, List.of(), EnemyKind.ORDINARY, null, 11);
        Game game = gameAt(with(next), 1, new Dice(), new Player("Ria", CharacterClass.MAGE), io());
        String hint = game.hintAbout(generated.room(1));
        assertTrue(hint.contains("Gegner") || hint.contains("Totes"), hint);
        assertTrue(hint.contains("versperrt") || hint.contains("bezwingen"), hint);
    }

    @Test
    void aHintTellsAboutAStatue() {
        Room next = room(2, false, List.of(), EnemyKind.SPECIAL, Item.IRON_KEY, 11);
        Game game = gameAt(with(next), 1, new Dice(), new Player("Ria", CharacterClass.MAGE), io());
        assertTrue(game.hintAbout(generated.room(1)).contains("Steinernes"));
    }

    @Test
    void aHintNamesAQuietFreeRoom() {
        Room next = room(2, false, List.of(), EnemyKind.NONE, null, 0);
        Game game = gameAt(with(next), 1, new Dice(), new Player("Ria", CharacterClass.MAGE), io());
        String hint = game.hintAbout(generated.room(1));
        assertTrue(hint.contains("Die Bibliothek"), hint);
        assertTrue(hint.contains("frei") || hint.contains("nicht zu erwarten"), hint);
    }

    // --- Durchgang: frei oder versperrt ---

    @Test
    void aFreeRoomLetsYouWalkOnWithAFlavourText() {
        Room room = room(1, false, List.of(), EnemyKind.NONE, null, 0);
        Game game = gameAt(with(room), 1, new Dice(), new Player("Ria", CharacterClass.MAGE), io("1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(printed().contains(RoomTemplate.LIBRARY.getScript().freeText()));
    }

    @Test
    void aBlockedRoomShowsTheWaysOnlyAfterTheObstacleWasExamined() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        // Hindernis untersuchen (1), dann Weg A (1)
        Game game = gameAt(with(room), 1, new FixedDice(20), new Player("Ria", CharacterClass.MAGE), io("1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());

        String text = printed();
        String obstacleLabel = RoomTemplate.LIBRARY.getScript().obstacleLabel();
        assertTrue(text.indexOf(obstacleLabel) >= 0);
        assertTrue(text.indexOf("[1] Weg A") > text.indexOf(obstacleLabel), "Ways appeared before examining");
        assertTrue(text.contains(RoomTemplate.LIBRARY.getScript().obstacleText()));
        assertTrue(text.contains("Weg A klappt"));
    }

    @Test
    void theIronKeyOpensABlockedWayAfterExaminingAndIsGone() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.IRON_KEY);
        // Hindernis untersuchen (1), dann Weg A (1), Weg B (2), Schluessel (3)
        Game game = gameAt(with(room), 1, new Dice(), mage, io("1", "3"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertFalse(mage.hasItem(Item.IRON_KEY));
        assertTrue(printed().contains("Schluessel"));
    }

    @Test
    void aSuccessfulWayLeadsOnUnharmed() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(20), mage, io());
        assertEquals(RoomResult.NEXT, game.attemptWay(room.wayOptions().get(0)));
        assertEquals(14, mage.getCurrentHp());
        assertTrue(printed().contains("Weg A klappt"));
    }

    @Test
    void aFailedWayTriggersAOneD6TrapAndStillLeadsOn() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(2, 4), mage, io());
        assertEquals(RoomResult.NEXT, game.attemptWay(room.wayOptions().get(0)));
        assertEquals(10, mage.getCurrentHp());
        assertTrue(printed().contains("Weg A scheitert"));
        assertTrue(printed().contains("Du nimmst 4 Schaden (1W6)."));
    }

    @Test
    void aFumbleDoublesTheTrapDamageButIsNotDeadlyForAHealthyCharacter() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(with(room), 1, new FixedDice(1, 3, 5), mage, io());
        assertEquals(RoomResult.NEXT, game.attemptWay(room.wayOptions().get(0)));
        assertEquals(6, mage.getCurrentHp());
        assertTrue(printed().contains("Du nimmst 8 Schaden (2W6)."));
    }

    @Test
    void aFumbleCanStillKillAWeakenedCharacter() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(9);
        Game game = gameAt(with(room), 1, new FixedDice(1, 6, 6), mage, io());
        assertEquals(RoomResult.DEAD, game.attemptWay(room.wayOptions().get(0)));
        assertFalse(mage.isAlive());
    }

    @Test
    void anOrdinaryTrapCanKillAnAlmostDeadCharacter() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(11);
        Game game = gameAt(with(room), 1, new FixedDice(2, 6), mage, io());
        assertEquals(RoomResult.DEAD, game.attemptWay(room.wayOptions().get(0)));
    }

    @Test
    void theIronKeyOpensTheWayAndIsGone() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.IRON_KEY);
        Game game = gameAt(generated, 1, new Dice(), mage, io());
        assertEquals(RoomResult.NEXT, game.useKey());
        assertFalse(mage.hasItem(Item.IRON_KEY));
    }

    // --- Gegner ---

    @Test
    void theStatueCanBeWokenAndGivesAnItemAfterTheFight() {
        Room room = room(1, false, List.of(), EnemyKind.SPECIAL, Item.RING_OF_WISDOM, 11);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Menue: Statue (1), im Kampf zweimal Angriff (1, 1), danach Weitergehen (1).
        // Wuerfe: Intelligenz-Check 20 (Spieler beginnt), Treffer 20 mit 6, Gegner 1, Treffer 20 mit 6
        Game game = gameAt(with(room), 1, new FixedDice(20, 20, 6, 1, 20, 6), mage, io("1", "1", "1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(mage.hasItem(Item.RING_OF_WISDOM));
        assertTrue(printed().contains("Geroell"));
    }

    @Test
    void anOrdinaryEnemyAttacksOnEntering() {
        Room room = room(2, false, List.of(), EnemyKind.ORDINARY, null, 11);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Sofort angreifen (3), Muenzwurf 1 = Spieler beginnt, Treffer 20 mit 6 (Skelett hat 6 Lebenspunkte),
        // Angriff (1), danach Weitergehen (1)
        Game game = gameAt(with(room), 2, new FixedDice(1, 20, 6), mage, io("3", "1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(game.getProgress().ordinaryDefeated);
        assertTrue(printed().contains("Wie gehst du vor?"));
        assertTrue(printed().contains("Skelett"));
    }

    // --- Arkane Sicht ---

    @Test
    void arcaneSightAddsThreeToTheNextIntelligenceCheckOnly() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Menue: Runen (1), Innehalten (2), Fusspuren (3), Tor (4), Speichern (5), Zauber wirken (6)
        Game game = gameAt(generated, 0, new FixedDice(20), mage, io("6", "1", "1", "3"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertEquals(2, mage.getSpellCharges());
        assertTrue(printed().contains("+ 8 Intelligenz + Arkane Sicht"));
        assertFalse(game.isArcaneSight());
    }

    @Test
    void arcaneSightCannotBeStacked() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Zauber wirken (6), Arkane Sicht (1), nochmal Zauber wirken (6), dann Tor (4)
        Game game = gameAt(generated, 0, new Dice(), mage, io("6", "1", "6", "4"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertEquals(2, mage.getSpellCharges());
        assertTrue(printed().contains("wirkt bereits"));
    }

    // --- Anzeige ---

    @Test
    void theResultOfAnActionIsShownBelowTheRoomIntroOnTheNextScreen() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        // Gitter absuchen (1, Wurf 20), danach erscheint das Menue neu, dann Tor (3)
        Game game = gameAt(generated, 0, new FixedDice(20), rogue, io("1", "3"));
        game.playRoom();

        String text = printed();
        int block = text.lastIndexOf("--- Was zuletzt geschah ---");
        assertTrue(block > 0, "The result block is missing");
        // Der Raum steht oben, das Ergebnis darunter
        assertTrue(text.substring(Math.max(0, block - 600), block).contains("Ein verrostetes Gitter"),
                "The room intro must come right before the result block");
        assertTrue(text.indexOf("Schutzamulett kommt in dein Inventar.", block) > block);
    }

    @Test
    void everyScreenShowsTheRoomTitleAndItsAtmosphere() {
        Game game = gameAt(generated, 0, new Dice(), new Player("Ria", CharacterClass.MAGE), io("4"));
        game.playRoom();
        assertTrue(printed().contains("RAUM 0 - Vor dem Tor"));
        assertTrue(printed().contains("Ein verrostetes Gitter versperrt den Eingang"));
    }

    // --- Inventar voll ---

    @Test
    void withAFullInventoryThePlayerChoosesWhatToDrop() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        for (int i = 0; i < 5; i++) {
            player.addItem(Item.IRON_KEY);
        }
        Game game = gameAt(generated, 1, new Dice(), player, io("2"));
        game.receiveItem(Item.RING_OF_WISDOM);
        assertTrue(player.hasItem(Item.RING_OF_WISDOM));
        assertEquals(5, player.getItems().size());
        assertEquals(4, player.getItems().stream().filter(item -> item == Item.IRON_KEY).count());
    }

    @Test
    void thePlayerCanLeaveTheNewItemBehind() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        for (int i = 0; i < 5; i++) {
            player.addItem(Item.IRON_KEY);
        }
        Game game = gameAt(generated, 1, new Dice(), player, io("6"));
        game.receiveItem(Item.RING_OF_WISDOM);
        assertFalse(player.hasItem(Item.RING_OF_WISDOM));
        assertEquals(5, player.getItems().size());
    }

    @Test
    void everyFoundItemComesWithAFlavourText() {
        for (Item item : Item.values()) {
            Player player = new Player("Ria", CharacterClass.MAGE);
            Game game = gameAt(generated, 1, new Dice(), player, io());
            output.reset();
            game.receiveItem(item);
            assertTrue(printed().contains("Du findest: " + item.getDisplayName()));
            assertTrue(printed().lines().count() >= 2, "No flavour text for " + item);
        }
    }

    // --- Speichern und Laden ---

    private int saveNumber(Game game, Room room) {
        // Interaktionen, Statue, Durchgang (frei oder Hindernis untersuchen), dann Speichern
        return game.availableInteractions(room).size() + (room.enemy() == EnemyKind.SPECIAL ? 1 : 0) + 1 + 1;
    }

    @Test
    void savingWritesTheCurrentStateAndEndsTheSession() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(3);
        mage.addItem(Item.IRON_KEY);
        // Raum 0: Runen (1), Innehalten (2), Fusspuren (3), Tor (4), Speichern (5)
        Game game = gameAt(generated, 0, new Dice(), mage, io("5"));
        assertEquals(RoomResult.SAVED, game.playRoom());

        SaveGame save = saves().load().orElseThrow();
        assertEquals(generated.seed(), save.seed());
        assertEquals("Ria", save.name());
        assertEquals(CharacterClass.MAGE, save.characterClass());
        assertEquals(0, save.roomIndex());
        assertEquals(11, save.currentHp());
        assertEquals(3, save.spellCharges());
        assertEquals(List.of(Item.IRON_KEY), save.items());
        assertTrue(save.doneInteractions().isEmpty());
        assertTrue(printed().contains("Spiel gespeichert."));
    }

    @Test
    void aLoadedGameContinuesInTheSameRoomAndDoesNotOfferWhatWasAlreadyDone() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Runen versuchen (1) und scheitern, dann Speichern (Menue: Innehalten, Fusspuren, Tor, Speichern = 4)
        Game game = gameAt(generated, 0, new FixedDice(1), mage, io("1", "4"));
        assertEquals(RoomResult.SAVED, game.playRoom());
        SaveGame save = saves().load().orElseThrow();
        assertEquals(List.of("poi2"), save.doneInteractions());

        output.reset();
        Game resumed = Game.fromSave(io("3"), new Dice(), save, saves());
        assertEquals(RoomResult.NEXT, resumed.playRoom());
        assertFalse(printed().contains("Die Runen am Tor lesen"), "The interaction must not be offered again");
        assertTrue(printed().contains("RAUM 0 - Vor dem Tor"));
    }

    @Test
    void theRoomProgressInsightAndArcaneSightSurviveSavingAndLoading() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        RoomProgress progress = new RoomProgress(Set.of("poi2", "poi3"), true, true, true, 2);
        Game game = new Game(io("3"), new Dice(), mage, generated, 0, progress, true, false, saves());
        SaveGame snapshot = game.snapshot();
        assertEquals(Set.of("poi2", "poi3"), Set.copyOf(snapshot.doneInteractions()));
        assertTrue(snapshot.obstacleExamined());
        assertTrue(snapshot.specialDone());
        assertTrue(snapshot.ordinaryDefeated());
        assertEquals(2, snapshot.insightBonus());
        assertTrue(snapshot.arcaneSight());

        saves().save(snapshot);
        Game resumed = Game.fromSave(io(), new Dice(), saves().load().orElseThrow(), saves());
        assertTrue(resumed.isArcaneSight());
        assertEquals(2, resumed.getProgress().insightBonus);
        assertEquals(Set.of("poi2", "poi3"), resumed.getProgress().done);
        assertTrue(resumed.getProgress().obstacleExamined);
    }

    @Test
    void aSavedGameRestoresHpItemsAndCharges() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.RING_OF_LIFE);
        mage.takeDamage(5);
        mage.useSpellCharge();
        Game game = gameAt(generated, 0, new Dice(), mage, io("5"));
        assertEquals(RoomResult.SAVED, game.playRoom());

        Game resumed = Game.fromSave(io(), new Dice(), saves().load().orElseThrow(), saves());
        SaveGame again = resumed.snapshot();
        assertEquals(12, again.currentHp());
        assertEquals(2, again.spellCharges());
        assertEquals(List.of(Item.RING_OF_LIFE), again.items());
    }

    @Test
    void aGameSavedInALaterRoomHasTheSameDungeon() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        Room room = generated.room(3);
        // Ein eventueller gewoehnlicher Gegner gilt als besiegt, damit gleich das Raummenue erscheint
        RoomProgress progress = new RoomProgress(Set.of(), false, false, true, 0);
        Game probe = new Game(io(), new Dice(), rogue, generated, 3, progress, false, false, saves());
        int saveNumber = saveNumber(probe, room);
        Game game = new Game(io(String.valueOf(saveNumber)), new Dice(), rogue, generated, 3, progress, false,
                false, saves());
        assertEquals(RoomResult.SAVED, game.playRoom());
        SaveGame save = saves().load().orElseThrow();
        assertEquals(3, save.roomIndex());
        assertEquals(generated, DungeonGenerator.generate(save.seed()));
    }

    // --- Ganze Durchlaeufe und Tod ---

    @Test
    void walkingThroughTheWholeDungeonWithPerfectRollsEndsInVictory() {
        // Der Schurke schleicht sich immer an (Wurf 20) und trifft mit doppeltem Schaden (2xD10 = 20)
        List<String> script = new ArrayList<>();
        script.add("4"); // Raum 0: Gitter absuchen (1), Innehalten (2), Fusspuren (3), Tor (4)
        for (int i = 1; i <= 4; i++) {
            Room room = generated.room(i);
            if (room.enemy() == EnemyKind.ORDINARY) {
                script.add("1"); // schleichen
                script.add("1"); // angreifen
            }
            int passage = 3 + (room.enemy() == EnemyKind.SPECIAL ? 1 : 0) + 1;
            script.add(String.valueOf(passage)); // frei weitergehen, oder das Hindernis untersuchen
            if (room.blocked()) {
                script.add(String.valueOf(passage)); // dann der erste Weg
            }
        }
        script.add("1"); // Endgegner: schleichen
        script.add("1"); // angreifen

        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        Game game = gameAt(generated, 0, new MaxDice(), rogue, io(script.toArray(new String[0])));
        GameResult result = game.run();

        assertEquals(GameOutcome.VICTORY, result.outcome());
        assertEquals(5, result.roomsReached());
        assertTrue(printed().contains("RAUM 5 - Der Thronsaal"));
        assertTrue(printed().contains("Du hast Der tote Koenig besiegt!"));
        assertTrue(printed().contains("Hinterhalt"));
    }

    @Test
    void dyingInTheBossFightEndsTheGameInRoomFive() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(12);
        // Menue (1) = Schleichen: Wurf 2 misslingt, Muenzwurf 2 = Gegner beginnt, trifft (20) mit 6 Schaden
        Game game = gameAt(generated, 5, new FixedDice(2, 2, 20, 6), mage, io("1"));
        GameResult result = game.run();
        assertEquals(GameOutcome.DEATH, result.outcome());
        assertEquals(5, result.roomsReached());
    }

    @Test
    void dyingOnATrapEndsTheGameWithTheRoomsReached() {
        Room room = room(1, true, List.of(), EnemyKind.NONE, null, 0);
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(12);
        // Hindernis untersuchen (1), Weg A (1): Wurf 2 misslingt, Falle macht 6 Schaden
        Game game = gameAt(with(room), 1, new FixedDice(2, 6), mage, io("1", "1"));
        GameResult result = game.run();
        assertEquals(GameOutcome.DEATH, result.outcome());
        assertEquals(1, result.roomsReached());
    }
}

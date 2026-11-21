package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.Attribute;
import character.CharacterClass;
import character.Player;
import check.Difficulty;
import dice.Dice;
import dice.FixedDice;
import dice.MaxDice;
import dungeon.Approach;
import dungeon.Dungeon;
import dungeon.DungeonGenerator;
import dungeon.EnemyKind;
import dungeon.LootSpot;
import dungeon.Room;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import save.SaveGame;
import save.SaveManager;

class GameTest {

    @TempDir
    Path dir;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Dungeon dungeon = DungeonGenerator.generate(1);

    private SaveManager saves() {
        return new SaveManager(dir.resolve("savegame.txt"));
    }

    private ConsoleIO io(String... lines) {
        String input = String.join("\n", lines) + "\n";
        return new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private Game gameAt(int room, Dice dice, Player player, ConsoleIO io) {
        return new Game(io, dice, player, dungeon, room, new RoomProgress(), false, false, saves());
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private static WayOption hardStrengthWay() {
        return new WayOption(new Approach(Attribute.STRENGTH, "Aufbrechen", false), Difficulty.HARD, 18);
    }

    // --- Raum 0 und Starthilfe ---

    @Test
    void mageFindsAChargePotionAtTheGate() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(0, new FixedDice(20), mage, io("1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(mage.hasItem(Item.CHARGE_POTION));
    }

    @Test
    void rogueFindsAProtectionAmuletAtTheGate() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        Game game = gameAt(0, new FixedDice(20), rogue, io("1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(rogue.hasItem(Item.PROTECTION_AMULET));
        assertEquals(14, rogue.getArmorClass());
    }

    @Test
    void aFailedLootCheckChangesNothing() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(0, new FixedDice(1), mage, io("1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertTrue(mage.getItems().isEmpty());
        assertEquals(12, mage.getCurrentHp());
        assertTrue(printed().contains("passiert nichts"));
    }

    @Test
    void anEmptyHidingPlaceGivesNothing() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(1, new FixedDice(20), mage, io());
        game.tryLoot(new LootSpot(Attribute.INTELLIGENCE, "Suchen", Difficulty.EASY, 6, null));
        assertTrue(mage.getItems().isEmpty());
        assertTrue(printed().contains("leer"));
    }

    // --- Inventar voll ---

    @Test
    void withAFullInventoryThePlayerChoosesWhatToDrop() {
        Player player = new Player("Ria", CharacterClass.MAGE);
        for (int i = 0; i < 5; i++) {
            player.addItem(Item.IRON_KEY);
        }
        Game game = gameAt(1, new Dice(), player, io("2"));
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
        Game game = gameAt(1, new Dice(), player, io("6"));
        game.receiveItem(Item.RING_OF_WISDOM);
        assertFalse(player.hasItem(Item.RING_OF_WISDOM));
        assertEquals(5, player.getItems().size());
    }

    // --- Fallen und Wege ---

    @Test
    void aSuccessfulWayCheckLeadsToTheNextRoomUnharmed() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(1, new FixedDice(20), mage, io());
        assertEquals(RoomResult.NEXT, game.attemptWay(hardStrengthWay()));
        assertEquals(12, mage.getCurrentHp());
    }

    @Test
    void aFailedWayCheckTriggersATrapWithTwoD6Damage() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(1, new FixedDice(2, 3, 4), mage, io());
        assertEquals(RoomResult.NEXT, game.attemptWay(hardStrengthWay()));
        assertEquals(5, mage.getCurrentHp());
        assertTrue(printed().contains("Du nimmst 7 Schaden"));
    }

    @Test
    void aTrapThatDealsEnoughDamageKillsTheMage() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        Game game = gameAt(1, new FixedDice(2, 6, 6), mage, io());
        assertEquals(RoomResult.DEAD, game.attemptWay(hardStrengthWay()));
        assertFalse(mage.isAlive());
    }

    @Test
    void aFumbleOnAWayCheckIsDeadly() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        Game game = gameAt(1, new FixedDice(1), rogue, io());
        assertEquals(RoomResult.DEAD, game.attemptWay(hardStrengthWay()));
        assertEquals(0, rogue.getCurrentHp());
        assertTrue(printed().contains("toedlich"));
    }

    @Test
    void theIronKeyOpensTheWayAndIsGone() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.IRON_KEY);
        Game game = gameAt(1, new Dice(), mage, io());
        assertEquals(RoomResult.NEXT, game.useKey());
        assertFalse(mage.hasItem(Item.IRON_KEY));
    }

    // --- Arkane Sicht ---

    @Test
    void arcaneSightAddsThreeToTheNextIntelligenceCheckOnly() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Menue: Beute (1), Tor (2), Speichern (3), Zauber wirken (4). Dann Arkane Sicht (1), Beute (1), Tor (1)
        Game game = gameAt(0, new FixedDice(20), mage, io("4", "1", "1", "1"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertEquals(2, mage.getSpellCharges());
        assertTrue(printed().contains("+ 8 Intelligenz + Arkane Sicht"));
        assertFalse(game.isArcaneSight());
    }

    @Test
    void arcaneSightCannotBeStacked() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Zauber (4), Arkane Sicht, nochmal Zauber (4), dann Tor (2)
        Game game = gameAt(0, new Dice(), mage, io("4", "1", "4", "2"));
        assertEquals(RoomResult.NEXT, game.playRoom());
        assertEquals(2, mage.getSpellCharges());
        assertTrue(printed().contains("wirkt bereits"));
    }

    @Test
    void theResultOfAnActionIsShownAtTheTopOfTheNextScreen() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        // Beute versuchen (1, Wurf 20, Schutzamulett), danach erscheint das Menue neu, dann Tor (1)
        Game game = gameAt(0, new FixedDice(20), rogue, io("1", "1"));
        game.playRoom();

        String text = printed();
        int block = text.lastIndexOf("--- Was zuletzt geschah ---");
        assertTrue(block > 0, "The result block is missing");
        String lastScreen = text.substring(block);
        assertTrue(lastScreen.indexOf("Schutzamulett kommt in dein Inventar.") < lastScreen.indexOf("RAUM 0"));
    }

    // --- Speichern und Laden ---

    @Test
    void savingWritesTheCurrentStateAndEndsTheSession() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.takeDamage(3);
        mage.addItem(Item.IRON_KEY);
        // Raum 0: Beute (1), Tor (2), Speichern (3)
        Game game = gameAt(0, new Dice(), mage, io("3"));
        assertEquals(RoomResult.SAVED, game.playRoom());

        SaveGame save = saves().load().orElseThrow();
        assertEquals(dungeon.seed(), save.seed());
        assertEquals("Ria", save.name());
        assertEquals(CharacterClass.MAGE, save.characterClass());
        assertEquals(0, save.roomIndex());
        assertEquals(9, save.currentHp());
        assertEquals(3, save.spellCharges());
        assertEquals(List.of(Item.IRON_KEY), save.items());
        assertFalse(save.lootTried());
        assertTrue(printed().contains("Spiel gespeichert."));
    }

    @Test
    void aLoadedGameContinuesInTheSameRoomAndDoesNotOfferWhatWasAlreadyTried() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        // Beute versuchen (1) und scheitern, dann Speichern (Menue: Tor (1), Speichern (2))
        Game game = gameAt(0, new FixedDice(1), mage, io("1", "2"));
        assertEquals(RoomResult.SAVED, game.playRoom());
        SaveGame save = saves().load().orElseThrow();
        assertTrue(save.lootTried());

        output.reset();
        Game resumed = Game.fromSave(io("1"), new Dice(), save, saves());
        assertEquals(RoomResult.NEXT, resumed.playRoom());
        assertFalse(printed().contains("Die Runen am Tor lesen"), "The loot attempt must not be offered again");
        assertTrue(printed().contains("RAUM 0 - Vor dem Tor"));
    }

    @Test
    void aSavedGameRestoresHpItemsChargesAndArcaneSight() {
        Player mage = new Player("Ria", CharacterClass.MAGE);
        mage.addItem(Item.RING_OF_LIFE);
        mage.takeDamage(5);
        mage.useSpellCharge();
        Game game = new Game(io("3"), new Dice(), mage, dungeon, 0, new RoomProgress(), true, false, saves());
        assertEquals(RoomResult.SAVED, game.playRoom());

        Game resumed = Game.fromSave(io(), new Dice(), saves().load().orElseThrow(), saves());
        assertTrue(resumed.isArcaneSight());
        SaveGame again = resumed.snapshot();
        assertEquals(10, again.currentHp());
        assertEquals(2, again.spellCharges());
        assertEquals(List.of(Item.RING_OF_LIFE), again.items());
    }

    @Test
    void aGameLoadedInALaterRoomHasTheSameDungeon() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        // Ein eventueller gewoehnlicher Gegner gilt als besiegt, damit gleich das Raummenue erscheint
        Game game = new Game(io(String.valueOf(saveOptionNumber(3))), new Dice(), rogue, dungeon, 3,
                new RoomProgress(false, false, true), false, false, saves());
        assertEquals(RoomResult.SAVED, game.playRoom());
        SaveGame save = saves().load().orElseThrow();
        assertEquals(3, save.roomIndex());
        assertEquals(dungeon, DungeonGenerator.generate(save.seed()));
    }

    /** Die Nummer von "Speichern" im Menue eines normalen Raums ohne Gegner-Begegnung beim Betreten. */
    private int saveOptionNumber(int roomIndex) {
        Room room = dungeon.room(roomIndex);
        return 1 + (room.enemy() == EnemyKind.SPECIAL ? 1 : 0) + room.wayOptions().size() + 1;
    }

    // --- Ein ganzer Durchlauf ---

    @Test
    void walkingThroughTheWholeDungeonWithPerfectRollsEndsInVictory() {
        // Der Schurke schleicht sich immer an (Wurf 20) und trifft mit doppeltem Schaden (2xD10 = 20)
        List<String> script = new ArrayList<>();
        script.add("2"); // Raum 0: Gitter aufstossen (Beute ist 1)
        for (int i = 1; i <= 4; i++) {
            Room room = dungeon.room(i);
            if (room.enemy() == EnemyKind.ORDINARY) {
                script.add("1"); // schleichen
                script.add("1"); // angreifen
            }
            int firstWay = 1 + (room.enemy() == EnemyKind.SPECIAL ? 1 : 0) + 1;
            script.add(String.valueOf(firstWay));
        }
        script.add("1"); // Endgegner: schleichen
        script.add("1"); // angreifen

        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        Game game = gameAt(0, new MaxDice(), rogue, io(script.toArray(new String[0])));
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
        mage.takeDamage(10);
        // Menue (1) = Schleichen: Wurf 2 misslingt, Muenzwurf 2 = Gegner beginnt, trifft (20) mit 6 Schaden
        Game game = gameAt(5, new FixedDice(2, 2, 20, 6), mage, io("1"));
        GameResult result = game.run();
        assertEquals(GameOutcome.DEATH, result.outcome());
        assertEquals(5, result.roomsReached());
    }

    @Test
    void dyingInARoomEndsTheGameWithTheRoomsReached() {
        Player rogue = new Player("Bob", CharacterClass.ROGUE);
        // Raum 1: Weg waehlen, Patzer = tot. Raum 1 hat nie einen gewoehnlichen Gegner.
        assertTrue(dungeon.room(1).enemy() != EnemyKind.ORDINARY);
        int firstWay = 1 + (dungeon.room(1).enemy() == EnemyKind.SPECIAL ? 1 : 0) + 1;
        Game game = gameAt(1, new FixedDice(1), rogue, io(String.valueOf(firstWay)));
        GameResult result = game.run();
        assertEquals(GameOutcome.DEATH, result.outcome());
        assertEquals(1, result.roomsReached());
    }
}

package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import dice.Dice;
import dungeon.Dungeon;
import dungeon.DungeonGenerator;
import item.Inventory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import save.SaveManager;

/**
 * Spielt viele Spiele mit zufaelligen Eingaben und echten (aber festen) Wuerfeln durch. Das findet Abstuerze
 * und Endlosschleifen, die gezielte Tests nicht abdecken.
 */
class RandomPlaythroughTest {

    private static final int GAMES = 400;
    private static final int MAX_INPUT_LINES = 50_000;

    @TempDir
    Path dir;

    /** Liefert endlos zufaellige Zahlen von 1 bis 6, jede in einer eigenen Zeile. */
    private static InputStream randomDigits(long seed) {
        Random random = new Random(seed);
        return new InputStream() {
            private byte[] current = new byte[0];
            private int position = 0;
            private int lines = 0;

            @Override
            public int read() {
                if (position >= current.length) {
                    if (lines++ >= MAX_INPUT_LINES) {
                        return -1;
                    }
                    current = ((1 + random.nextInt(6)) + "\n").getBytes(StandardCharsets.UTF_8);
                    position = 0;
                }
                return current[position++];
            }
        };
    }

    @Test
    void manyRandomGamesAlwaysEndCleanly() {
        Map<GameOutcome, Integer> outcomes = new EnumMap<>(GameOutcome.class);
        for (long seed = 0; seed < GAMES; seed++) {
            ConsoleIO io = new ConsoleIO(randomDigits(seed),
                    new PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8));
            CharacterClass characterClass = CharacterClass.values()[(int) (seed % 2)];
            Player player = new Player("Tester", characterClass);
            Dungeon dungeon = DungeonGenerator.generate(seed);
            SaveManager saves = new SaveManager(dir.resolve("savegame" + seed + ".txt"));

            GameResult result = new Game(io, new Dice(seed), player, dungeon, saves).run();

            assertNotNull(result.outcome(), "Seed " + seed);
            assertTrue(result.roomsReached() >= 0 && result.roomsReached() <= Dungeon.LAST_ROOM, "Seed " + seed);
            assertTrue(player.getCurrentHp() <= player.getMaxHp(), "Seed " + seed);
            assertTrue(player.getItems().size() <= Inventory.CAPACITY, "Seed " + seed);
            assertTrue(player.getSpellCharges() <= characterClass.getSpellCharges(), "Seed " + seed);
            switch (result.outcome()) {
                case DEATH -> assertEquals(0, player.getCurrentHp(), "Seed " + seed);
                case VICTORY -> {
                    assertTrue(player.isAlive(), "Seed " + seed);
                    assertEquals(Dungeon.LAST_ROOM, result.roomsReached(), "Seed " + seed);
                }
                case SAVED -> {
                    assertTrue(player.isAlive(), "Seed " + seed);
                    assertTrue(saves.load().isPresent(), "A saved game must be loadable, seed " + seed);
                }
            }
            outcomes.merge(result.outcome(), 1, Integer::sum);
        }
        assertTrue(outcomes.getOrDefault(GameOutcome.DEATH, 0) > 0, "No game ended in death: " + outcomes);
        assertTrue(outcomes.getOrDefault(GameOutcome.SAVED, 0) > 0, "No game was saved: " + outcomes);
    }

    @Test
    void savedRandomGamesCanBeResumedAndPlayedOn() {
        int resumed = 0;
        for (long seed = 0; seed < GAMES; seed++) {
            ConsoleIO io = new ConsoleIO(randomDigits(seed),
                    new PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8));
            Player player = new Player("Tester", CharacterClass.values()[(int) (seed % 2)]);
            SaveManager saves = new SaveManager(dir.resolve("resume" + seed + ".txt"));
            GameResult first = new Game(io, new Dice(seed), player, DungeonGenerator.generate(seed), saves).run();
            if (first.outcome() != GameOutcome.SAVED) {
                continue;
            }
            ConsoleIO io2 = new ConsoleIO(randomDigits(seed + 1000),
                    new PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8));
            GameResult second = Game.fromSave(io2, new Dice(seed + 1000), saves.load().orElseThrow(), saves).run();
            assertNotNull(second.outcome(), "Seed " + seed);
            assertTrue(second.roomsReached() >= first.roomsReached(), "Seed " + seed);
            resumed++;
        }
        assertTrue(resumed > 0, "No saved game was resumed");
    }
}

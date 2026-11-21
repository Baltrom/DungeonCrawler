package dungeon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import check.Difficulty;
import item.Item;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DungeonGeneratorTest {

    private static final int SEEDS = 500;

    @Test
    void sameSeedGivesTheSameDungeon() {
        assertEquals(DungeonGenerator.generate(42), DungeonGenerator.generate(42));
    }

    @Test
    void differentSeedsGiveDifferentDungeons() {
        Set<Dungeon> dungeons = new HashSet<>();
        for (long seed = 0; seed < 50; seed++) {
            dungeons.add(DungeonGenerator.generate(seed));
        }
        assertTrue(dungeons.size() > 40);
    }

    @Test
    void hasSixRoomsEntranceFirstBossLast() {
        Dungeon dungeon = DungeonGenerator.generate(1);
        assertEquals(6, dungeon.rooms().size());
        assertEquals(RoomKind.ENTRANCE, dungeon.room(0).kind());
        for (int i = 1; i <= 4; i++) {
            assertEquals(RoomKind.NORMAL, dungeon.room(i).kind());
            assertEquals(i, dungeon.room(i).index());
        }
        assertEquals(RoomKind.BOSS, dungeon.room(5).kind());
        assertEquals(EnemyKind.BOSS, dungeon.room(5).enemy());
    }

    @Test
    void roomsOneToFourUseFourDifferentTemplates() {
        for (long seed = 0; seed < SEEDS; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            Set<RoomTemplate> templates = new HashSet<>();
            for (int i = 1; i <= 4; i++) {
                templates.add(dungeon.room(i).template());
            }
            assertEquals(4, templates.size(), "Seed " + seed);
        }
    }

    @Test
    void thereIsExactlyOneOrdinaryEnemyInRoomTwoToFourAndOneSpecialEnemyElsewhere() {
        for (long seed = 0; seed < SEEDS; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            int ordinary = 0;
            int special = 0;
            for (int i = 1; i <= 4; i++) {
                Room room = dungeon.room(i);
                if (room.enemy() == EnemyKind.ORDINARY) {
                    ordinary++;
                    assertTrue(i >= 2, "Ordinary enemy in room 1, seed " + seed);
                    assertTrue(room.encounterTarget() >= 10 && room.encounterTarget() <= 12);
                    assertNull(room.enemyItem());
                } else if (room.enemy() == EnemyKind.SPECIAL) {
                    special++;
                    assertNotNull(room.enemyItem(), "The special enemy always drops an item");
                    assertTrue(room.encounterTarget() >= 10 && room.encounterTarget() <= 12);
                } else {
                    assertEquals(0, room.encounterTarget());
                }
            }
            assertEquals(1, ordinary, "Seed " + seed);
            assertEquals(1, special, "Seed " + seed);
        }
    }

    // --- freie und versperrte Raeume ---

    @Test
    void roomOneIsAlwaysFreeOneOfTwoAndThreeIsFreeAndRoomFourIsAlwaysBlocked() {
        for (long seed = 0; seed < SEEDS; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            assertFalse(dungeon.room(0).blocked());
            assertFalse(dungeon.room(1).blocked(), "Seed " + seed);
            assertTrue(dungeon.room(4).blocked(), "Seed " + seed);
            int freeAmongTwoAndThree = 0;
            for (int i = 2; i <= 3; i++) {
                if (!dungeon.room(i).blocked()) {
                    freeAmongTwoAndThree++;
                }
            }
            assertEquals(1, freeAmongTwoAndThree, "Seed " + seed);
            assertFalse(dungeon.room(5).blocked());
        }
    }

    @Test
    void bothFreeRoomsOccurAcrossSeeds() {
        Set<Integer> secondFree = new HashSet<>();
        for (long seed = 0; seed < 100; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            secondFree.add(dungeon.room(2).blocked() ? 3 : 2);
        }
        assertEquals(Set.of(2, 3), secondFree);
    }

    @Test
    void blockedRoomsHaveTwoWaysWithTargetsInsideTheirDifficultyAndFreeRoomsHaveNone() {
        for (long seed = 0; seed < SEEDS; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            for (int i = 1; i <= 4; i++) {
                Room room = dungeon.room(i);
                if (!room.blocked()) {
                    assertTrue(room.wayOptions().isEmpty(), "Free room with ways, seed " + seed);
                    continue;
                }
                assertEquals(2, room.wayOptions().size());
                for (WayOption way : room.wayOptions()) {
                    Difficulty difficulty = way.difficulty();
                    assertEquals(way.def().harder() ? Difficulty.HARD : Difficulty.MEDIUM, difficulty);
                    assertTrue(way.target() >= difficulty.getMinTarget() && way.target() <= difficulty.getMaxTarget());
                }
            }
        }
    }

    // --- Interaktionen ---

    @Test
    void everyNormalRoomHasItsScriptInteractionsWithExactlyOneItemSpot() {
        for (long seed = 0; seed < 50; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            for (int i = 1; i <= 4; i++) {
                Room room = dungeon.room(i);
                assertEquals(room.template().getScript().interactions().size(), room.interactions().size());
                long itemSpots = room.interactions().stream()
                        .filter(interaction -> interaction.def().effect() == Effect.ITEM).count();
                assertEquals(1, itemSpots, "Room " + i + ", seed " + seed);
            }
        }
    }

    @Test
    void checkTargetsStayInsideTheirDifficultyAndFollowDifficultyByRoom() {
        for (long seed = 0; seed < SEEDS; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            for (int i = 0; i <= 4; i++) {
                for (Interaction interaction : dungeon.room(i).interactions()) {
                    assertCheckValues(interaction, i);
                    if (interaction.follow() != null) {
                        assertCheckValues(interaction.follow(), i);
                    }
                }
            }
        }
    }

    private void assertCheckValues(Interaction interaction, int roomIndex) {
        if (interaction.def().attribute() == null) {
            assertNull(interaction.difficulty());
            assertEquals(0, interaction.target());
            return;
        }
        Difficulty base = switch (roomIndex) {
            case 0, 1 -> Difficulty.EASY;
            case 4 -> Difficulty.HARD;
            default -> Difficulty.MEDIUM;
        };
        Difficulty expected = interaction.def().easier() ? base.easier() : base;
        assertEquals(expected, interaction.difficulty(), "Room " + roomIndex + " " + interaction.id());
        assertTrue(interaction.target() >= expected.getMinTarget() && interaction.target() <= expected.getMaxTarget());
    }

    @Test
    void lootNeverContainsTheHealingPotionAndIsSometimesEmpty() {
        int spots = 0;
        int empty = 0;
        for (long seed = 0; seed < 2000; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            for (int i = 1; i <= 4; i++) {
                for (Interaction interaction : dungeon.room(i).interactions()) {
                    if (interaction.def().effect() != Effect.ITEM) {
                        assertNull(interaction.item(), "Only item spots hold items");
                        continue;
                    }
                    spots++;
                    if (interaction.item() == null) {
                        empty++;
                    } else {
                        assertNotEquals(Item.HEALING_POTION, interaction.item());
                    }
                }
                Item enemyItem = dungeon.room(i).enemyItem();
                if (enemyItem != null) {
                    assertNotEquals(Item.HEALING_POTION, enemyItem);
                }
            }
        }
        double share = (double) empty / spots;
        assertTrue(share > 0.07 && share < 0.13, "Empty share was " + share);
    }

    @Test
    void theEntranceOffersTheClassSpecificStartHelp() {
        Dungeon dungeon = DungeonGenerator.generate(5);
        Room entrance = dungeon.room(0);
        Interaction mage = entrance.interactions().stream()
                .filter(i -> i.def().onlyFor() == CharacterClass.MAGE).findFirst().orElseThrow();
        Interaction rogue = entrance.interactions().stream()
                .filter(i -> i.def().onlyFor() == CharacterClass.ROGUE).findFirst().orElseThrow();
        assertEquals(Item.CHARGE_POTION, mage.item());
        assertEquals(Item.PROTECTION_AMULET, rogue.item());
        assertEquals(Difficulty.EASY, mage.difficulty());
        assertTrue(mage.target() >= 6 && mage.target() <= 8);
    }

    @Test
    void theBossRoomHasNoInteractionsAndAHardEncounter() {
        Room boss = DungeonGenerator.generate(3).room(5);
        assertTrue(boss.interactions().isEmpty());
        assertTrue(boss.encounterTarget() >= 14 && boss.encounterTarget() <= 18);
    }

    @Test
    void ringOfLifeIsRarerThanOtherItems() {
        int rings = 0;
        int wisdom = 0;
        Random random = new Random(11);
        for (int i = 0; i < 20000; i++) {
            Item item = DungeonGenerator.pickItem(random);
            if (item == Item.RING_OF_LIFE) {
                rings++;
            } else if (item == Item.RING_OF_WISDOM) {
                wisdom++;
            }
        }
        assertTrue(rings > 0);
        assertTrue(rings < wisdom, "Ring of life should be rarer: " + rings + " vs " + wisdom);
    }
}

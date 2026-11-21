package dungeon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import check.Difficulty;
import item.Item;
import java.util.HashSet;
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

    @Test
    void everyNormalRoomHasTwoWaysWithTargetsInsideTheirDifficulty() {
        for (long seed = 0; seed < SEEDS; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            for (int i = 1; i <= 4; i++) {
                Room room = dungeon.room(i);
                assertEquals(2, room.wayOptions().size());
                for (WayOption way : room.wayOptions()) {
                    Difficulty difficulty = way.difficulty();
                    assertTrue(way.target() >= difficulty.getMinTarget()
                            && way.target() <= difficulty.getMaxTarget());
                }
            }
        }
    }

    @Test
    void waysGetHarderWithTheRoomNumberAndTheHarderApproach() {
        Dungeon dungeon = DungeonGenerator.generate(7);
        for (WayOption way : dungeon.room(1).wayOptions()) {
            assertEquals(way.approach().harder() ? Difficulty.MEDIUM : Difficulty.EASY, way.difficulty());
        }
        for (int i = 2; i <= 4; i++) {
            for (WayOption way : dungeon.room(i).wayOptions()) {
                assertEquals(way.approach().harder() ? Difficulty.HARD : Difficulty.MEDIUM, way.difficulty());
            }
        }
    }

    @Test
    void lootDifficultyFollowsTheRoomNumber() {
        Dungeon dungeon = DungeonGenerator.generate(3);
        assertEquals(Difficulty.EASY, dungeon.room(1).loot().difficulty());
        assertEquals(Difficulty.MEDIUM, dungeon.room(2).loot().difficulty());
        assertEquals(Difficulty.MEDIUM, dungeon.room(3).loot().difficulty());
        assertEquals(Difficulty.HARD, dungeon.room(4).loot().difficulty());
    }

    @Test
    void lootNeverContainsTheHealingPotionAndIsSometimesEmpty() {
        int spots = 0;
        int empty = 0;
        for (long seed = 0; seed < 2000; seed++) {
            Dungeon dungeon = DungeonGenerator.generate(seed);
            for (int i = 1; i <= 4; i++) {
                Item item = dungeon.room(i).loot().item();
                spots++;
                if (item == null) {
                    empty++;
                } else {
                    assertNotEquals(Item.HEALING_POTION, item);
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
    void startHelpDependsOnTheClass() {
        Dungeon dungeon = DungeonGenerator.generate(5);
        LootSpot mage = dungeon.startHelp(CharacterClass.MAGE);
        LootSpot rogue = dungeon.startHelp(CharacterClass.ROGUE);
        assertEquals(Item.CHARGE_POTION, mage.item());
        assertEquals(Item.PROTECTION_AMULET, rogue.item());
        assertEquals(Difficulty.EASY, mage.difficulty());
        assertTrue(mage.target() >= 6 && mage.target() <= 8);
    }

    @Test
    void ringOfLifeIsRarerThanOtherItems() {
        int rings = 0;
        int wisdom = 0;
        java.util.Random random = new java.util.Random(11);
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

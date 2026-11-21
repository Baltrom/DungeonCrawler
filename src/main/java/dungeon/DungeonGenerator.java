package dungeon;

import check.Difficulty;
import item.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Erzeugt aus einem Seed immer denselben Dungeon. Benutzt einen eigenen Zufallsgenerator,
 * getrennt von den Wuerfen im Spiel.
 */
public final class DungeonGenerator {

    private static final int RANDOM_ROOMS = 4;
    private static final int EMPTY_LOOT_CHANCE = 10; // 1 von 10, also 10 %

    private DungeonGenerator() {
    }

    public static Dungeon generate(long seed) {
        Random random = new Random(seed);

        List<RoomTemplate> templates = new ArrayList<>(Arrays.asList(RoomTemplate.values()));
        Collections.shuffle(templates, random);

        // Gewoehnlicher Gegner in Raum 2-4, besonderer Gegner in einem anderen Raum 1-4
        int ordinaryRoom = 2 + random.nextInt(3);
        int specialRoom;
        do {
            specialRoom = 1 + random.nextInt(RANDOM_ROOMS);
        } while (specialRoom == ordinaryRoom);

        int startHelpTarget = Difficulty.EASY.pickTarget(random);

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(0, RoomKind.ENTRANCE, null, List.of(), null, EnemyKind.NONE, null, 0));
        for (int index = 1; index <= RANDOM_ROOMS; index++) {
            RoomTemplate template = templates.get(index - 1);
            EnemyKind enemy = index == ordinaryRoom ? EnemyKind.ORDINARY
                    : index == specialRoom ? EnemyKind.SPECIAL : EnemyKind.NONE;
            rooms.add(new Room(index, RoomKind.NORMAL, template,
                    createWayOptions(template, index, random),
                    createLoot(template, index, random),
                    enemy,
                    enemy == EnemyKind.SPECIAL ? pickItem(random) : null,
                    enemy == EnemyKind.NONE ? 0 : Difficulty.MEDIUM.pickTarget(random)));
        }
        rooms.add(new Room(Dungeon.LAST_ROOM, RoomKind.BOSS, null, List.of(), null,
                EnemyKind.BOSS, null, Difficulty.HARD.pickTarget(random)));

        return new Dungeon(seed, List.copyOf(rooms), startHelpTarget);
    }

    private static List<WayOption> createWayOptions(RoomTemplate template, int roomIndex, Random random) {
        Difficulty base = roomIndex == 1 ? Difficulty.EASY : Difficulty.MEDIUM;
        List<WayOption> options = new ArrayList<>();
        for (Approach approach : template.getApproaches()) {
            Difficulty difficulty = approach.harder() ? base.harder() : base;
            options.add(new WayOption(approach, difficulty, difficulty.pickTarget(random)));
        }
        return List.copyOf(options);
    }

    private static LootSpot createLoot(RoomTemplate template, int roomIndex, Random random) {
        Difficulty difficulty = switch (roomIndex) {
            case 1 -> Difficulty.EASY;
            case 4 -> Difficulty.HARD;
            default -> Difficulty.MEDIUM;
        };
        LootAction action = template.getLootAction();
        boolean empty = random.nextInt(EMPTY_LOOT_CHANCE) == 0;
        Item item = empty ? null : pickItem(random);
        return new LootSpot(action.attribute(), action.label(), difficulty, difficulty.pickTarget(random), item);
    }

    /** Waehlt ein Item aus dem Beute-Pool, gewichtet nach Haeufigkeit (der Ring des Lebens ist selten). */
    static Item pickItem(Random random) {
        List<Item> pool = Item.lootPool();
        int totalWeight = pool.stream().mapToInt(Item::getLootWeight).sum();
        int roll = random.nextInt(totalWeight);
        for (Item item : pool) {
            roll -= item.getLootWeight();
            if (roll < 0) {
                return item;
            }
        }
        throw new IllegalStateException("Loot pool is empty");
    }
}

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
        // Raum 1 ist immer frei, dazu Raum 2 oder 3. Raum 4 (vor dem Endgegner) ist immer versperrt.
        int secondFreeRoom = 2 + random.nextInt(2);

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(0, RoomKind.ENTRANCE, null, false, List.of(),
                createInteractions(RoomScript.load("room0"), 0, random), EnemyKind.NONE, null, 0));
        for (int index = 1; index <= RANDOM_ROOMS; index++) {
            RoomTemplate template = templates.get(index - 1);
            RoomScript script = template.getScript();
            boolean blocked = index != 1 && index != secondFreeRoom;
            EnemyKind enemy = index == ordinaryRoom ? EnemyKind.ORDINARY
                    : index == specialRoom ? EnemyKind.SPECIAL : EnemyKind.NONE;
            rooms.add(new Room(index, RoomKind.NORMAL, template, blocked,
                    blocked ? createWayOptions(script, random) : List.of(),
                    createInteractions(script, index, random),
                    enemy,
                    enemy == EnemyKind.SPECIAL ? pickItem(random) : null,
                    enemy == EnemyKind.NONE ? 0 : Difficulty.MEDIUM.pickTarget(random)));
        }
        rooms.add(new Room(Dungeon.LAST_ROOM, RoomKind.BOSS, null, false, List.of(), List.of(),
                EnemyKind.BOSS, null, Difficulty.HARD.pickTarget(random)));

        return new Dungeon(seed, List.copyOf(rooms));
    }

    private static List<WayOption> createWayOptions(RoomScript script, Random random) {
        List<WayOption> options = new ArrayList<>();
        for (WayDef way : script.ways()) {
            Difficulty difficulty = way.harder() ? Difficulty.HARD : Difficulty.MEDIUM;
            options.add(new WayOption(way, difficulty, difficulty.pickTarget(random)));
        }
        return List.copyOf(options);
    }

    private static List<Interaction> createInteractions(RoomScript script, int roomIndex, Random random) {
        List<Interaction> interactions = new ArrayList<>();
        for (InteractionDef def : script.interactions()) {
            interactions.add(createInteraction(def, roomIndex, random));
        }
        return List.copyOf(interactions);
    }

    private static Interaction createInteraction(InteractionDef def, int roomIndex, Random random) {
        Difficulty difficulty = null;
        int target = 0;
        if (def.attribute() != null) {
            difficulty = checkDifficulty(roomIndex, def.easier());
            target = difficulty.pickTarget(random);
        }
        Item item = null;
        if (def.effect() == Effect.ITEM) {
            if (def.fixedItem() != null) {
                item = def.fixedItem();
            } else if (random.nextInt(EMPTY_LOOT_CHANCE) != 0) {
                item = pickItem(random);
            }
        }
        Interaction follow = def.follow() == null ? null : createInteraction(def.follow(), roomIndex, random);
        return new Interaction(def, difficulty, target, item, follow);
    }

    /** Schwierigkeit der Checks beim Untersuchen: Raum 0-1 leicht, Raum 2-3 mittel, Raum 4 schwer. */
    private static Difficulty checkDifficulty(int roomIndex, boolean easier) {
        Difficulty base = switch (roomIndex) {
            case 0, 1 -> Difficulty.EASY;
            case 4 -> Difficulty.HARD;
            default -> Difficulty.MEDIUM;
        };
        return easier ? base.easier() : base;
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

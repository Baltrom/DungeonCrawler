package save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import item.Item;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaveManagerTest {

    @TempDir
    Path dir;

    private SaveManager manager() {
        return new SaveManager(dir.resolve("savegame.txt"));
    }

    private static SaveGame sample() {
        return new SaveGame(123456789L, "Ria", CharacterClass.MAGE, 2, 9, 3,
                List.of(Item.HEALING_POTION, Item.RING_OF_LIFE),
                List.of("poi1", "poi2", "poi2.follow"), true, false, true, 2, false);
    }

    private List<String> validLines() {
        return List.of("seed=5", "name=Ria", "class=MAGE", "room=1", "hp=9", "charges=3",
                "items=HEALING_POTION", "done=poi1,poi2.follow", "obstacleExamined=false",
                "specialDone=false", "ordinaryDefeated=false", "insight=0", "arcaneSight=false");
    }

    private String replace(String key, String value) {
        return String.join("\n", validLines().stream()
                .map(line -> line.startsWith(key + "=") ? key + "=" + value : line).toList());
    }

    private void assertCorrupt(String content) throws IOException {
        Files.writeString(dir.resolve("savegame.txt"), content, StandardCharsets.UTF_8);
        assertTrue(manager().exists());
        assertTrue(manager().load().isEmpty(), "Expected a corrupt save to be rejected: " + content);
    }

    @Test
    void thereIsNoSaveInTheBeginning() {
        assertFalse(manager().exists());
        assertTrue(manager().load().isEmpty());
    }

    @Test
    void aSavedGameCanBeLoadedExactlyAsSaved() {
        SaveManager manager = manager();
        manager.save(sample());
        assertTrue(manager.exists());
        assertEquals(sample(), manager.load().orElseThrow());
    }

    @Test
    void aGameWithoutItemsOrProgressRoundTrips() {
        SaveGame empty = new SaveGame(-77L, "Bob", CharacterClass.ROGUE, 0, 16, 0, List.of(),
                List.of(), false, false, false, 0, true);
        manager().save(empty);
        assertEquals(empty, manager().load().orElseThrow());
    }

    @Test
    void savingAgainOverwritesTheOldSave() {
        SaveManager manager = manager();
        manager.save(sample());
        SaveGame other = new SaveGame(1L, "Max", CharacterClass.ROGUE, 3, 10, 0, List.of(Item.IRON_KEY),
                List.of("poi3"), false, true, false, 0, false);
        manager.save(other);
        assertEquals(other, manager.load().orElseThrow());
    }

    @Test
    void deleteRemovesTheSaveAndIsHarmlessWithoutOne() {
        SaveManager manager = manager();
        manager.delete();
        manager.save(sample());
        manager.delete();
        assertFalse(manager.exists());
    }

    @Test
    void theRingOfLifeRaisesTheValidHpRange() throws IOException {
        // Mage 14 + Ring 3 = 17 maximale Lebenspunkte
        Files.writeString(dir.resolve("savegame.txt"),
                replace("items", "RING_OF_LIFE").replace("hp=9", "hp=17"), StandardCharsets.UTF_8);
        assertTrue(manager().load().isPresent());
        Files.writeString(dir.resolve("savegame.txt"),
                replace("items", "RING_OF_LIFE").replace("hp=9", "hp=18"), StandardCharsets.UTF_8);
        assertTrue(manager().load().isEmpty());
    }

    @Test
    void aBrokenSaveIsRejectedInsteadOfCrashing() throws IOException {
        assertCorrupt("das ist kein Spielstand");
        assertCorrupt("");
        assertCorrupt(String.join("\n", validLines().stream().filter(l -> !l.startsWith("hp=")).toList()));
        assertCorrupt(String.join("\n", validLines().stream().filter(l -> !l.startsWith("done=")).toList()));
        assertCorrupt(replace("seed", "abc"));
        assertCorrupt(replace("class", "WIZARD"));
        assertCorrupt(replace("items", "SWORD_OF_DOOM"));
        assertCorrupt(replace("room", "5"));
        assertCorrupt(replace("room", "-1"));
        assertCorrupt(replace("hp", "0"));
        assertCorrupt(replace("hp", "99"));
        assertCorrupt(replace("charges", "4"));
        assertCorrupt(replace("name", "Ria Muster"));
        assertCorrupt(replace("obstacleExamined", "maybe"));
        assertCorrupt(replace("insight", "5"));
        assertCorrupt(replace("insight", "x"));
        assertCorrupt(replace("done", "Poi 1"));
        assertCorrupt(replace("items", "IRON_KEY,IRON_KEY,IRON_KEY,IRON_KEY,IRON_KEY,IRON_KEY"));
    }

    @Test
    void anOldSaveFormatIsReportedAsBroken() throws IOException {
        assertCorrupt(String.join("\n", "seed=5", "name=Ria", "class=MAGE", "room=1", "hp=9", "charges=3",
                "items=", "lootTried=false", "specialDone=false", "ordinaryDefeated=false", "arcaneSight=false"));
    }

    @Test
    void aRogueCannotHaveSpellCharges() throws IOException {
        assertCorrupt(replace("class", "ROGUE"));
    }

    @Test
    void aSaveGameRebuildsThePlayer() {
        Player player = sample().toPlayer();
        assertEquals("Ria", player.getName());
        assertEquals(CharacterClass.MAGE, player.getCharacterClass());
        assertEquals(9, player.getCurrentHp());
        assertEquals(17, player.getMaxHp());
        assertEquals(3, player.getSpellCharges());
        assertTrue(player.hasItem(Item.HEALING_POTION));
        assertTrue(player.hasItem(Item.RING_OF_LIFE));
    }

    @Test
    void aSaveGameRefusesInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new SaveGame(1L, "Ria", CharacterClass.MAGE, 5, 9, 3,
                List.of(), List.of(), false, false, false, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new SaveGame(1L, "", CharacterClass.MAGE, 1, 9, 3,
                List.of(), List.of(), false, false, false, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new SaveGame(1L, "Ria", CharacterClass.MAGE, 1, 9, 3,
                List.of(), List.of("bad id"), false, false, false, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new SaveGame(1L, "Ria", CharacterClass.MAGE, 1, 9, 3,
                List.of(), List.of(), false, false, false, 1, false));
    }
}

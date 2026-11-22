package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import story.AsciiArt;

class ArtPainterTest {

    private static final List<String> IMAGES = List.of("title", "mage", "rogue", "gate", "library", "crypt",
            "statue_hall", "trap_corridor", "alchemy_lab", "prison", "cistern", "armory", "throne");
    private static final List<String> ROOM_SCRIPTS = List.of("room0", "room5", "library", "crypt", "statue_hall",
            "trap_corridor", "alchemy_lab", "prison", "cistern", "armory");

    @AfterEach
    void colorsOffAgain() {
        Ansi.setEnabled(false);
    }

    @Test
    void withoutColorsTheLinesStayUnchanged() {
        List<String> lines = AsciiArt.load("mage");
        assertSame(lines, ArtPainter.paint("mage", lines));
    }

    @Test
    void colorsDoNotChangeTheVisibleText() {
        Ansi.setEnabled(true);
        for (String name : IMAGES) {
            List<String> plain = AsciiArt.load(name);
            List<String> painted = ArtPainter.paint(name, plain);
            assertEquals(plain.size(), painted.size(), name);
            for (int i = 0; i < plain.size(); i++) {
                assertEquals(plain.get(i), stripCodes(painted.get(i)), name + " Zeile " + i);
            }
        }
    }

    @Test
    void everyRoomHasAnImageThatExistsAndFitsTheScreen() {
        for (String room : ROOM_SCRIPTS) {
            String art = dungeon.RoomScript.load(room).extra().get("art");
            List<String> lines = AsciiArt.load(art);
            assertEquals(12, lines.size(), room);
            for (String line : lines) {
                assertTrue(line.length() <= 56, room + ": " + line);
                assertTrue(line.chars().allMatch(ch -> ch >= 32 && ch < 127), room + ": nur ASCII");
            }
        }
    }

    @Test
    void padRightIgnoresColorCodes() {
        Ansi.setEnabled(true);
        assertEquals(10, Ansi.visibleLength(Ansi.padRight(Ansi.gold("abc"), 10)));
    }

    private static String stripCodes(String text) {
        return text.replaceAll("\u001B\\[[0-9;]*m", "");
    }
}

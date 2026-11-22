package game;

/**
 * ANSI-Farben (Truecolor) fuer die Konsolenausgabe. Standardmaessig aus (Tests, Dateiausgabe),
 * {@link Main} schaltet sie ein. Ist sie aus, bleibt der Text unveraendert.
 */
public final class Ansi {

    private static final String ESC = String.valueOf((char) 27);
    private static final String RESET = ESC + "[0m";

    private static boolean enabled = false;

    private Ansi() {
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    /** Raumname, Menuenummern, Rahmenlinien, Heilung und Funde. */
    public static String gold(String text) {
        return paint(255, 211, 77, text);
    }

    /** Erfolg. */
    public static String green(String text) {
        return paint(125, 220, 125, text);
    }

    /** Schaden. */
    public static String red(String text) {
        return paint(255, 107, 107, text);
    }

    /** Zauber. */
    public static String blue(String text) {
        return paint(90, 150, 255, text);
    }

    /** Ruestungsklasse. */
    public static String cyan(String text) {
        return paint(106, 212, 232, text);
    }

    /** Nebensaechliches: Beschreibung, "Was zuletzt geschah". */
    public static String grey(String text) {
        return paint(125, 119, 136, text);
    }

    /** Menuenummer in Gold, z. B. "[1]". */
    public static String number(int n) {
        return gold("[" + n + "]");
    }

    /** Beliebige Farbe, z. B. fuer die Bilder. */
    public static String rgb(int r, int g, int b, String text) {
        return paint(r, g, b, text);
    }

    /** Laenge ohne Farbcodes, damit sich eingefaerbter Text auffuellen laesst. */
    public static int visibleLength(String text) {
        return text.replaceAll("\u001B\\[[0-9;]*m", "").length();
    }

    /** Fuellt rechts mit Leerzeichen auf die sichtbare Breite auf (Farbcodes zaehlen nicht mit). */
    public static String padRight(String text, int width) {
        return text + " ".repeat(Math.max(0, width - visibleLength(text)));
    }

    private static String paint(int r, int g, int b, String text) {
        if (!enabled) {
            return text;
        }
        return ESC + "[38;2;" + r + ";" + g + ";" + b + "m" + text + RESET;
    }
}

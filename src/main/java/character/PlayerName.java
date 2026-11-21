package character;

import java.util.regex.Pattern;

public final class PlayerName {

    public static final int MAX_LENGTH = 15;

    // 1-15 Zeichen, nur Buchstaben (ohne Umlaute) und Zahlen, keine Leerzeichen
    private static final Pattern VALID = Pattern.compile("[A-Za-z0-9]{1," + MAX_LENGTH + "}");

    private PlayerName() {
    }

    public static boolean isValid(String name) {
        return name != null && VALID.matcher(name).matches();
    }
}

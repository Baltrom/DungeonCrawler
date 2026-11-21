package highscore;

import character.CharacterClass;

public record HighscoreEntry(String name, CharacterClass characterClass, int rooms) {

    /** Fuer die Anzeige, z. B. "Ria (Magier) - 5 Raeume". */
    public String describe() {
        return name + " (" + characterClass.getDisplayName() + ") - " + rooms + (rooms == 1 ? " Raum" : " Raeume");
    }
}

package dungeon;

/** Die acht Raumvorlagen. Raum 1-4 werden aus diesem Pool gezogen. Inhalt und Texte stehen in den Skriptdateien. */
public enum RoomTemplate {

    LIBRARY("library"),
    CRYPT("crypt"),
    STATUE_HALL("statue_hall"),
    TRAP_CORRIDOR("trap_corridor"),
    ALCHEMY_LAB("alchemy_lab"),
    PRISON("prison"),
    CISTERN("cistern"),
    ARMORY("armory");

    private final String textName;

    RoomTemplate(String textName) {
        this.textName = textName;
    }

    /** Name der Skriptdatei (src/main/resources/story/rooms/NAME.properties). */
    public String getTextName() {
        return textName;
    }

    public RoomScript getScript() {
        return RoomScript.load(textName);
    }

    public String getDisplayName() {
        return getScript().name();
    }
}

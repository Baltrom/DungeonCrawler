package character;

public class Player {

    private final String name;
    private final CharacterClass characterClass;
    private int maxHp;
    private int currentHp;

    public Player(String name, CharacterClass characterClass) {
        if (!PlayerName.isValid(name)) {
            throw new IllegalArgumentException("Invalid player name: " + name);
        }
        this.name = name;
        this.characterClass = characterClass;
        this.maxHp = characterClass.getMaxHp();
        this.currentHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage must not be negative: " + amount);
        }
        currentHp = Math.max(0, currentHp - amount);
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Healing must not be negative: " + amount);
        }
        currentHp = Math.min(maxHp, currentHp + amount);
    }
}

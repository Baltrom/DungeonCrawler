package character;

import item.Inventory;
import item.Item;
import item.ItemType;
import java.util.List;

public class Player {

    private final String name;
    private final CharacterClass characterClass;
    private final Inventory inventory = new Inventory();
    private int currentHp;
    private int spellCharges;

    public Player(String name, CharacterClass characterClass) {
        if (!PlayerName.isValid(name)) {
            throw new IllegalArgumentException("Invalid player name: " + name);
        }
        this.name = name;
        this.characterClass = characterClass;
        this.currentHp = characterClass.getMaxHp();
        this.spellCharges = characterClass.getSpellCharges();
    }

    public String getName() {
        return name;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    /** Klassenwert plus Boni aus dem Inventar. */
    public int getAttribute(Attribute attribute) {
        return characterClass.getAttribute(attribute) + inventory.attributeBonus(attribute);
    }

    public int getMaxHp() {
        return characterClass.getMaxHp() + inventory.maxHpBonus();
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getArmorClass() {
        return characterClass.getArmorClass() + inventory.armorBonus();
    }

    /** Bonus auf den Trefferwurf: Hauptattribut der Klasse plus Treffer-Boni. */
    public int getAttackBonus() {
        return getAttribute(characterClass.getAttackAttribute()) + inventory.hitBonus();
    }

    public int getDamageBonus() {
        return inventory.damageBonus();
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
        currentHp = Math.min(getMaxHp(), currentHp + amount);
    }

    /** Setzt Lebenspunkte und Zauberladungen nach dem Laden eines Spielstands. Items muessen schon im Inventar sein. */
    public void restoreState(int currentHp, int spellCharges) {
        if (currentHp < 1 || currentHp > getMaxHp()) {
            throw new IllegalArgumentException("Invalid HP: " + currentHp);
        }
        if (spellCharges < 0 || spellCharges > characterClass.getSpellCharges()) {
            throw new IllegalArgumentException("Invalid spell charges: " + spellCharges);
        }
        this.currentHp = currentHp;
        this.spellCharges = spellCharges;
    }

    // --- Zauberladungen ---

    public int getSpellCharges() {
        return spellCharges;
    }

    public boolean hasSpells() {
        return characterClass.getSpellCharges() > 0;
    }

    /** Verbraucht eine Ladung. Gibt false zurueck, wenn keine mehr da ist. */
    public boolean useSpellCharge() {
        if (spellCharges <= 0) {
            return false;
        }
        spellCharges--;
        return true;
    }

    public void refillSpellCharges() {
        spellCharges = characterClass.getSpellCharges();
    }

    // --- Inventar ---

    public List<Item> getItems() {
        return inventory.getItems();
    }

    public boolean hasItem(Item item) {
        return inventory.contains(item);
    }

    public boolean isInventoryFull() {
        return inventory.isFull();
    }

    /** Nimmt ein Item auf. Gibt false zurueck, wenn das Inventar voll ist. */
    public boolean addItem(Item item) {
        int maxBefore = getMaxHp();
        if (!inventory.add(item)) {
            return false;
        }
        // Ein Ring fuer mehr Lebenspunkte schenkt die zusaetzlichen Punkte auch sofort
        currentHp += getMaxHp() - maxBefore;
        return true;
    }

    /** Wirft ein Item weg. Es ist danach endgueltig weg. */
    public boolean removeItem(Item item) {
        boolean removed = inventory.remove(item);
        currentHp = Math.min(currentHp, getMaxHp());
        return removed;
    }

    /** Ob dieser Spieler den Trank gerade sinnvoll trinken kann. */
    public boolean canDrink(Item item) {
        if (item.getType() != ItemType.POTION || !inventory.contains(item)) {
            return false;
        }
        return switch (item) {
            case HEALING_POTION -> true;
            case CHARGE_POTION -> hasSpells();
            default -> false;
        };
    }

    public void drink(Item item) {
        if (!canDrink(item)) {
            throw new IllegalStateException("Cannot drink " + item);
        }
        inventory.remove(item);
        switch (item) {
            case HEALING_POTION -> heal(Item.HEALING_POTION_AMOUNT);
            case CHARGE_POTION -> refillSpellCharges();
            default -> throw new IllegalStateException("Not a potion: " + item);
        }
    }
}

package item;

import character.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {

    public static final int CAPACITY = 5;

    private final List<Item> items = new ArrayList<>();

    public boolean isFull() {
        return items.size() >= CAPACITY;
    }

    public boolean add(Item item) {
        if (isFull()) {
            return false;
        }
        items.add(item);
        return true;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int attributeBonus(Attribute attribute) {
        return items.stream().mapToInt(item -> item.getBonuses().attribute(attribute)).sum();
    }

    public int armorBonus() {
        return items.stream().mapToInt(item -> item.getBonuses().armorClass()).sum();
    }

    public int maxHpBonus() {
        return items.stream().mapToInt(item -> item.getBonuses().maxHp()).sum();
    }

    public int hitBonus() {
        return items.stream().mapToInt(item -> item.getBonuses().hit()).sum();
    }

    public int damageBonus() {
        return items.stream().mapToInt(item -> item.getBonuses().damage()).sum();
    }
}

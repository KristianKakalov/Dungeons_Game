package dungeons.gameLogic.characters.utilities;

import dungeons.messeges.Messages;
import dungeons.gameLogic.treasure.TreasureItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Backpack {

    private final static int MAX_CAPACITY_OF_BACKPACK = 10;
    private List<TreasureItem> backpack;

    public Backpack() {
        this.backpack = new ArrayList<>(MAX_CAPACITY_OF_BACKPACK);
    }

    public String addItem(TreasureItem item) {
        if (backpack.size() < MAX_CAPACITY_OF_BACKPACK) {
            backpack.add(item);
            return String.format(Messages.BACKPACK_ITEM_ADDED.toString(), item.getName());
        }
        return Messages.BACKPACK_FULL.toString();
    }

    public String removeItem(TreasureItem item) {
        backpack.remove(item);
        return item.getName() + Messages.BACKPACK_ITEM_REMOVED;
    }

    public TreasureItem getItem(int index) {
        return backpack.get(index);
    }

    public boolean isBackpackFull() {
        return backpack.size() == MAX_CAPACITY_OF_BACKPACK;
    }

    public TreasureItem dropRandomTreasure() {
        if (backpack.isEmpty()) {
            return null;
        }
        int itemIndex = new Random().nextInt(backpack.size());
        TreasureItem item = backpack.get(itemIndex);
        backpack.remove(itemIndex);
        return item;
    }

    @Override
    public String toString() {
        if (backpack.isEmpty()) {
            return Messages.BACKPACK_EMPTY.toString();
        }
        StringBuilder backpackString = new StringBuilder();
        for (int i = 0; i < backpack.size(); i++) {
            backpackString.append(i).append(". ")
                    .append(backpack.get(i))
                    .append(System.lineSeparator());
        }
        return backpackString.toString();
    }
}

package dungeons.gameLogic.treasure;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.map.Coordinates;

public class Potion extends BaseTreasureItem {

    public Potion(String name, int points, Coordinates coordinates, TreasureType type) {
        super(name, points, coordinates);
        this.treasureType = type;
    }

    @Override
    public String use(Hero hero) {
        return hero.drinkPotion(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

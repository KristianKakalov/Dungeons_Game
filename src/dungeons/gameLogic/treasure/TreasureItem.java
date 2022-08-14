package dungeons.gameLogic.treasure;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.map.Coordinates;

public interface TreasureItem {
    int getPoints();

    String getName();

    String use(Hero hero);

    TreasureType getType();

    Coordinates getCoordinates();

    void setCoordinates(Coordinates coordinates);
}

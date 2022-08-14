package dungeons.gameLogic.characters;

import dungeons.gameLogic.map.Coordinates;

public interface Actor {

    void takeDamage(int damage);

    int attack();

    int getLevel();

    Coordinates getCoordinates();

    void setCoordinates(Coordinates coordinates);

    String getSymbol();

    boolean isAlive();
}

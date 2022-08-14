package dungeons.gameLogic.treasure;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.map.Coordinates;

import java.util.Objects;

public abstract class BaseTreasureItem implements TreasureItem {

    protected TreasureType treasureType;
    protected String name;
    protected int points;
    private Coordinates coordinates;

    private static final int TYPE = 0;
    private static final int NAME = 1;
    private static final int POINTS = 2;
    private static final int LEVEL = 3;
    private static final int MIN_MANA = 4;

    public BaseTreasureItem(String name, int points, Coordinates coordinates) {
        this.points = points;
        this.name = name;
        this.coordinates = coordinates;
    }

    public static TreasureItem of(String line) {
        String[] tokens = line.split(";");

        TreasureType type = TreasureType.valueOf(tokens[TYPE]);

        return switch (type) {
            case WEAPON -> new Weapon(tokens[NAME],
                    Integer.parseInt(tokens[POINTS]),
                    Integer.parseInt(tokens[LEVEL]),
                    new Coordinates(0, 0));
            case SPELL -> new Spell(tokens[NAME],
                    Integer.parseInt(tokens[POINTS]),
                    Integer.parseInt(tokens[LEVEL]),
                    Integer.parseInt(tokens[MIN_MANA]),
                    new Coordinates(0, 0));
            case MANA_POTION, HEALTH_POTION -> new Potion(tokens[NAME],
                    Integer.parseInt(tokens[POINTS]),
                    new Coordinates(0, 0), type);
        };
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TreasureType getType() {
        return treasureType;
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTreasureItem that = (BaseTreasureItem) o;
        return points == that.points && Objects.equals(name, that.name) &&
                treasureType == that.treasureType && Objects.equals(coordinates, that.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, name, treasureType, coordinates);
    }

    @Override
    public String toString() {
        return
                treasureType + " " +
                        ", " + name +
                        ", points=" + points;
    }
}

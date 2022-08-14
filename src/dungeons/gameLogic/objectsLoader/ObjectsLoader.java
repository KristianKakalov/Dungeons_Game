package dungeons.gameLogic.objectsLoader;

import dungeons.gameLogic.characters.Minion;
import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.map.GameMap;
import dungeons.gameLogic.objectsLoader.exceptions.TreasureItemsNotLoadedCorrectlyException;
import dungeons.gameLogic.treasure.BaseTreasureItem;
import dungeons.gameLogic.treasure.TreasureItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectsLoader {

    private final Set<Coordinates> coordinatesSet;

    private final Map<Coordinates, Minion> minionsOnMap;
    private final Map<Coordinates, TreasureItem> treasuresOnMap;

    private static final int NUM_OF_MINIONS = 5;

    public ObjectsLoader(Path fileName) {
        this.coordinatesSet = new HashSet<>();
        this.minionsOnMap = new HashMap<>();
        this.treasuresOnMap = new HashMap<>();
        generateMinions();

        try {
            loadTreasureItems(new InputStreamReader(new FileInputStream(fileName.toFile())));
        } catch (FileNotFoundException e) {
            throw new TreasureItemsNotLoadedCorrectlyException("Could not load treasure items successfully");
        }
    }

    private void loadTreasureItems(Reader treasureItemsReader) {
        try (var reader = new BufferedReader(treasureItemsReader)) {
            List<TreasureItem> treasureItems = reader.lines()
                    .skip(1)
                    .map(BaseTreasureItem::of)
                    .toList();
            for (var item : treasureItems) {
                Coordinates coordinates = getRandomCoordinates();
                item.setCoordinates(coordinates);
                treasuresOnMap.put(coordinates, item);
            }
        } catch (IOException e) {
            throw new TreasureItemsNotLoadedCorrectlyException("Could not load treasure items successfully");
        }
    }

    private void generateMinions() {
        for (int i = 0; i < NUM_OF_MINIONS; i++) {
            Coordinates coordinates = getRandomCoordinates();
            int level = i + 1;
            minionsOnMap.put(coordinates, Minion.generateMinion(level, coordinates));
        }
    }

    private Coordinates getRandomCoordinates() {
        Coordinates coordinates =
                Coordinates.generateCoordinates(GameMap.WIDTH, GameMap.HEIGHT);
        while (coordinatesSet.contains(coordinates)) {
            coordinates =
                    Coordinates.generateCoordinates(GameMap.WIDTH, GameMap.HEIGHT);
        }
        coordinatesSet.add(coordinates);
        return coordinates;
    }

    public Set<Coordinates> coordinatesOfMinionsSet() {
        return minionsOnMap.keySet();
    }

    public Set<Coordinates> coordinatesOfTreasuresSet() {
        return treasuresOnMap.keySet();
    }

    public Minion getMinion(Coordinates coordinates) {
        return minionsOnMap.get(coordinates);
    }

    public void addMinion(Minion minion) {
        minionsOnMap.put(minion.getCoordinates(), minion);
    }

    public TreasureItem getTreasure(Coordinates coordinates) {
        return treasuresOnMap.get(coordinates);
    }

    public void removeTreasure(TreasureItem item) {
        treasuresOnMap.remove(item.getCoordinates());
    }

    public void addTreasure(TreasureItem item) {
        treasuresOnMap.put(item.getCoordinates(), item);
    }
}

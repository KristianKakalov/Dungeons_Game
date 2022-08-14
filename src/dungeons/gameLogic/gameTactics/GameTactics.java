package dungeons.gameLogic.gameTactics;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.characters.Minion;
import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.map.GameMap;
import dungeons.gameLogic.map.MapSymbols;
import dungeons.messeges.Messages;
import dungeons.gameLogic.objectsLoader.ObjectsLoader;
import dungeons.gameLogic.treasure.TreasureItem;
import dungeons.gameLogic.map.exceptions.InvalidMapCoordinatesException;

public class GameTactics {
    private final GameMap map;
    private final ObjectsLoader objectsLoader;

    public GameTactics(GameMap map, ObjectsLoader objectsLoader) {
        this.map = map;
        this.objectsLoader = objectsLoader;
    }

    public String moveHero(Hero hero, Direction direction) {
        Coordinates currentCoordinates = hero.getCoordinates();
        Coordinates coordinatesAfterMovement =
                Direction.coordinatesAfterMovement(hero.getCoordinates(), direction);
        String symbolAfterMovement;
        try {
            symbolAfterMovement = map.getSymbol(coordinatesAfterMovement);
        } catch (InvalidMapCoordinatesException e) {
            return Messages.INVALID_MOVE.toString();
        }
        MapSymbols mapSymbol = MapSymbols.fromString(symbolAfterMovement);
        if (mapSymbol != null) {
            switch (mapSymbol) {
                case FREE_SPOT -> {
                    return moveHeroToFreePosition(hero, currentCoordinates, coordinatesAfterMovement);
                }
                case OBSTACLE -> {
                    return Messages.INVALID_MOVE.toString();
                }
                case TREASURE -> {
                    TreasureItem item = objectsLoader.getTreasure(coordinatesAfterMovement);
                    objectsLoader.removeTreasure(item);
                    return collectTreasure(hero, item);
                }
                case MINION -> {
                    return fightWithMinion(hero, objectsLoader.getMinion(coordinatesAfterMovement));
                }
            }
        } else {
            changePreviousCoordinatesSymbol(hero, currentCoordinates);
            map.changeSymbol(coordinatesAfterMovement, hero.getSymbol() + symbolAfterMovement);
            hero.setCoordinates(coordinatesAfterMovement);
            return Messages.SUCCESSFUL_MOVE.toString();
        }
        return Messages.INVALID_MOVE.toString();
    }

    public String swapItem(Hero fromHero, Hero toHero, int index) {
        if (toHero.isBackpackFull()) {
            return Messages.OPPONENT_BACKPACK_FULL.toString();
        }
        if (!fromHero.getCoordinates().equals(toHero.getCoordinates())) {
            return Messages.NOT_ON_SAME_SPOT.toString();
        }
        try {
            TreasureItem item = fromHero.getFromBackpack(index);
            toHero.addToBackpack(item);
            fromHero.removeFromBackpack(index);
            return item.getName() + Messages.ITEM_SWAPPED;
        } catch (IndexOutOfBoundsException e) {
            return Messages.ITEM_NOT_FOUND_IN_BACKPACK.toString();
        }
    }

    public String heroUseItemFromBackpack(Hero hero, int index) {
        try {
            TreasureItem item = hero.getFromBackpack(index);
            return item.use(hero);
        } catch (IndexOutOfBoundsException e) {
            return Messages.ITEM_NOT_FOUND_IN_BACKPACK.toString();
        }
    }

    public String heroRemoveItemFromBackpack(Hero hero, int index) {
        try {
            TreasureItem item = hero.getFromBackpack(index);
            Coordinates coordinates = hero.getCoordinates();
            item.setCoordinates(coordinates);
            objectsLoader.addTreasure(item);
            map.changeSymbol(coordinates, hero.getSymbol() + MapSymbols.TREASURE);
            return hero.removeFromBackpack(index);
        } catch (IndexOutOfBoundsException e) {
            return Messages.ITEM_NOT_FOUND_IN_BACKPACK.toString();
        }
    }

    public String heroesFight(Hero hero1, Hero hero2) {
        if (!hero1.getCoordinates().equals(hero2.getCoordinates())) {
            return Messages.NOT_ON_SAME_SPOT.toString();
        }
        boolean hero1Won = false;
        while (true) {
            hero2.takeDamage(hero1.attack());
            if (!hero2.isAlive()) {
                hero1Won = true;
                break;
            }
            hero1.takeDamage(hero2.attack());
            if (!hero1.isAlive()) {
                break;
            }
        }
        if (hero1Won) {
            removeHeroFromMap(hero2);
            dropItemFromBackpack(hero2);
            return Messages.PLAYER_KILLED_OTHER_PLAYER.toString() + hero2;
        } else {
            removeHeroFromMap(hero1);
            dropItemFromBackpack(hero1);
            return Messages.PLAYER_KILLED.toString() + hero2;
        }
    }

    private String moveHeroToFreePosition(Hero hero,
                                          Coordinates currentCoordinates,
                                          Coordinates coordinatesAfterMovement) {
        changePreviousCoordinatesSymbol(hero, currentCoordinates);
        map.changeSymbol(coordinatesAfterMovement, hero.getSymbol());
        hero.setCoordinates(coordinatesAfterMovement);
        return Messages.SUCCESSFUL_MOVE.toString();

    }

    private String collectTreasure(Hero hero, TreasureItem item) {
        changePreviousCoordinatesSymbol(hero, hero.getCoordinates());
        String message = hero.addToBackpack(item);

        if (message.equals(Messages.BACKPACK_FULL.toString())) {
            map.changeSymbol(item.getCoordinates(), hero.getSymbol() + MapSymbols.TREASURE);
        } else {
            map.changeSymbol(item.getCoordinates(), hero.getSymbol());
        }

        hero.setCoordinates(item.getCoordinates());
        return message;
    }

    private String fightWithMinion(Hero hero, Minion minion) {
        changePreviousCoordinatesSymbol(hero, hero.getCoordinates());
        boolean heroWon = false;
        while (true) {
            minion.takeDamage(hero.attack());
            if (!minion.isAlive()) {
                heroWon = true;
                break;
            }
            hero.takeDamage(minion.attack());
            if (!hero.isAlive()) {
                break;
            }
        }
        if (heroWon) {
            map.changeSymbol(minion.getCoordinates(), hero.getSymbol());
            hero.setCoordinates(minion.getCoordinates());
            int xp = minion.giveXP();
            hero.increaseXP(xp);
            spawnNewMinion(minion.getLevel());
            return String.format(Messages.MINION_KILLED.toString(), xp);
        } else {
            dropItemFromBackpack(hero);
            return Messages.PLAYER_KILLED.toString() + minion;
        }
    }

    private void changePreviousCoordinatesSymbol(Hero hero, Coordinates currentCoordinates) {
        String currentSymbol = map.getSymbol(currentCoordinates);
        if (currentSymbol.equals(hero.getSymbol())) {
            map.changeSymbol(currentCoordinates, MapSymbols.FREE_SPOT.toString());
        } else {
            map.changeSymbol(currentCoordinates, currentSymbol.replace(hero.getSymbol(), ""));
        }
    }

    private void spawnNewMinion(int level) {
        Coordinates coordinates = getRandomFreeCoordinates();
        Minion minion = Minion.generateMinion(level, coordinates);
        map.changeSymbol(coordinates, MapSymbols.MINION.toString());
        objectsLoader.addMinion(minion);
    }

    public Coordinates getRandomFreeCoordinates() {
        return map.generateRandomFreeCoordinates();
    }

    public void spawnHero(Hero hero) {
        map.changeSymbol(hero.getCoordinates(), hero.getSymbol());
    }

    public void removeHeroFromMap(Hero hero) {
        Coordinates coordinates = hero.getCoordinates();
        String symbol = map.getSymbol(coordinates);
        if (symbol.length() == 2) {
            map.changeSymbol(coordinates, symbol.replace(hero.getSymbol(), ""));
        } else {
            map.changeSymbol(coordinates, MapSymbols.FREE_SPOT.toString());
        }
    }

    public String getMap() {
        return map.gameMapString();
    }

    private void dropItemFromBackpack(Hero hero) {
        TreasureItem item = hero.dropItemFromBackpack();
        if (item == null) {
            return;
        }
        Coordinates coordinates = map.generateRandomFreeCoordinates();
        item.setCoordinates(coordinates);
        objectsLoader.addTreasure(item);
        map.changeSymbol(coordinates, MapSymbols.TREASURE.toString());
    }

    public String getOtherPlayerSymbol(Hero hero) {
        String symbols = map.getSymbol(hero.getCoordinates());
        if (symbols.length() != 2) {
            return Messages.NOT_ON_SAME_SPOT.toString();
        }
        String otherPlayerSymbol = symbols.replace(hero.getSymbol(), "");
        return validPlayerSymbol(otherPlayerSymbol) ?
                otherPlayerSymbol : Messages.NOT_ON_SAME_SPOT.toString();
    }

    private boolean validPlayerSymbol(String symbol) {
        return symbol.equals("1") ||
                symbol.equals("2") ||
                symbol.equals("3") ||
                symbol.equals("4") ||
                symbol.equals("5") ||
                symbol.equals("6") ||
                symbol.equals("7") ||
                symbol.equals("8") ||
                symbol.equals("9");
    }
}

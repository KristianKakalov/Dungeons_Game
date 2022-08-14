package dungeons.online;

import dungeons.gameLogic.gameTactics.GameTactics;
import dungeons.gameLogic.map.GameMap;
import dungeons.gameLogic.objectsLoader.ObjectsLoader;
import dungeons.online.command.CommandExecutor;
import dungeons.online.server.Server;
import dungeons.online.storage.UserRepository;

import java.io.File;
import java.nio.file.Path;

public class DungeonOnline {

    private static final String FILE_NAME = "resources" + File.separator + "TreasureItems.csv";

    public static void main(String[] args) {
        ObjectsLoader objectsLoader = new ObjectsLoader(Path.of(FILE_NAME));
        GameMap map = new GameMap(objectsLoader.coordinatesOfMinionsSet(),
                objectsLoader.coordinatesOfTreasuresSet());
        GameTactics game = new GameTactics(map, objectsLoader);

        CommandExecutor commandExecutor = new CommandExecutor(game, new UserRepository());

        new Server(commandExecutor).start();
    }
}

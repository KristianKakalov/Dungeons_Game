package dungeons.online;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.gameTactics.Direction;
import dungeons.gameLogic.gameTactics.GameTactics;
import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.treasure.Potion;
import dungeons.gameLogic.treasure.TreasureItem;
import dungeons.gameLogic.treasure.TreasureType;
import dungeons.messeges.Messages;
import dungeons.online.command.Command;
import dungeons.online.command.CommandCreator;
import dungeons.online.command.CommandExecutor;
import dungeons.online.storage.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandsTest {

    private GameTactics gameMock = Mockito.mock(GameTactics.class);
    private UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
    private CommandExecutor commandExecutor = new CommandExecutor(gameMock, userRepositoryMock);

    @Test
    public void testExecuteMoveNotConnected() {
        Command cmd = CommandCreator.newCommand("down");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(false);

        String expectedMsg = Messages.USER_NOT_PLAYING.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Player not connected to lobby");
    }

    @Test
    public void testExecuteMove() {
        Command cmd = CommandCreator.newCommand("down");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock)).thenReturn(hero);
        when(gameMock.moveHero(hero, Direction.DOWN)).thenReturn(Messages.SUCCESSFUL_MOVE.toString());

        String expectedMsg = Messages.SUCCESSFUL_MOVE.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Player not moved correctly");
    }

    @Test
    public void testExecuteStats() {
        Command cmd = CommandCreator.newCommand("stats");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock)).thenReturn(hero);

        String expectedMsg = hero.displayStats();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Player stats not displayed correctly");
    }

    @Test
    public void testExecuteBackpack() {
        Command cmd = CommandCreator.newCommand("backpack");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock)).thenReturn(hero);

        String expectedMsg = hero.displayBackpack();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Player backpack not displayed correctly");
    }

    @Test
    public void testExecuteUse() {
        Command cmd = CommandCreator.newCommand("use 0");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);

        Hero hero = new Hero("1", new Coordinates(0, 0));
        TreasureItem item = new Potion("potion", 10, new Coordinates(0, 0), TreasureType.HEALTH_POTION);
        hero.addToBackpack(item);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock)).thenReturn(hero);
        when(gameMock.heroUseItemFromBackpack(hero, 0)).thenReturn("+10 health");

        String expectedMsg = item.use(hero);
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Player didn't use item correctly");
    }

    @Test
    public void testExecuteUseWithInvalidArgument() {
        Command cmd = CommandCreator.newCommand("use invalid");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);

        String expectedMsg = Messages.INVALID_COMMAND.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Invalid arguments not handled correctly");
    }

    @Test
    public void testExecuteRemove() {
        Command cmd = CommandCreator.newCommand("remove 0");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);

        Hero hero = new Hero("1", new Coordinates(0, 0));
        TreasureItem item = new Potion("potion", 10, new Coordinates(0, 0), TreasureType.HEALTH_POTION);
        hero.addToBackpack(item);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock)).thenReturn(hero);
        when(gameMock.heroRemoveItemFromBackpack(hero, 0)).thenReturn("potion removed");

        String expectedMsg = hero.removeFromBackpack(0);
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Player didn't remove item correctly");
    }

    @Test
    public void testExecuteRemoveWithInvalidArgument() {
        Command cmd = CommandCreator.newCommand("remove 2.2");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);

        String expectedMsg = Messages.INVALID_COMMAND.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);
        assertEquals(expectedMsg, actualMsg, "Invalid arguments not handled correctly");
    }

    @Test
    public void testExecuteSwap() {
        Command cmd = CommandCreator.newCommand("swap 0");
        SocketChannel socketChannelMock1 = Mockito.mock(SocketChannel.class);
        SocketChannel socketChannelMock2 = Mockito.mock(SocketChannel.class);

        Hero hero1 = new Hero("1", new Coordinates(0, 0));
        Hero hero2 = new Hero("2", new Coordinates(0, 0));

        Map.Entry<SocketChannel, Hero> otherPlayerInfoMock = Mockito.mock(Map.Entry.class);

        TreasureItem item = new Potion("potion", 10, new Coordinates(0, 0), TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock1)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock1)).thenReturn(hero1);
        when(gameMock.getOtherPlayerSymbol(hero1)).thenReturn("2");
        when(userRepositoryMock.getPlayerInfoBySymbol("2")).thenReturn(otherPlayerInfoMock);
        when(otherPlayerInfoMock.getValue()).thenReturn(hero2);
        when(gameMock.swapItem(hero1, hero2, 0)).thenReturn("potion swapped");
        when(otherPlayerInfoMock.getKey()).thenReturn(socketChannelMock2);

        String expectedMsg = "potion swapped";
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock1);
        assertEquals(expectedMsg, actualMsg, "Players didn't swap item correctly");
    }

    @Test
    public void testExecuteFightAndWin() {
        Command cmd = CommandCreator.newCommand("fight");
        SocketChannel socketChannelMock1 = Mockito.mock(SocketChannel.class);
        SocketChannel socketChannelMock2 = Mockito.mock(SocketChannel.class);

        Hero hero1 = new Hero("1", new Coordinates(0, 0));
        Hero hero2 = new Hero("2", new Coordinates(0, 0));

        Map.Entry<SocketChannel, Hero> otherPlayerInfoMock = Mockito.mock(Map.Entry.class);

        TreasureItem item = new Potion("potion", 10, new Coordinates(0, 0), TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock1)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock1)).thenReturn(hero1);
        when(gameMock.getOtherPlayerSymbol(hero1)).thenReturn("2");
        when(userRepositoryMock.getPlayerInfoBySymbol("2")).thenReturn(otherPlayerInfoMock);
        when(otherPlayerInfoMock.getValue()).thenReturn(hero2);
        when(gameMock.heroesFight(hero1, hero2)).thenReturn("You killed 2");
        when(otherPlayerInfoMock.getKey()).thenReturn(socketChannelMock2);

        String expectedMsg = "You killed 2";
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock1);
        assertEquals(expectedMsg, actualMsg, "Players didn't fight correctly");
    }

    @Test
    public void testExecuteFightAndLose() {
        Command cmd = CommandCreator.newCommand("fight");
        SocketChannel socketChannelMock1 = Mockito.mock(SocketChannel.class);
        SocketChannel socketChannelMock2 = Mockito.mock(SocketChannel.class);

        Hero hero1 = new Hero("1", new Coordinates(0, 0));
        Hero hero2 = new Hero("2", new Coordinates(0, 0));

        Map.Entry<SocketChannel, Hero> otherPlayerInfoMock = Mockito.mock(Map.Entry.class);

        TreasureItem item = new Potion("potion", 10, new Coordinates(0, 0), TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock1)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock1)).thenReturn(hero1);
        when(gameMock.getOtherPlayerSymbol(hero1)).thenReturn("2");
        when(userRepositoryMock.getPlayerInfoBySymbol("2")).thenReturn(otherPlayerInfoMock);
        when(otherPlayerInfoMock.getValue()).thenReturn(hero2);
        when(gameMock.heroesFight(hero1, hero2)).thenReturn("You died from 2");
        when(otherPlayerInfoMock.getKey()).thenReturn(socketChannelMock2);

        String expectedMsg = "You died from 2";
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock1);
        assertEquals(expectedMsg, actualMsg, "Players didn't fight correctly");
    }

    @Test
    public void testExecuteSwapAndPlayersNotOnSameSpot() {
        Command cmd = CommandCreator.newCommand("swap 0");
        SocketChannel socketChannelMock1 = Mockito.mock(SocketChannel.class);

        Hero hero1 = new Hero("1", new Coordinates(0, 0));

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock1)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock1)).thenReturn(hero1);
        when(gameMock.getOtherPlayerSymbol(hero1)).thenReturn(Messages.NOT_ON_SAME_SPOT.toString());

        String expectedMsg = Messages.NOT_ON_SAME_SPOT.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock1);
        assertEquals(expectedMsg, actualMsg, "Players can't swap items on different spots");
    }

    @Test
    public void testExecuteSwapOtherPlayerNotFound() {
        Command cmd = CommandCreator.newCommand("swap 0");
        SocketChannel socketChannelMock1 = Mockito.mock(SocketChannel.class);

        Hero hero1 = new Hero("1", new Coordinates(0, 0));

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock1)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock1)).thenReturn(hero1);
        when(gameMock.getOtherPlayerSymbol(hero1)).thenReturn("2");
        when(userRepositoryMock.getPlayerInfoBySymbol("2")).thenReturn(null);

        String expectedMsg = Messages.HERO_NOT_FOUND.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock1);
        assertEquals(expectedMsg, actualMsg, "Other player should not be connected");
    }

    @Test
    public void testExecuteSwapWithInvalidArguments() {
        Command cmd = CommandCreator.newCommand("swap helloWorld");
        SocketChannel socketChannelMock1 = Mockito.mock(SocketChannel.class);
        SocketChannel socketChannelMock2 = Mockito.mock(SocketChannel.class);

        Hero hero1 = new Hero("1", new Coordinates(0, 0));
        Hero hero2 = new Hero("2", new Coordinates(0, 0));

        Map.Entry<SocketChannel, Hero> otherPlayerInfoMock = Mockito.mock(Map.Entry.class);

        TreasureItem item = new Potion("potion", 10, new Coordinates(0, 0), TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        when(userRepositoryMock.isPlayerPlaying(socketChannelMock1)).thenReturn(true);
        when(userRepositoryMock.getHero(socketChannelMock1)).thenReturn(hero1);
        when(gameMock.getOtherPlayerSymbol(hero1)).thenReturn("2");
        when(userRepositoryMock.getPlayerInfoBySymbol("2")).thenReturn(otherPlayerInfoMock);
        when(otherPlayerInfoMock.getValue()).thenReturn(hero2);
        when(gameMock.swapItem(hero1, hero2, 0)).thenReturn("potion swapped");
        when(otherPlayerInfoMock.getKey()).thenReturn(socketChannelMock2);

        String expectedMsg = Messages.INVALID_COMMAND.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock1);
        assertEquals(expectedMsg, actualMsg, "Invalid arguments not handled correctly");
    }

    @Test
    public void testExecuteInvalidCommand() {
        Command cmd = CommandCreator.newCommand("invalid command");
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        when(userRepositoryMock.isPlayerPlaying(socketChannelMock)).thenReturn(true);

        String expectedMsg = Messages.UNKNOWN_COMMAND.toString();
        String actualMsg = commandExecutor.execute(cmd, socketChannelMock);

        assertEquals(expectedMsg, actualMsg, "Invalid command not handled correctly");
    }
}
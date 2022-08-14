package dungeons.online;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.gameTactics.GameTactics;
import dungeons.messeges.Messages;
import dungeons.online.storage.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest {

    private UserRepository userRepository = new UserRepository();

    @Test
    public void testConnectPlayer() {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        GameTactics gameTacticsMock = Mockito.mock(GameTactics.class);

        String expectedMsg = "You have connected as 1";
        String actualMsg = userRepository.connectPlayer(socketChannelMock, gameTacticsMock);

        Collection<SocketChannel> socketChannelsSet = userRepository.getSocketChannels();

        assertEquals(expectedMsg, actualMsg, "Player didn't connect successfully");
        assertTrue(socketChannelsSet.contains(socketChannelMock), "Player socket channel not saved");
    }

    @Test
    public void testConnectPlayerLobbyFull() {
        GameTactics gameTacticsMock = Mockito.mock(GameTactics.class);

        for (int i = 0; i < 9; i++) {
            SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
            String message = userRepository.connectPlayer(socketChannelMock, gameTacticsMock);
            assertTrue(message.contains("You have connected as"),
                    "Lobby couldn't connect expected number of players");
        }
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        String expectedMsg = Messages.LOBBY_IS_FULL.toString();
        String actualMsg = userRepository.connectPlayer(socketChannelMock, gameTacticsMock);
        assertEquals(expectedMsg, actualMsg, "Lobby should be full");
    }

    @Test
    public void testConnectPlayerAlreadyConnected() {
        GameTactics gameTacticsMock = Mockito.mock(GameTactics.class);
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        userRepository.connectPlayer(socketChannelMock, gameTacticsMock);

        String expectedMsg = Messages.ALREADY_CONNECTED.toString();
        String actualMsg = userRepository.connectPlayer(socketChannelMock, gameTacticsMock);

        assertEquals(expectedMsg, actualMsg, "Player should be saved already");
    }

    @Test
    public void testRemovePlayer() {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        GameTactics gameTacticsMock = Mockito.mock(GameTactics.class);

        String expectedMsg = "You have connected as 1";
        String actualMsg = userRepository.connectPlayer(socketChannelMock, gameTacticsMock);

        assertEquals(expectedMsg, actualMsg, "Player didn't connect successfully");

        userRepository.removePlayer(socketChannelMock, gameTacticsMock);

        assertFalse(userRepository.isPlayerPlaying(socketChannelMock), "Player socket should have been removed");
    }

    @Test
    public void testGetPlayerInfo() {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        GameTactics gameTacticsMock = Mockito.mock(GameTactics.class);

        String expectedMsg = "You have connected as 1";
        String actualMsg = userRepository.connectPlayer(socketChannelMock, gameTacticsMock);

        assertEquals(expectedMsg, actualMsg, "Player didn't connect successfully");

        Map.Entry<SocketChannel, Hero> socketWithHero = userRepository.getPlayerInfoBySymbol("1");
        Hero hero = userRepository.getHero(socketChannelMock);

        assertEquals(hero, socketWithHero.getValue(),
                "User repository didn't return correct pair of hero and socket");
    }
}
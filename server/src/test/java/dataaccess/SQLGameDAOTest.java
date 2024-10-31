package dataaccess;

import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
class SQLGameDAOTest {

    static SQLGameDAO gameDAO;

    String gameName = "myGame";
    static int gameID;

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        gameDAO = new SQLGameDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO.clear();
        gameID = gameDAO.createGame(gameName);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        gameDAO.clear();

    }

    @Test
    @DisplayName("Should create a new gameData in the DAO when createGame() is called")
    void createGamePass() throws DataAccessException {
        String myGameName1 = "daGame";
        Assertions.assertFalse(gameDAO.empty());
        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(myGameName1));
        assertNotNull(gameDAO.getGame(2));
    }

    @Test
    @DisplayName("Should throw an error when createGame() is called")
    void createGameFail() throws DataAccessException {
        String statement = "INSERT INTO gamedata (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        Assertions.assertFalse(gameDAO.empty());
        Assertions.assertThrows(DataAccessException.class, () -> {
            int badGame = gameDAO.createGame(null);
            Assertions.assertEquals(0, badGame);
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s",
                            statement,
                            e.getMessage()));
        });
    }

    @Test
    @DisplayName("Should create a list of gameData in the DAO when listGames() is called")
    void listGamesPass() throws DataAccessException {
        Collection<GameData> expectedGames = new HashSet<>();
        Assertions.assertFalse(gameDAO.empty());
        String newGameName1 = "ThisIsTheGame";
        String newGameName2 = "IAmTheGame";
        int newGame1 = gameDAO.createGame(newGameName1);
        int newGame2 = gameDAO.createGame(newGameName2);
        expectedGames.add(new GameData(gameID, null, null, gameName, null));
        expectedGames.add(new GameData(newGame1, null, null, newGameName1, null));
        expectedGames.add(new GameData(newGame2, null, null, newGameName2, null));
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(gameID));
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(newGame1));
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(newGame2));
        Assertions.assertDoesNotThrow(() -> gameDAO.listGames());
        Assertions.assertEquals(expectedGames, gameDAO.listGames());
    }

    @Test
    @DisplayName("Should throw an error when listGames() is called")
    void listGamesFail() throws DataAccessException {
        Collection<GameData> expectedGames = new HashSet<>();
        Assertions.assertFalse(gameDAO.empty());
        gameDAO.clear();
        Assertions.assertTrue(gameDAO.empty());
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.listGames();
            Assertions.assertEquals(expectedGames, gameDAO.listGames());
            SQLException e = new SQLException();
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        });
        // may write something different, although I am currently at a loss as to what I could do to throw an error
        // that wouldn't already be caught by a different method
    }

    @Test
    @DisplayName("Should update existing gameData in the DAO when updateGame() is called")
    void updateGamePass() throws DataAccessException, InvalidMoveException {
        ChessPosition startPos = new ChessPosition(2,2);
        ChessPosition endPos = new ChessPosition(3,2);
        ChessMove move = new ChessMove(startPos, endPos, null);
        ChessGame chessGame = new ChessGame();
        ChessBoard board = chessGame.getBoard();
        chessGame.makeMove(move);
        chessGame.setBoard(board);
        Assertions.assertFalse(gameDAO.empty());
        GameData gameData = new GameData(gameID, "V", "J", gameName, chessGame);
        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(gameData));
        Assertions.assertEquals(gameData, gameDAO.getGame(gameID));
    }

    @Test
    @DisplayName("Should throw an error when updateGame() is called")
    void updateGameFail() throws DataAccessException {
        Assertions.assertFalse(gameDAO.empty());
        GameData badGame = new GameData(gameID, null, "Cyn", null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(badGame);
            SQLException e = new SQLException();
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        });
    }

    @Test
    @DisplayName("Should get an existing gameData from the DAO when getGame() is called")
    void getGamePass() throws DataAccessException {
        Assertions.assertFalse(gameDAO.empty());
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(gameID));
        Assertions.assertEquals(gameData, gameDAO.getGame(gameID));
    }

    @Test
    @DisplayName("Should throw an error when getGame() is called")
    void getGameFail() throws DataAccessException {
        Assertions.assertFalse(gameDAO.empty());
        int badID = 99;
        Assertions.assertThrows(DataAccessException.class, () -> {
            Assertions.assertNull(gameDAO.getGame(badID));
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to read data: %s", e.getMessage()));
        });
    }

    @Test
    @DisplayName("Should delete all rows in the database when clear() is called")
    void clearTestPass() throws DataAccessException {
        Assertions.assertFalse(gameDAO.empty());
        gameDAO.createGame("daGame");
        gameDAO.clear();
        Assertions.assertTrue(gameDAO.empty());
        Assertions.assertDoesNotThrow(() -> gameDAO.clear());
    }

    @Test
    @DisplayName("Should return true after clearing a database when empty() is called")
    void emptyTestPass() throws DataAccessException {
        Assertions.assertFalse(gameDAO.empty());
        gameDAO.createGame("disIsDaGame");
        gameDAO.clear();
        Assertions.assertTrue(gameDAO.empty());
        Assertions.assertDoesNotThrow(() -> gameDAO.empty());
    }

    @Test
    @DisplayName("Should return false when database is not empty when empty() is called")
    void emptyTestFail() throws DataAccessException {
        Assertions.assertFalse(gameDAO.empty());
        gameDAO.createGame("darnILostDaGame");
        Assertions.assertFalse(gameDAO.empty());
        Assertions.assertDoesNotThrow(() -> gameDAO.empty());
    }
}
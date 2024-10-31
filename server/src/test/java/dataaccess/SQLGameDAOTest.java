package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
class SQLGameDAOTest {

    static SQLGameDAO gameDAO;

    String gameName = "myGame";

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        gameDAO = new SQLGameDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO.clear();
        gameDAO.createGame(gameName);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        gameDAO.clear();

    }

    @Test
    void createGame() {
    }

    @Test
    void listGames() {
    }

    @Test
    void updateGame() {
    }

    @Test
    void getGame() {
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
package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import static dataaccess.SQLGameDAOTest.gameDAO;
import static org.junit.jupiter.api.Assertions.*;
class SQLAuthDAOTest {

    static SQLAuthDAO authDAO;

    static AuthData authData = new AuthData("daToken", "Fido");

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        authDAO = new SQLAuthDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO.clear();
        authDAO.createAuth(authData);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        authDAO.clear();

    }

    @Test
    void createAuth() {
    }

    @Test
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    @DisplayName("Should delete all rows in the database when clear() is called")
    void clearTestPass() throws DataAccessException {
        Assertions.assertFalse(authDAO.empty());
        AuthData authData1 = new AuthData("myToken", "N");
        authDAO.createAuth(authData1);
        authDAO.clear();
        Assertions.assertTrue(authDAO.empty());
        Assertions.assertDoesNotThrow(() -> authDAO.clear());
    }

    @Test
    @DisplayName("Should return true after clearing a database when empty() is called")
    void emptyTestPass() throws DataAccessException {
        Assertions.assertFalse(authDAO.empty());
        AuthData authData1 = new AuthData("myToken", "N");
        authDAO.createAuth(authData1);
        authDAO.clear();
        Assertions.assertTrue(authDAO.empty());
        Assertions.assertDoesNotThrow(() -> authDAO.empty());
    }

    @Test
    @DisplayName("Should return false when database is not empty when empty() is called")
    void emptyTestFail() throws DataAccessException {
        Assertions.assertFalse(authDAO.empty());
        AuthData authDataBad = new AuthData("AbsoluteSolver", "Cyn");
        authDAO.createAuth(authDataBad);
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertDoesNotThrow(() -> authDAO.empty());
    }
}
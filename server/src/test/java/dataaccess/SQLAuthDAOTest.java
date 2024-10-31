package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

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
    @DisplayName("Should create a new authData in the DAO when createAuth() is called")
    void createAuthPass() throws DataAccessException {
        AuthData goodAuth = new AuthData("BiteMe", "Uzi");
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(goodAuth));
        Assertions.assertTrue(authDAO.getAuth("BiteMe") != null);
    }

    @Test
    @DisplayName("Should throw an error when createAuth() is called")
    void createAuthFail() throws DataAccessException {
        AuthData authBad = new AuthData(null, "Cyn");
        String statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(authBad);
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s",
                            statement,
                            e.getMessage()));
        });
    }

    @Test
    @DisplayName("Should get an existing authData in the DAO when getAuth() is called")
    void getAuthPass() throws DataAccessException {
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authData.authToken()));
        Assertions.assertEquals(authData, authDAO.getAuth(authData.authToken()));
    }

    @Test
    @DisplayName("Should throw an error when getAuth() is called")
    void getAuthFail() throws DataAccessException {
        Assertions.assertFalse(authDAO.empty());
        AuthData authBad = new AuthData("ANGRY", "Cyn");
        Assertions.assertThrows(DataAccessException.class, () -> {
            Assertions.assertNull(authDAO.getAuth(authBad.authToken()));
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to read data: %s", e.getMessage()));
        });
    }

    @Test
    @DisplayName("Should delete an existing authData in the DAO when deleteAuth() is called")
    void deleteAuthPass() throws DataAccessException  {
        AuthData goodAuth = new AuthData("DoorMaster", "Khan");
        Assertions.assertFalse(authDAO.empty());
        authDAO.createAuth(goodAuth);
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(goodAuth.authToken()));
        assertNull(authDAO.getAuth(goodAuth.authToken()));
    }

    @Test
    @DisplayName("Should throw an error when deleteAuth() is called")
    void deleteAuthFail() throws DataAccessException  {
        String statement = "DELETE FROM authdata WHERE authToken=?";
        AuthData badAuth = new AuthData("dino", "Barney");
        Assertions.assertFalse(authDAO.empty());
        authDAO.createAuth(badAuth);
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("dno");
            // shows that badAuth has not been deleted
            Assertions.assertNotNull(authDAO.getAuth(badAuth.authToken()));
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s",
                            statement,
                            e.getMessage()));
        });
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
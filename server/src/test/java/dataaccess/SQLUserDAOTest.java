package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
class SQLUserDAOTest {

    static SQLUserDAO userDAO;
    static UserData user1 = new UserData("Fido", "BigChungas", "ThisIsNotAWebsite.com");

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        userDAO = new SQLUserDAO();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO.clear();
        userDAO.createUser(user1);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        userDAO.clear();

    }


    @Test
    @DisplayName("Should create a new user in the DAO when createUser() is called")
    void createUserTestPass() throws DataAccessException {
        UserData userGood = new UserData("N", "goldenRetrieversRAwesome", "GoodBoiN@JCJenson.com");
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(userGood));
        Assertions.assertTrue(userDAO.getUser("N") != null);


    }

    @Test
    @DisplayName("Should throw an error when createUser() is called")
    void createUserTestFail() throws DataAccessException, SQLException {
        UserData userBad = new UserData("Cyn", "getSnuckUpon", null);
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(userBad);
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s",
                            statement,
                            e.getMessage()));
        });

    }

    @Test
    @DisplayName("Should get an existing user in the DAO when getUser() is called")
    void getUserPass() throws DataAccessException {
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertDoesNotThrow(() -> userDAO.getUser(user1.username()));
        Assertions.assertEquals(user1, userDAO.getUser(user1.username()));
    }

    @Test
    @DisplayName("Should throw an error when getUser() is called, as no such user exists")
    void getUserFail() {
        Assertions.assertFalse(userDAO.empty());
        UserData userBad = new UserData("Cyn", "getSnuckUpon", "annoyedExpression");
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(userBad);
            SQLException e = new SQLException();
            throw new DataAccessException(
                    String.format("Unable to read data: %s", e.getMessage()));
        });
    }

    @Test
    @DisplayName("Should delete all rows in the database when clear() is called")
    void clearTestPass() throws DataAccessException {
        Assertions.assertFalse(userDAO.empty());
        userDAO.createUser(new UserData("Manny", "BOBO", "woof"));
        userDAO.clear();
        Assertions.assertTrue(userDAO.empty());
        Assertions.assertDoesNotThrow(() -> userDAO.clear());
    }

    @Test
    @DisplayName("Should return true after clearing a database when empty() is called")
    void emptyTestPass() throws DataAccessException {
        Assertions.assertFalse(userDAO.empty());
        userDAO.createUser(new UserData("Manny", "BOBO", "woof"));
        userDAO.clear();
        Assertions.assertTrue(userDAO.empty());
        Assertions.assertDoesNotThrow(() -> userDAO.empty());
    }

    @Test
    @DisplayName("Should return false when database is not empty when empty() is called")
    void emptyTestFail() throws DataAccessException {
        Assertions.assertFalse(userDAO.empty());
        userDAO.createUser(new UserData("Manny", "BOBO", "woof"));
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertDoesNotThrow(() -> userDAO.empty());
    }
}
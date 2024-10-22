package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static MemoryGameDAO gameDAO = new MemoryGameDAO();
    AuthData authData = new AuthData("myToken", "Fido");

    static ClearService clearService = new ClearService(new MemoryUserDAO(), gameDAO, authDAO);
    static GameService gameService = new GameService(authDAO, gameDAO);

    @BeforeEach
    void setUp() throws DataAccessException {
        clearService.clear();
        authDAO.createAuth(authData);
    }

    @AfterAll
    public static void tearDown() throws DataAccessException {
        /*
        After all tests are run, clear the database
         */
        clearService.clear();
    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }

    @Test
    void joinGame() {
    }
}
package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import service.ClearService;

public class ClearServiceTest {
    /*
    Variables in the class:
    1) the class you are testing (the one with the functions you are testing
    2) All classes that are required for the class you are testing
        ex. user class or a chess game class, or dao class?
        It all depends on what function you are testing and its dependencies
    3) Expected values
        for example, if you are testing a function that modifies a value, create the
        expected value here. (auth tokens might be created in a function -> noticed
        that you might want to create expected here and modify before comparison
        basically, only modify expected in the test class if the tested function randomly
        generates something
     Extra) if you need to instantiate something, and it requires some calculation or something else
     can consider making a before all function to instantiate -> or in before each
     */

    // Supporting variables
    static MemoryUserDAO userDAO = new MemoryUserDAO();
    static MemoryGameDAO gameDAO = new MemoryGameDAO();
    static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    UserData user1 = new UserData("Fido", "BigChungas", "ThisIsNotAWebsite.com");
    String gameName = "myGame";
    AuthData authData = new AuthData("ThisIsATokenIGuess", "Fido");
    static ClearService clearService =
            new ClearService(userDAO, gameDAO, authDAO);
    @BeforeEach
    public void setUp() throws DataAccessException {
        /*
        For each test:
        1) Instantiate what you want to -> not that good in before each
        2) clear database
        3) Then add what you want in the database prior to the test
        i.e. if you have a map, can instantiate it then insert desired values
         */
        clearService.clear();
        userDAO.createUser(user1);
        gameDAO.createGame(gameName);
        authDAO.createAuth(authData);
        //clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @AfterAll
    public static void tearDown() throws DataAccessException {
        /*
        After all tests are run, clear the database
         */
        clearService.clear();
    }

    @Test
    @DisplayName("Should clear all DAOs when ClearService.clear() is called")
    public void testClearServicePass() throws DataAccessException {
        /*
        For each test ->
        1) get expect value of what variables you are testing -> much of this could be in variable section
            Part of this step can be verifying that the state of the thing you are testing is correct
        2) call function you are testing
        3) Assertions -> assert true, false, equal, not equal, etc.
        Extra) what does it mean to be empty? What is the value of its variables? Is it null?
        Are they null? What is the default value? What are you expecting?
         */
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertFalse(gameDAO.empty());
        Assertions.assertFalse(authDAO.empty());
        clearService.clear();
        Assertions.assertTrue(userDAO.empty());
        Assertions.assertTrue(gameDAO.empty());
        Assertions.assertTrue(authDAO.empty());
    }


}

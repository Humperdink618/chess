package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestExceptionChess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;

public class UserServiceTest {

    static MemoryUserDAO userDAO = new MemoryUserDAO();
    static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    UserData user1 = new UserData("Fido", "BigChungas", "ThisIsNotAWebsite.com");

    static UserService userService = new UserService(userDAO, authDAO);
    static ClearService clearService = new ClearService(userDAO, new MemoryGameDAO(), authDAO);

    @BeforeEach
    public void setUp() throws DataAccessException, BadRequestExceptionChess, AlreadyTakenException {
        clearService.clear();
        //userDAO.createUser(user1);
        userService.register(new RegisterRequest(user1.username(), user1.password(), user1.email()));
    }

    @AfterAll
    public static void tearDown() throws DataAccessException {
        /*
        After all tests are run, clear the database
         */
        clearService.clear();
    }

    @Test
    @DisplayName("Should add a new user to the DAO when register() is called")
    void registerPass() throws Exception {
        RegisterRequest goodReq = new RegisterRequest("John", "Moo", "email@mail.com");
        Assertions.assertFalse(userDAO.empty());
        userService.register(goodReq);
        Assertions.assertTrue(userDAO.getUser(goodReq.username()) != null);
        Assertions.assertFalse(authDAO.empty());
    }

    @Test
    @DisplayName("Should throw exception when register() is called")
    void registerFail() throws Exception {
        RegisterRequest badReq = new RegisterRequest("Fido", "Moo", "email@mail.com");
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertThrows(Exception.class, () -> {
            userService.register(badReq);
            throw new Exception("Error: already taken");
        });
    }

    @Test
    @DisplayName("Should log in an existing user and return a new authToken when login() is called")
    void loginPass() throws Exception {

        LoginRequest goodReq = new LoginRequest("Fido", "BigChungas");
        Assertions.assertFalse(userDAO.empty());
        userService.login(goodReq);
        Assertions.assertFalse(authDAO.empty());
    }

    @Test
    @DisplayName("Should throw an exception when login() is called")
    void loginFail() throws Exception {
        LoginRequest badReq = new LoginRequest("Fdo", "BigChungas");
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertThrows(Exception.class, () -> {
            userService.login(badReq);
            throw new Exception("Error: unauthorized");
        });
    }

    @Test
    @DisplayName("Should logout in an existing user and delete authToken when logout() is called")
    void logoutPass() throws Exception {
        AuthData authData = new AuthData("LOLIAmAuthToken", "Fido");
        authDAO.createAuth(authData);
        LogoutRequest goodReq = new LogoutRequest("LOLIAmAuthToken");
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertFalse(authDAO.empty());
        userService.logout(goodReq);
        Assertions.assertTrue(authDAO.getAuth("LOLIAmAuthToken") == null);
    }

    @Test
    @DisplayName("Should throw an error when logout() is called")
    void logoutFail() throws Exception {
        AuthData authData = new AuthData("LOLIAmAuthToken", "Fido");
        authDAO.createAuth(authData);
        LogoutRequest badReq = new LogoutRequest("Soup");
        Assertions.assertFalse(userDAO.empty());
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertThrows(Exception.class, () -> {
            userService.logout(badReq);
            throw new Exception("Error: unauthorized");
        });
    }
}
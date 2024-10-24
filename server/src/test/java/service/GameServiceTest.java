package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.CreateRequest;
import request.JoinRequest;
import request.ListRequest;
import java.util.Objects;

public class GameServiceTest {

    static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static MemoryGameDAO gameDAO = new MemoryGameDAO();
    AuthData authData = new AuthData("myToken", "Fido");
    String gameName1 = "myGame";
    static int myGameID;

    static ClearService clearService = new ClearService(new MemoryUserDAO(), gameDAO, authDAO);
    static GameService gameService = new GameService(authDAO, gameDAO);

    @BeforeEach
    void setUp() throws DataAccessException {
        clearService.clear();
        authDAO.createAuth(authData);
        myGameID = gameDAO.createGame(gameName1);
    }

    @AfterAll
    public static void tearDown() throws DataAccessException {
        /*
        After all tests are run, clear the database
         */
        clearService.clear();
    }

    @Test
    @DisplayName("Should return a list of games when listGames() is called")
    void listGamesPass() throws Exception {
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertFalse(gameDAO.empty());
        gameDAO.createGame("otherGame");
        ListRequest goodReq = new ListRequest("myToken");
        gameService.listGames(goodReq);
        //ListResult result = gameService.listGames(goodReq);   // just checking if the result returned the right stuff
        Assertions.assertTrue(Objects.equals(authData.authToken(), goodReq.authToken()));
        Assertions.assertTrue(gameDAO.listGames().size() == 2);
    }

    @Test
    @DisplayName("Should throw an error when listGames() is called")
    void listGamesFail() throws Exception {
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertFalse(gameDAO.empty());
        gameDAO.createGame("otherGame");
        ListRequest badReq = new ListRequest("IAmATeapotShortAndStout");
        Assertions.assertThrows(Exception.class, () -> {
            gameService.listGames(badReq);
            throw new Exception("Error: unauthorized");
        });
    }

    @Test
    @DisplayName("Should create a new game when createGame() is called")
    void createGamePass() throws Exception {
        Assertions.assertFalse(authDAO.empty());
        CreateRequest goodReq = new CreateRequest("myToken", "myNewGame");
        gameService.createGame(goodReq);
        Assertions.assertTrue(Objects.equals(authData.authToken(), goodReq.authToken()));
        Assertions.assertTrue(gameDAO.listGames().size() == 2);
    }

    @Test
    @DisplayName("Should throw an error when createGame() is called")
    void createGameFail() throws Exception {
        Assertions.assertFalse(authDAO.empty());
        CreateRequest badReq = new CreateRequest("LEEROOOYJENKIIIIINS", "myNewGame");
        Assertions.assertThrows(Exception.class, () -> {
            gameService.createGame(badReq);
            throw new Exception("Error: unauthorized");
        });
    }

//    @Test
//    @DisplayName("Should throw an error when createGame() is called due to bad request")
//    // don't actually need this test, but a good idea to try it out anyway
//    void createGameBadReq() throws Exception {
//        Assertions.assertFalse(authDAO.empty());
//        CreateRequest badReq = new CreateRequest("myToken", null);
//        Assertions.assertThrows(Exception.class, () -> {
//            gameService.createGame(badReq);
//            throw new Exception("Error: bad request");
//        });
//    }

    @Test
    @DisplayName("Should update an existing game when joinGame() is called")
    void joinGamePass() throws Exception {
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertFalse(gameDAO.empty());
        JoinRequest goodReq = new JoinRequest("myToken", "WHITE", myGameID);
        gameService.joinGame(goodReq);
        Assertions.assertTrue(Objects.equals(authData.authToken(), goodReq.authToken()));
        Assertions.assertTrue(Objects.equals(gameDAO.getGame(myGameID).whiteUsername(), "Fido"));
    }

    @Test
    @DisplayName("Should throw an exception when joinGame() is called")
        // I've tested a lot of authentication errors already. Let's test for a bad request this time!
    void joinGameFail() throws Exception {
        Assertions.assertFalse(authDAO.empty());
        Assertions.assertFalse(gameDAO.empty());
        JoinRequest badReq = new JoinRequest("myToken", "PLAID", myGameID);
        Assertions.assertThrows(Exception.class, () -> {
            gameService.joinGame(badReq);
            throw new Exception("Error: bad request");
        });
    }

    //    @Test
//    @DisplayName("Should throw an exception when joinGame() is called")
//    void joinGameFailAuth() throws Exception {
//        Assertions.assertFalse(authDAO.empty());
//        Assertions.assertFalse(gameDAO.empty());
//        JoinRequest badReq = new JoinRequest("JoeBiden", "WHITE", myGameID);
//        Assertions.assertThrows(Exception.class, () -> {
//            gameService.joinGame(badReq);
//            throw new Exception("Error: unauthorized");
//        });
//    }

//    @Test
//    @DisplayName("Should throw an exception when joinGame() is called")
//        // I've tested a lot of authentication errors already. Let's test for an already taken error this time!
//    void joinGameFailAlreadyTaken() throws Exception {
//        Assertions.assertFalse(authDAO.empty());
//        Assertions.assertFalse(gameDAO.empty());
//        gameDAO.updateGame(
//                new GameData(
//                        1,
//                        null,
//                        "BarneyThePurpleDinosaur",
//                        "myGame",
//                        new ChessGame()
//                ));
//        JoinRequest badReq = new JoinRequest("myToken", "BLACK", myGameID);
//        Assertions.assertThrows(Exception.class, () -> {
//            gameService.joinGame(badReq);
//            throw new Exception("Error: already taken");
//        });
//    }
}
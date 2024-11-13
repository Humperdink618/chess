package client;

import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import result.ListResult;
import server.Server;
import ui.serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        StringBuilder portString = new StringBuilder();
        portString.append("http://localhost:" + port);
        serverFacade = new ServerFacade(portString.toString());
        System.out.println(portString);
    }

    @BeforeEach
    public void setUp() throws ResponseException {
        serverFacade.clear();
        serverFacade.register("Uzi", "BiteMe", "DarkXWolf17@JCJenson.com");
    }

    @AfterEach
    public void tearDown() throws ResponseException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @DisplayName("Should create a new user when register() is called")
    public void registerPass() throws ResponseException {
        String goodUsername = "N";
        String goodPW = "HappyDoingAnything";
        String goodEmail = "goodBoi@JCJenson.com";
        String authData = serverFacade.register(goodUsername, goodPW, goodEmail);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.register(goodUsername, goodPW, goodEmail));
    }

    @Test
    @DisplayName("Should throw an error when register() is called")
    public void registerFail() throws ResponseException {
        String badUsername = "Uzi";
        String badPW = "BiteMe";
        String badEmail = "DarkXWolf17@JCJenson.com";
        // user already exists.
        Assertions.assertThrows(ResponseException.class, () -> {
            String authData = serverFacade.register(badUsername, badPW, badEmail);
            HashMap errorMessageMap = new Gson().fromJson(authData, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            throw new ResponseException(errorMessage);
        });
    }

    @Test
    @DisplayName("Should login an existing user when login() is called")
    public void loginPass() throws ResponseException {
        String goodUsername = "Uzi";
        String goodPW = "BiteMe";
        String authData = serverFacade.login(goodUsername, goodPW);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.login(goodUsername, goodPW));
    }

    @Test
    @DisplayName("Should throw an error when login() is called")
    public void loginFail() throws ResponseException {
        String badUsername = "Cyn";
        String badPW = "getSNUCKUponXD";
        Assertions.assertThrows(ResponseException.class, () -> {
            String authData = serverFacade.login(badUsername, badPW);
            HashMap errorMessageMap = new Gson().fromJson(authData, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            throw new ResponseException(errorMessage);
        });
    }

    @Test
    @DisplayName("Should logout an existing user when logout() is called")
    public void logoutPass() throws ResponseException {
        String goodUsername = "V";
        String goodPW = "AndYetIStillFeelNothing";
        String goodEmail = "GrumpyMcTraumaBot823@JCJenson.com";
        String authData = serverFacade.register(goodUsername, goodPW, goodEmail);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.register(goodUsername, goodPW, goodEmail));
        String response = serverFacade.logout(authData);
        Assertions.assertTrue(response.equals("logout successful!"));
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(authData));
    }

    @Test
    @DisplayName("Should throw an error when logout() is called")
    public void logoutFail() throws ResponseException {
        String badAuth = "Absolute_Solver";
        Assertions.assertThrows(ResponseException.class, () -> {
            String response = serverFacade.logout(badAuth);
            HashMap errorMessageMap = new Gson().fromJson(response, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            throw new ResponseException(errorMessage);
        });
    }

    @Test
    @DisplayName("Should create a new game when create() is called")
    public void createPass() throws ResponseException {
        String goodUsername = "Khan Doorman";
        String goodPW = "CertificateOfBestDoor";
        String goodEmail = "Doors4Lyfe@JCJenson.com";
        String authData = serverFacade.register(goodUsername, goodPW, goodEmail);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.register(goodUsername, goodPW, goodEmail));
        String goodGameName = "Doors <3";
        String gameID = serverFacade.create(goodGameName, authData);
        Assertions.assertTrue(serverFacade.isNumeric(gameID));
        Assertions.assertDoesNotThrow(() -> serverFacade.create(goodGameName, authData));
    }

    @Test
    @DisplayName("Should throw an error when create() is called")
    public void createFail() throws ResponseException {
        String badAuth = "Fatal Error";
        String badGameName = "SENTINAL OOOD";
        Assertions.assertThrows(ResponseException.class, () -> {
            String gameID = serverFacade.create(badGameName, badAuth);
            Assertions.assertFalse(serverFacade.isNumeric(gameID));
            HashMap errorMessageMap = new Gson().fromJson(gameID, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            throw new ResponseException(errorMessage);
        });
    }

    @Test
    @DisplayName("Should return a list of gameData when list() is called")
    public void listPass() throws ResponseException {
        String goodUsername = "Doll";
        String goodPW = "ЯНеРешил";
        String goodEmail = "BabeATronQueenthousand@JCJenson.com";
        String authData = serverFacade.register(goodUsername, goodPW, goodEmail);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.register(goodUsername, goodPW, goodEmail));
        serverFacade.create("Ты Такая Мелочхый", authData);
        serverFacade.create("Дай Отпор", authData);
        String response = serverFacade.list(authData);
        ListResult listResult = new Gson().fromJson(response, ListResult.class);
        Collection<GameData> gameList = listResult.games();
        ArrayList<String> games = new ArrayList<>();
        for(GameData game : gameList) {
            StringBuilder individualGameData = new StringBuilder();
            individualGameData.append(" " + game.whiteUsername() + ", ");
            individualGameData.append(game.blackUsername() + ", " + game.gameName());
            games.add(individualGameData.toString());
        }
        Assertions.assertTrue(games.size() == listResult.games().size());
        Assertions.assertTrue(games.size() == 2);
        Assertions.assertDoesNotThrow(() -> serverFacade.list(authData));
    }

    @Test
    @DisplayName("Should throw an error when list() is called")
    public void listFail() throws ResponseException {
        String badAuth = "Кукла";
        Assertions.assertThrows(ResponseException.class, () -> {
            String response = serverFacade.list(badAuth);
            Assertions.assertTrue(response.contains("message"));
            HashMap errorMessageMap = new Gson().fromJson(response, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            throw new ResponseException(errorMessage);
        });
    }

    @Test
    @DisplayName("Should join an existing user to an existing game when join() is called")
    public void joinPass() throws ResponseException {
        String goodUsername = "J";
        String goodPW = "BrandedPens<3";
        String goodEmail = "MotherOfCompanyLeadership@JCJenson.com";
        Collection<Integer> gameIDs = new HashSet<>();
        String authData = serverFacade.register(goodUsername, goodPW, goodEmail);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.register(goodUsername, goodPW, goodEmail));
        String goodGameName1 = "NoEscapeEvenInDeath";
        String gameIDString1 = serverFacade.create(goodGameName1, authData);
        int gameID1 = Integer.parseInt(gameIDString1);
        gameIDs.add(gameID1);
        String goodGameName2 = "CorporateHasSpoken";
        String gameIDString2 = serverFacade.create(goodGameName2, authData);
        int gameID2 = Integer.parseInt(gameIDString2);
        gameIDs.add(gameID2);
        String goodPlayerColor = "BLACK";
        String response = serverFacade.join(authData, gameID1, goodPlayerColor);
        Assertions.assertDoesNotThrow(() -> serverFacade.join(authData, gameID1, goodPlayerColor));
        Assertions.assertTrue(response.equals("join successful!"));
    }

    @Test
    @DisplayName("Should throw an error when join() is called")
    public void joinFail() throws ResponseException {
        String badUsername = "EldritchJ";
        String badPW = "SNARL";
        String badEmail = "SOLVER_OF_THE_ABSOLUTE_FABRIC@JCJenson.com";
        //String badAuth = "sys_cyn_callbackping_uzi";  // tests UnauthorizedException
        Collection<Integer> gameIDs = new HashSet<>();
        String authData = serverFacade.register(badUsername, badPW, badEmail);
        Assertions.assertTrue(authData.length() > 10);
        Assertions.assertDoesNotThrow(() -> serverFacade.register(badUsername, badPW, badEmail));
        String badGameName1 = "EasierToAssimilateThanExplain";
        String gameIDString1 = serverFacade.create(badGameName1, authData);
        int gameID1 = Integer.parseInt(gameIDString1);
        gameIDs.add(gameID1);
        String badGameName2 = "Time To Do Something SHOCKING";
        String gameIDString2 = serverFacade.create(badGameName2, authData);
        int gameID2 = Integer.parseInt(gameIDString2);
        gameIDs.add(gameID2);
        //String badPlayerColor = "YELLOW";  // tests BadRequestException
        String badPlayerColor = "BLACK";
        serverFacade.join(authData, gameID2, badPlayerColor); // tests AlreadyTakenException
        Assertions.assertThrows(ResponseException.class, () -> {
            String response = serverFacade.join(authData, gameID2, badPlayerColor);
            //String response = serverFacade.join(badAuth, gameID2, badPlayerColor);
            Assertions.assertTrue(response.contains("message"));
            HashMap errorMessageMap = new Gson().fromJson(response, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            throw new ResponseException(errorMessage);
        });
    }
    // note: no unit tests for getErrorMessage(Exception e), as it doesn't call my Server and is an internal method
    // nor is there any unit test for clear(), as that method ONLY exists for testing purposes.
}

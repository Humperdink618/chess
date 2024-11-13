package ui.serverfacade;

import com.google.gson.Gson;
import exceptions.ResponseException;
import request.*;
import result.CreateResult;
import result.ListResult;
import result.LoginResult;
import result.RegisterResult;

import java.util.Map;

public class ServerFacade {

    private final String serverURL;

    //private static boolean isCreate = false;

    public ServerFacade(String url) {
        serverURL = url;
    }

    // TODO: implement ServerFacade class

    public String login(String username, String password) throws ResponseException {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return auth token (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        try {
            String path = "/session";
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult authData = ClientCommunicator.makeRequest(
                    "POST",
                    path,
                    loginRequest,
                    LoginResult.class,
                    serverURL,
                    null);
            return authData.authToken();

        } catch (Exception e) {
            return getErrorMessage(e);
        }
        // String authToken = authData.authToken;
        // TODO: for testing purposes only: delete when this method is actually implemented
        //return "authToken";
        //return "";
    }

    private static String getErrorMessage(Exception e) {
        String body = new Gson().toJson(Map.of("message", e.getMessage()));
        return body;
    }

    public String register(String username, String password, String email) throws ResponseException{
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return auth token (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        // TODO: for testing purposes only: delete when this method is actually implemented
        //return "authToken";
        try {
            String path = "/user";
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult authData
                    = ClientCommunicator.makeRequest(
                    "POST",
                    path,
                    registerRequest,
                    RegisterResult.class,
                    serverURL,
                    null);
            return authData.authToken();
        } catch (Exception e) {
            return getErrorMessage(e);
        }
    }

    public String logout(String authToken) throws ResponseException {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return string "logout successful!"
        // (might consider returning full http response if code in ui needs more info)
        // if bad -> return "logout failed" for ease of knowing what's wrong
        // want to pass result back to ui
        // TODO: for testing purposes only: delete when this method is actually implemented
        try {
            //String path = String.format("/session/%s", authToken);
            String path = "/session";
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            ClientCommunicator.makeRequest(
                    "DELETE",
                    path,
                    logoutRequest,
                    null,
                    serverURL,
                    authToken);
            return "logout successful!";

        } catch (Exception e) {
            return getErrorMessage(e);
        }
    }

    public String create(String gameName, String authToken) throws ResponseException{
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return gameID (might consider returning full http response if code in ui needs more info)
        // if bad -> return 0 for ease of knowing what's wrong
        // want to pass result back to ui
        //isCreate = true;
        // write request to serverFacade
        //isCreate = false; // for cleanup purposes
        // TODO: for testing purposes only: delete when this method is actually implemented
        try {
            String path = "/game";
            CreateRequest createRequest = new CreateRequest(authToken, gameName);
            CreateResult createResult = ClientCommunicator.makeRequest(
                    "POST",
                    path,
                    createRequest,
                    CreateResult.class,
                    serverURL,
                    authToken);
            int gameID = createResult.gameID();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(gameID);
            //return 1;
            return stringBuilder.toString();
        } catch (Exception e) {
            return getErrorMessage(e);
        }
    }

    public String list(String authToken) throws ResponseException {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return list of games
        // (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui

        try {
            String path = "/game";
            ListRequest listRequest = new ListRequest(authToken);
            ListResult listGames = ClientCommunicator.makeRequest(
                    "GET",
                    path,
                    null,
                    ListResult.class,
                    serverURL,
                    authToken
            );

            return new Gson().toJson(listGames);

        } catch (Exception e) {

            return getErrorMessage(e);
        }
        // note: do NOT have this list of games display the internal game IDs.
        // also, if you have something in JSON, parse it, and only display the information we want the user to see.
        // TODO: for testing purposes only: delete when this method is actually implemented
       /* ArrayList<String> games = new ArrayList<>();
        StringBuilder individualGameData = new StringBuilder();
        GameData game1 = new GameData(1, null, null, "myGame", null);
        individualGameData.append(" " + game1.whiteUsername() + ", ");
        individualGameData.append(game1.blackUsername() + ", " + game1.gameName());
        games.add(individualGameData.toString());

        return games;
        */
        //return null;
    }

    public String join(String authToken, Integer gameID, String playerColor) throws ResponseException{
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return string "join successful"
        // (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        //Integer.parseInt(gameID); // convert the string gameID to an int
        // TODO: for testing purposes only: delete when this method is actually implemented
        try {
            String path = "/game";
            JoinRequest joinRequest = new JoinRequest(null, playerColor, gameID);
            ClientCommunicator.makeRequest(
                    "PUT",
                    path,
                    joinRequest,
                    null,
                    serverURL,
                    authToken);
            return "join successful!";
        } catch(Exception e) {
            return getErrorMessage(e);
        }
    }

    // note: for testing purposes only:
    public void clear() throws ResponseException {
        String path = "/db";
        ClientCommunicator.makeRequest("DELETE", path, null, null, serverURL, null);
    }













}

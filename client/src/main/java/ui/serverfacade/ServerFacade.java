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

    public ServerFacade(String url) {
        serverURL = url;
    }

    public String login(String username, String password) throws ResponseException {
        /* note: may need to change the return type at some point

         send http request to server

         */

        /*
         check result


         if good -> ? return auth token (might consider returning full http response if code in ui needs more info)

         */
        /*
         if bad -> return "" for ease of knowing what's wrong
         want to pass result back to ui

         */
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
    }

    private static String getErrorMessage(Exception e) {
        String body = new Gson().toJson(Map.of("message", e.getMessage()));
        return body;
    }

    public String register(String username, String password, String email) throws ResponseException{

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
    }

    public String join(String authToken, Integer gameID, String playerColor) throws ResponseException{

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
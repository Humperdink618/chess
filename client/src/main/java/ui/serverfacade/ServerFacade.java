package ui.serverfacade;

import model.GameData;

import java.util.Collection;

public class ServerFacade {

    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    // TODO: implement ServerFacade class

    public static String login(String username, String password) {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return auth token (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        return "";
    }

    public static String register(String username, String password, String email){
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return auth token (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        return "";
    }

    public static String logout(String authToken){
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return string "logout successful!"
        // (might consider returning full http response if code in ui needs more info)
        // if bad -> return "logout failed" for ease of knowing what's wrong
        // want to pass result back to ui
        return "";
    }

    public static int create(String gameName, String authToken) {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return gameID (might consider returning full http response if code in ui needs more info)
        // if bad -> return 0 for ease of knowing what's wrong
        // want to pass result back to ui
        return 0;
    }

    public static GameData[] list(String authToken) {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return list of games
        // (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        //var path = "/game";
        //record listGameResponse(GameData[] games){}
        // note: do NOT have this list of games display the internal game IDs.
        // also, if you have something in JSON, parse it, and only display the information we want the user to see.
        return null;
    }

    public static String join(String authToken, Integer gameID, String playerColor) {
        // note: may need to change the return type at some point
        // send http request to server
        // check result
        // if good -> ? return string "join successful"
        // (might consider returning full http response if code in ui needs more info)
        // if bad -> return "" for ease of knowing what's wrong
        // want to pass result back to ui
        //Integer.parseInt(gameID); // convert the string gameID to an int

        return "";
    }












}

package ui.serverfacade;

public class ServerFacade {
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
}

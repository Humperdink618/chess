package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Game;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.HashMap;

// Note: this is based on PetShop Code. May require additional tweaks to make it compatible
// with my code.
public class ConnectionManager {
    public final HashMap<String, Connection> userSessions = new HashMap<String, Connection>();

    public void add(String username, Session session) {
        Connection connection = new Connection(username, session);
        userSessions.put(username, connection);
    }

    public void remove(String username){
        userSessions.remove(username);
    }

    public void broadcastToAllButRootClient(String excludeUsername, ServerMessage serverMessage) throws Exception {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for(Connection c : userSessions.values()){
            if(c.session.isOpen()){
                if(!c.username.equals(excludeUsername)) {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open
        for(Connection c : removeList){
            userSessions.remove(c.username);
        }
    }

    public void broadcastToRootClient(String username, ServerMessage serverMessage) throws Exception {
        ArrayList<Connection> removeList = new ArrayList<>();
        for(Connection c : userSessions.values()){
            if(c.session.isOpen()){
                if(c.username.equals(username)) {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open
        for(Connection c : removeList){
            userSessions.remove(c.username);
        }
    }

    public void broadcastToAll(ServerMessage serverMessage) throws Exception {
        ArrayList<Connection> removeList = new ArrayList<>();
        for(Connection c : userSessions.values()){
            if(c.session.isOpen()){
                c.send(new Gson().toJson(serverMessage));
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open
        for(Connection c : removeList){
            userSessions.remove(c.username);
        }
    }
}

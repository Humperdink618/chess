package server;


import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String username;
    public Session session;

    public Connection(String username, Session session){
        this.session = session;
        this.username = username;
    }

    public void send(String msg) throws Exception {
        session.getRemote().sendString(msg);
    }
}

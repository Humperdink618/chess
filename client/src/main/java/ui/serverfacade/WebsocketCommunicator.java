package ui.serverfacade;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.DrawChessboard;
import ui.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import javax.websocket.ContainerProvider;
import javax.websocket.*;
import javax.websocket.MessageHandler;
import javax.websocket.WebSocketContainer;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    public ServerMessageObserver observer;
    public Session session;

    public WebsocketCommunicator(ServerMessageObserver observer) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.observer = observer;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            // may edit this further as I continue to test my server and websocket stuff.
            public void onMessage(String message) {
                try {
//                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    // observer.notify(message);
//                } catch(Exception e) {
//                    //  observer.notify(new ErrorMessage(e.getMessage()));
//                }
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                switch (serverMessage.getServerMessageType()) {
//                    case NOTIFICATION -> displayNotification((NotificationMessage) serverMessage);
//                    // create a subclass
//                    case ERROR -> displayError(((ErrorMessage) serverMessage));
//                    case LOAD_GAME -> loadGame(((LoadGameMessage) serverMessage).getGame());
//                }
                     observer.notify(serverMessage);
                } catch(Exception e) {
                     //observer.notify(new ErrorMessage(e.getMessage()));
                }
            }
        });
    }
//
//    private void loadGame(ChessGame game) {
//        DrawChessboard drawChessboard = new DrawChessboard(game, 0);
//        drawChessboard.run();
//    }
//
//    private void displayNotification(NotificationMessage serverMessage) {
//        System.out.println(serverMessage.getMessage());
//    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

//    public void displayError(ErrorMessage errorMessage){
//        System.out.println(errorMessage.getErrorMessage());
//    }
}

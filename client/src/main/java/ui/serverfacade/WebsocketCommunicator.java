package ui.serverfacade;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.ResponseException;
import ui.DrawChessboard;
import ui.ServerMessageObserver;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import javax.websocket.ContainerProvider;
import javax.websocket.*;
import javax.websocket.MessageHandler;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
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
                     observer.notify(new ErrorMessage(e.getMessage()));
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

    public void enterGamePlayMode(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void leaveGamePlayMode(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void resignFromGame(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void clientMakeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            //UserGameCommand command = new MakeMoveCommand(authToken, gameID, move);
             MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);

            //UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

//    public void displayError(ErrorMessage errorMessage){
//        System.out.println(errorMessage.getErrorMessage());
//    }
}

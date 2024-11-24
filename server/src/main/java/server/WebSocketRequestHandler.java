package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.Game;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.HashMap;

@WebSocket
public class WebSocketRequestHandler {

    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public WebSocketRequestHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private HashMap<Integer, ConnectionManager> sessionCollection = new HashMap<>();
    // private HashMap<Integer, HashSet<Sessions> sessionCollection = new HashMap<Integer, HashSet<Sessions>();
    // key is gameID, mapped to a set of Sessions
    // every time a player joins or observes, websocket automatically creates a session. Just have to add that session
    // to a collection, and pass that into a map.
    private boolean hasResigned = false;


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception, UnauthorizedException {
        // need some structure on the server side to keep track of players (i.e. games to players.)
        // can grab all those sessions and send messages to those players and observers.
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            // Throws a custom UnauthorizedException. Yours may work differently.
            String authToken = command.getAuthToken();
            if(authDAO.getAuth(authToken) == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            GameData gameData = gameDAO.getGame(command.getGameID());
            Integer gameID = gameData.gameID();
            if(gameID == null){
                throw new UnauthorizedException("Error: game doesn't exist");
            }
            // String username = getUsername(command.getAuthToken());
            // TODO: create a method or a class that keeps track of all the sessions (probably a collection would be
            //  easiest).

            //saveSession(command.getGameID(), session);
            switch (command.getCommandType()) {
                // TODO: create a method that implements all of these commands for each commandType
                // case CONNECT -> connect(session, username, (ConnectCommand) command) ;
                case CONNECT -> connect(session, username, command) ;
                // -- add user to the collection of sessions and send a message to everyone else that that player
                //     has joined the game. Use send() method.
                case MAKE_MOVE -> makeMove(session, username, ((MakeMoveCommand) command));
                // case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case LEAVE -> leaveGame(session, username, command);
                // case RESIGN -> resign(session, username, (ResignCommand) command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (UnauthorizedException e) {
            // Serializes and sends the error message
             sendMessage(session, new Gson().toJson(new ErrorMessage("Error: unauthorized")));
        } catch (Exception e){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    public void connect(Session session, String username, UserGameCommand command) throws Exception{

        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            ConnectionManager newUserSessions = new ConnectionManager();
            userSessions = newUserSessions;
            saveSession(command.getGameID(), userSessions);
        }
        userSessions.add(username, session);
        GameData gameData = gameDAO.getGame(command.getGameID());
        String message = "";
        String playerColor = "";
        if(gameData.blackUsername().equals(username)){
            message = String.format("%s has joined the game, playing as Black", username);
            playerColor = "BLACK";
        } else if(gameData.whiteUsername().equals(username)){
            message = String.format("%s has joined the game, playing as White", username);
            playerColor = "WHITE";
        } else {
            message = String.format("%s has joined the game as an observer", username);
            playerColor = "WHITE";
        }
        Game game = new Game(gameData.game(), playerColor);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        userSessions.broadcastToRootClient(username, loadGameMessage);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        userSessions.broadcastToAllButRootClient(username, notificationMessage);
    }

    public void saveSession(int gameID, ConnectionManager userSessions){
        sessionCollection.put(gameID, userSessions);
    }

    public void resign(Session session, String username, UserGameCommand command) throws Exception{
        // sets the game to game over
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't resign if you were never part" +
                    "of the game")));
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        if(oldGD.whiteUsername() == null || oldGD.blackUsername() == null) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot resign if you're the" +
                    " only one playing (or if you are simply observing).")));
        } else if (!oldGD.blackUsername().equals(username) && !oldGD.whiteUsername().equals(username)) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot resign if you are " +
                            "observing.")));
        } else if(oldGD.game().isGameOver()){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: The game is already over. No need to " +
                    "resign.")));
        } else {

            ChessGame chessGame = oldGD.game();
            chessGame.setGameOver(true);
            gameDAO.updateGame(new GameData(
                    oldGD.gameID(),
                    oldGD.whiteUsername(),
                    oldGD.blackUsername(),
                    oldGD.gameName(),
                    chessGame)
            );
            String message = String.format("%s has resigned.", username);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            userSessions.broadcastToAll(notificationMessage);
            hasResigned = true;
        }
    }

    public void makeMove(Session session, String username, MakeMoveCommand command) throws Exception{
        // sets the game to game over
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't make a move if you're " +
                    "not in the game!")));
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        if(oldGD.whiteUsername() == null || oldGD.blackUsername() == null) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot make a move if you're not " +
                    "playing against anyone.")));
        } else if (!oldGD.blackUsername().equals(username) && !oldGD.whiteUsername().equals(username)) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot make a move if you are " +
                    "observing.")));
        } else if(oldGD.game().isGameOver()){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: The game is already over. No more moves" +
                    " can be made.")));
        } else {

            ChessGame chessGame = oldGD.game();
            ChessMove move = command.getMove();
            ChessBoard board = chessGame.getBoard();
            ChessPiece chessPiece = board.getPiece(move.getStartPosition());
            String playerColor = "";
            String endGameMessage = "";
            String kingInCheckMessage = "";
            String moveMessage = "";
            ChessGame.TeamColor teamColor = chessPiece.getTeamColor();
            ChessGame.TeamColor enemyTeamColor = null;
            String opponentUserName = "";
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            int startRow = startPos.getRow();
            int startCol = startPos.getColumn();
            int endRow = endPos.getRow();
            int endCol = endPos.getColumn();
            String startRowLetter = "";
            String endRowLetter = "";
            for(int i = 1; i < 9; i++){
                if(i == startRow){
                    startRowLetter = getCharForNumber(i);
                }
                if(i == endRow){
                    endRowLetter = getCharForNumber(i);
                }
            }
            if(startRowLetter.isBlank() || endRowLetter.isBlank()){
                sendMessage(session, new Gson().toJson(new ErrorMessage("Error: invalid move.")));
            }

            if(oldGD.whiteUsername().equals(username)){
                if(teamColor != ChessGame.TeamColor.WHITE){
                    sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't move an opponent's " +
                            "piece.")));
                } else {
                    playerColor = "WHITE";
                    enemyTeamColor = ChessGame.TeamColor.BLACK;
                    opponentUserName = oldGD.blackUsername();
                }
            } else if(oldGD.blackUsername().equals(username)){
                if(teamColor != ChessGame.TeamColor.BLACK){
                    sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't move an opponent's " +
                            "piece.")));
                } else {
                    playerColor = "BLACK";
                    enemyTeamColor = ChessGame.TeamColor.WHITE;
                    opponentUserName = oldGD.whiteUsername();
                }
            }

            try {
                chessGame.makeMove(move);

                moveMessage =
                        String.format(
                                "%s moved a piece from %s%s to %s%s",
                                username,
                                startRowLetter,
                                startCol,
                                endRowLetter,
                                endCol);

                if(move.getPromotionPiece() != null){
                    moveMessage =
                            String.format(
                                    "%s moved a PAWN from %s%s to %s%s and promoted it to a %s",
                                    username,
                                    startRowLetter,
                                    startCol,
                                    endRowLetter,
                                    endCol,
                                    move.getPromotionPiece()
                            );
                }

                if(chessGame.isInCheck(enemyTeamColor)){
                    kingInCheckMessage = String.format("%s has put %s's king in Check", username, opponentUserName);
                }
                if(chessGame.isInCheckmate(enemyTeamColor)) {
                    endGameMessage = String.format("GAME OVER: %s has put %s's king in Checkmate. " +
                            "%s Team WINS! Thank you for playing!", username, opponentUserName, teamColor);
                    chessGame.setGameOver(true);
                } else if(chessGame.isInStalemate(enemyTeamColor)){
                    endGameMessage = "GAME OVER: game ends in Stalemate. No one wins. Better luck next time!";
                    chessGame.setGameOver(true);
                }


            } catch (InvalidMoveException e) {
                sendMessage(session, new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
            }

            gameDAO.updateGame(new GameData(
                    oldGD.gameID(),
                    oldGD.whiteUsername(),
                    oldGD.blackUsername(),
                    oldGD.gameName(),
                    chessGame)
            );
            Game game = new Game(chessGame, playerColor);
            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            userSessions.broadcastToAll(loadGameMessage);
            NotificationMessage notifyThatPlayerHasMadeMove = new NotificationMessage(moveMessage);
            userSessions.broadcastToAllButRootClient(username, notifyThatPlayerHasMadeMove);
            if(chessGame.isInCheck(enemyTeamColor)){
                NotificationMessage notifyIsInCheck = new NotificationMessage(kingInCheckMessage);
                userSessions.broadcastToAll(notifyIsInCheck);
            }
            if(chessGame.isInCheckmate(enemyTeamColor) || chessGame.isInStalemate(enemyTeamColor)){
                NotificationMessage notifyGameOver = new NotificationMessage(endGameMessage);
                userSessions.broadcastToAll(notifyGameOver);
            }
        }
    }

    private String getCharForNumber(int i){
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'a' - 1)) : null;
    }

    public void leaveGame(Session session, String username, UserGameCommand command) throws Exception{
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't leave if you weren't here to " +
                    "begin with")));
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        if(oldGD.blackUsername() != null){
            if(oldGD.blackUsername().equals(username)) {
                GameData newGD
                        = new GameData(
                        oldGD.gameID(),
                        oldGD.whiteUsername(),
                        null,
                        oldGD.gameName(),
                        oldGD.game()
                );
                gameDAO.updateGame(newGD);
            }
        }
        if(oldGD.whiteUsername() != null) {
            if (oldGD.whiteUsername().equals(username)) {
                GameData newGD =
                        new GameData(
                                oldGD.gameID(),
                                null,
                                oldGD.blackUsername(),
                                oldGD.gameName(),
                                oldGD.game()
                        );
                gameDAO.updateGame(newGD);
            }
        }
        userSessions.remove(username);
        String message = String.format("%s has left the game", username);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        userSessions.broadcastToAllButRootClient(username, notificationMessage);
    }

    public void sendMessage(Session session, String msg) throws Exception {
        session.getRemote().sendString(msg);
    }
}

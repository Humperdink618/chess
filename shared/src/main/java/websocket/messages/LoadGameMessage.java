package websocket.messages;

import chess.ChessGame;


public class LoadGameMessage extends ServerMessage {

    private Game game;

    public LoadGameMessage(Game game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}

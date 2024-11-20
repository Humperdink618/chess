package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private ChessGame game;

    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}

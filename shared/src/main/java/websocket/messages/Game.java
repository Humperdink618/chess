package websocket.messages;

import chess.ChessGame;

public class Game {

    public ChessGame game;
    public String playerColor;

    public Game(ChessGame game, String playerColor){
        this.game = game;
        this.playerColor = playerColor;
    }
}

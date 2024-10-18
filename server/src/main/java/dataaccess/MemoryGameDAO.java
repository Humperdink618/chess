package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public int createGame(String gameName){
        GameData game = new GameData(nextId++, null, null, gameName, new ChessGame());
        // whiteUsername and blackUsername will be updated when joinGame is called

        games.put(game.gameID(), game);
        return game.gameID();
    }

    public Collection<GameData> listGames(){
        return games.values();
    }

    public void updateGame(GameData gameData){
        for(int i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameData.gameID()){
                games.replace(i, gameData);
                break;
            }
        }
    }
    // I think this is right? May need to check with TAs.

    public GameData getGame(int gameID){
        return games.get(gameID);
    }
/*
    public void deleteGame(int gameID){
        games.remove(gameID);
    }
    // not sure if I need this. I'll write it down anyway, just in case.

 */
    public void clearGameData(){
        games.clear();
    }
}

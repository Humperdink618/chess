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
    // this will work, but it has poor performance (very slow). May need to seek alternate methods, but for this class
    // this will work. Should still try to find a Big O of O(1) instead of the current O(n)

    public GameData getGame(int gameID){
        return games.get(gameID);
    }


    public void clear(){
        games.clear();
    }
}

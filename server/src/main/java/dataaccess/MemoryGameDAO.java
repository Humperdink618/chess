package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

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
        Collection<GameData> myGames = new HashSet<>();
        for( GameData game : games.values()){
            myGames.add(
                    new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null)
            );
        }

        return myGames;
        //return games.values();
    }
    // specs show that the listGames response doesn't actually contain "ChessGame" as part of the list.
    // may need to ask TAs about this...

    public void updateGame(GameData gameData){
        games.put(gameData.gameID(), gameData);
        // TODO eventually update in database
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

    // for testing purposes only
    public boolean empty() {
        return games.isEmpty();
    }
}

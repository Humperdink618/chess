package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    // void deleteGame(int gameID) throws DataAccessException;
    //  just in case. Not sure if I will need the above method, but good to think about

    void clear() throws DataAccessException;
}

package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void clear() throws DataAccessException;

    // For testing purposes
    boolean empty();
}

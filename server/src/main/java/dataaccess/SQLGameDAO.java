package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public int createGame(String gameName) throws DataAccessException {
        // TODO: not implemented
        return 0; // change this when implementing this method
    }

    public Collection<GameData> listGames() throws DataAccessException {
        // TODO: not implemented
        return null;
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        // TODO: not implemented
    }

    public GameData getGame(int gameID) throws DataAccessException {
        // TODO: not implemented
        return null;
    }

    public void clear() throws DataAccessException {
        // TODO: not implemented
    }

    // for testing purposes only
    public boolean empty() {
        return false; // TODO: fix this later
    }

    private final String[] createStatementsGame = {
            """
            CREATE TABLE IF NOT EXISTS gamedata (
              `gameID` int(100) NOT NULL,
              `whiteUsername` varchar(100),
              `blackUsername` varchar(100),
              `gameName varchar(100) NOT NULL,
              `game` varchar(100) NOT NULL,
              PRIMARY KEY (`gameID`),
              FOREIGN KEY (`whiteUsername`) REFERENCES user(username),
              FOREIGN KEY (`blackUsername`) REFERENCES user(username)
            )
            """
    }; // possibly may make gameID AUTO INCREMENT. Will need to ask TAs at some point
    // also ask TAs if this is formatted correctly

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsGame) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

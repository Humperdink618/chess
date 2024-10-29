package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        // TODO: not implemented
        return null;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        // TODO: not implemented
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        // TODO: not implemented
    }

    public void clear() throws DataAccessException {
        // TODO: not implemented
    }

    // for testing purposes only
    public boolean empty() {
        // TODO: not implemented
        return false; // fix this later
    }

    private final String[] createStatementsAuth = {
            """
            CREATE TABLE IF NOT EXISTS authdata (
              `authToken` varchar(100) NOT NULL,
              `username` varchar(100), NOT NULL,
              PRIMARY KEY (`authToken`),
              FOREIGN KEY (`username`) REFERENCES user(username)
            )
            """
    };
    // ask TAs if this is formatted correctly

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsAuth) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

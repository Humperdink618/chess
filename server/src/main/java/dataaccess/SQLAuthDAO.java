package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";
        try(var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());

                preparedStatement.executeUpdate();
                // note: primary keys that are strings do not require any generated keys, so don't worry about it
                return new AuthData(authData.authToken(), authData.username());
            }
        } catch (SQLException e){
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        // similar to query format
        // try (var conn = DatabaseManager.getConnection()) {
        String statement = "SELECT authToken, username FROM authdata WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()){
                        String myAuthToken = rs.getString("authToken");
                        String username = rs.getString("username");
                        return new AuthData(myAuthToken, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM authdata WHERE authToken=?";
        try(var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e){
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        String statement = "TRUNCATE authdata";
        try(var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e){
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    // for testing purposes only
    public boolean empty() {
        // TODO: not implemented
        // for testing purposes only
        /*
        String statement = "SELECT COUNT(*) FROM user";
        try(var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement
                         = conn.prepareStatement(statement)) {
                if(preparedStatement.executeQuery() = 0){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        } */
        return false; // fix this later
        // SQL COUNT method might be helpful here
    }
}

package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;

    // For testing purposes
    boolean empty();
}

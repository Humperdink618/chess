package dataaccess;

import model.UserData;

// import java.util.Collection;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;


    void clear() throws DataAccessException;
}

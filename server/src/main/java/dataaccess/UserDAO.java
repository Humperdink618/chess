package dataaccess;

import model.UserData;

// import java.util.Collection;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    // void deleteUser(String username) throws DataAccessException;
    //  just in case. Not sure if I will need the above method, but good to think about

    void clearUserData() throws DataAccessException;
}

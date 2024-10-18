package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData){
        users.put(userData.username(),userData);
    }

    public UserData getUser(String username){
        return users.get(username);
    }

    /*
    public void deleteUser(String username){
        users.remove(username);
    }
    // not sure if I need this. I'll write it down anyway, just in case.

 */
    public void clearUserData(){
        users.clear();
    }

}

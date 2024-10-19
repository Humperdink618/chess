package dataaccess;

import model.AuthData;

//import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    //private String authToken = UUID.randomUUID().toString();
    final private HashMap<String, AuthData> authDataObjects = new HashMap<>();

    public AuthData createAuth(AuthData authData){
        //authData = new AuthData(authToken, authData.username());

        authDataObjects.put(authData.authToken(), authData);
        return authData;
    }

    public AuthData getAuth(String authToken){
        return authDataObjects.get(authToken);
    }

    public void deleteAuth(String authToken){
        authDataObjects.remove(authToken);
    }

    public void clear(){
        authDataObjects.clear();
    }

}

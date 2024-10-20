package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;



public class UserService {
        private final UserDAO userDAO;

        private final AuthDAO authDAO;

        public UserService(UserDAO userDAO, AuthDAO authDAO){
            this.userDAO = userDAO;
            this.authDAO = authDAO;
        }

        public static String generateToken() {
            return UUID.randomUUID().toString();
        }

        public RegisterResult register(RegisterRequest registerRequest) throws Exception {
            userDAO.getUser(registerRequest.username());
            if(userDAO.getUser(registerRequest.username()) == null){
                userDAO.createUser(
                        new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
                String authToken = generateToken();
                authDAO.createAuth(new AuthData(authToken, registerRequest.username()));
                return new RegisterResult(registerRequest.username(), authToken);
            }
            throw new Exception("Error: already taken");
            // TODO: potentially in the future, may change the exception type
            // create authToken here
        }
        public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
            userDAO.getUser(loginRequest.username());
            String authToken = generateToken();
            authDAO.createAuth(new AuthData(authToken, loginRequest.username()));
            return new LoginResult(loginRequest.username(), authToken);
            // create authToken here
        }
        public void logout(LogoutRequest logoutRequest) throws DataAccessException {
            // check if authToken is valid
            authDAO.getAuth(logoutRequest.authToken());
            authDAO.deleteAuth(logoutRequest.authToken());
        }
        // delete authToken
}

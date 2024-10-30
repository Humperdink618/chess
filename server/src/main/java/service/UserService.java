package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestExceptionChess;
import dataaccess.UserDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
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

        public RegisterResult register(RegisterRequest registerRequest)
                throws BadRequestExceptionChess, AlreadyTakenException, DataAccessException {
            // do request checking
            if(registerRequest.username() == null
                    || registerRequest.password() == null
                    || registerRequest.email() == null) {
                throw new BadRequestExceptionChess("Error: bad request");
            }
            userDAO.getUser(registerRequest.username());
            if(userDAO.getUser(registerRequest.username()) == null){
                String hash = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
                userDAO.createUser(
                        new UserData(registerRequest.username(), hash, registerRequest.email()));
                String authToken = generateToken();
                authDAO.createAuth(new AuthData(authToken, registerRequest.username()));
                return new RegisterResult(registerRequest.username(), authToken);
            }
            throw new AlreadyTakenException("Error: already taken");
            // create authToken here
        }
        public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {

            UserData userData = userDAO.getUser(loginRequest.username());
            if(userData == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            if(!BCrypt.checkpw(loginRequest.password(), userData.password())){
                throw new UnauthorizedException("Error: unauthorized");
            }
            // do password checking
            // also do request checking
            String authToken = generateToken();
            authDAO.createAuth(new AuthData(authToken, loginRequest.username()));
            return new LoginResult(loginRequest.username(), authToken);
            // create authToken here
        }
        public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
            // check if authToken is valid
            AuthData authData = authDAO.getAuth(logoutRequest.authToken());
            if(authData == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            authDAO.deleteAuth(logoutRequest.authToken());
        }
        // delete authToken
}

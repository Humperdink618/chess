package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exceptionChess.AlreadyTakenException;
import exceptionChess.BadRequestExceptionChess;
import dataaccess.UserDAO;
import exceptionChess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.Objects;
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
        // note: DataAccessException inherits from Exception, so I don't actually need to specify it
        // here. Only reason I'm doing so is to show that I can throw multiple exception types via
        // comma delimitation.
        public RegisterResult register(RegisterRequest registerRequest)
                throws BadRequestExceptionChess, AlreadyTakenException, DataAccessException {
            // do request checking
            if(registerRequest.username() == null || registerRequest.password() == null
                    || registerRequest.email() == null) {
                throw new BadRequestExceptionChess("Error: bad request");
            }
            userDAO.getUser(registerRequest.username());
            if(userDAO.getUser(registerRequest.username()) == null){
                userDAO.createUser(
                        new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
                String authToken = generateToken();
                authDAO.createAuth(new AuthData(authToken, registerRequest.username()));
                return new RegisterResult(registerRequest.username(), authToken);
            }
            throw new AlreadyTakenException("Error: already taken");
            // TODO: potentially in the future, may change the exception type
            // create authToken here
        }
        public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {

            UserData userData = userDAO.getUser(loginRequest.username());
            if(userData == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            if(!Objects.equals(userData.password(), loginRequest.password())){
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

package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service_exception.AlreadyTakenException;
import service_exception.BadRequestExceptionChess;
import service_exception.UnauthorizedException;
import model.AuthData;
import model.GameData;
import request.CreateRequest;
import request.JoinRequest;
import request.ListRequest;
import result.CreateResult;
import result.ListResult;
import java.util.Objects;

public class GameService {
        private final AuthDAO authDAO;
        private final GameDAO gameDAO;

        public GameService(AuthDAO authDAO, GameDAO gameDAO){
            this.authDAO = authDAO;
            this.gameDAO = gameDAO;
        }

        public ListResult listGames(ListRequest listRequest) throws UnauthorizedException, DataAccessException {
            authDAO.getAuth(listRequest.authToken());
            if(authDAO.getAuth(listRequest.authToken()) == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            gameDAO.listGames();
            return new ListResult(gameDAO.listGames());
        }

        public CreateResult createGame(CreateRequest createRequest)
                throws BadRequestExceptionChess, UnauthorizedException, DataAccessException {
            if(createRequest.gameName() == null || createRequest.authToken() == null){
                throw new BadRequestExceptionChess("Error: bad request");
            }
            AuthData authData = authDAO.getAuth(createRequest.authToken());
            // do request checking
            if(authData == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            int gameID = gameDAO.createGame(createRequest.gameName());
            return new CreateResult(gameID);
        }

        public void joinGame(JoinRequest joinRequest)
                throws BadRequestExceptionChess, UnauthorizedException, AlreadyTakenException, DataAccessException {
            if(joinRequest.gameID() == null
                    || joinRequest.authToken() == null
                    || joinRequest.playerColor() == null){
                throw new BadRequestExceptionChess("Error: bad request");
            }
            AuthData authData = authDAO.getAuth(joinRequest.authToken());
            if(authData == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            GameData gameData = gameDAO.getGame(joinRequest.gameID());
            if(gameData == null){
                throw new BadRequestExceptionChess("Error: bad request");
            }
            if(Objects.equals(joinRequest.playerColor(), "WHITE")){
                if(gameData.whiteUsername() != null){
                    throw new AlreadyTakenException("Error: already taken");
                }
                gameDAO.updateGame(
                        new GameData(
                                joinRequest.gameID(),
                                authData.username(),
                                gameData.blackUsername(),
                                gameData.gameName(),
                                gameData.game()
                                ));
            } else if(Objects.equals(joinRequest.playerColor(), "BLACK")){
                if(gameData.blackUsername() != null){
                    throw new AlreadyTakenException("Error: already taken");
                }
                gameDAO.updateGame(
                        new GameData(
                                joinRequest.gameID(),
                                gameData.whiteUsername(),
                                authData.username(),
                                gameData.gameName(),
                                gameData.game()
                        ));
            }
            else {
               throw new BadRequestExceptionChess("Error: bad request");
            }
        }
}

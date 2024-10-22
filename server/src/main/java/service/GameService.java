package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
        // note: of all the services, this one will be the most complicated. Talk to TAs for recommendations
        // on implementing this service.
        private final AuthDAO authDAO;
        private final GameDAO gameDAO;

        public GameService(AuthDAO authDAO, GameDAO gameDAO){
            this.authDAO = authDAO;
            this.gameDAO = gameDAO;
        }

        public ListResult listGames(ListRequest listRequest) throws Exception {
            authDAO.getAuth(listRequest.authToken());
            if(authDAO.getAuth(listRequest.authToken()) == null){
                throw new Exception("Error: unauthorized");
            }
            gameDAO.listGames();
            return new ListResult(gameDAO.listGames());
        }
        public CreateResult createGame(CreateRequest createRequest) throws Exception {
            authDAO.getAuth(createRequest.authToken());
            if(authDAO.getAuth(createRequest.authToken()) == null){
                throw new Exception("Error: unauthorized");
            }
            gameDAO.createGame(createRequest.gameName());
            return new CreateResult(gameDAO.createGame(createRequest.gameName()));
            // also, may need to change the return type and parameters at some point.
        }
        public void joinGame(JoinRequest joinRequest) throws Exception {
            authDAO.getAuth(joinRequest.authToken());
            if(authDAO.getAuth(joinRequest.authToken()) == null){
                throw new Exception("Error: unauthorized");
            }
            AuthData authData = authDAO.getAuth(joinRequest.authToken());
            gameDAO.getGame(joinRequest.gameID());
//            if(gameDAO.getGame(joinRequest.gameID()) == null){
//                throw new Exception("Error: bad request");
//            }
            // may need to double-check this with TAs
            GameData gameData = gameDAO.getGame(joinRequest.gameID());
            if(joinRequest.playerColor() == "WHITE"){
                if(gameData.whiteUsername() != null){
                    throw new Exception("Error: already taken");
                }
                gameDAO.updateGame(
                        new GameData(joinRequest.gameID(),
                                authData.username(),
                                gameData.blackUsername(),
                                gameData.gameName(),
                                gameData.game()
                                ));
            } else if(joinRequest.playerColor() == "BLACK"){
                if(gameData.blackUsername() != null){
                    throw new Exception("Error: already taken");
                }
                gameDAO.updateGame(
                        new GameData(joinRequest.gameID(),
                                gameData.whiteUsername(),
                                authData.username(),
                                gameData.gameName(),
                                gameData.game()
                        ));
            }
//            else {
//                throw new Exception("Error: bad request");
//            }
            // ask TAs about this
        }

        // also, may need to change the parameters at some point.
}

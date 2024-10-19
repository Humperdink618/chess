package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
        // note: of all the services, this one will be the most complicated. Talk to TAs for recommendations
        // on implementing this service.
        public ListResult listGames(ListRequest listRequest) throws DataAccessException {
            return null; // TODO: not implemented
            // also, may need to change the return type at some point.
        }
        public CreateResult createGame(CreateRequest createRequest) throws DataAccessException {
            return null; // TODO: not implemented
            // also, may need to change the return type and parameters at some point.
        }
        public void joinGame(JoinRequest joinRequest) throws DataAccessException {}
            // TODO: not implemented
            // also, may need to change the parameters at some point.
}

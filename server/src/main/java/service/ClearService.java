package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class ClearService {
        private final UserDAO userDAO;

        private final GameDAO gameDAO;

        private final AuthDAO authDAO;

        public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
            this.userDAO = userDAO;
            this.gameDAO = gameDAO;
            this.authDAO = authDAO;
        }
    /*
        public void clearUserData() throws DataAccessException {
            userDAO.clearUserData();
        }
        public void clearGameData() throws DataAccessException {
            gameDAO.clearGameData();
        }
        public void clearAuthData() throws DataAccessException {
            authDAO.clearAuth();
        }

        // not sure which method would be most ideal. Specs have one endpoint, so currently only using one
        // endpoint that calls three separate methods that do only one thing.
        // will need to meet with TAs to double-check.

     */
        public void clear() throws DataAccessException {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();
        }
}

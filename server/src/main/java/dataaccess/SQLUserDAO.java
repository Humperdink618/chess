package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.sql.*;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    // when a user provides a password, hash it before storing it in the database
   /*
    void storeUserPassword(String username, String password) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        writeHashedPasswordToDatabase(username, hashedPassword);
    }

    boolean verifyUser(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    */
    // the above methods may be useful later, but do adapt them so that they fit your code

    public void createUser(UserData userData) throws DataAccessException {
        // TODO: not implemented
    }

    public UserData getUser(String username) throws DataAccessException {
        // TODO: not implemented
        return null;
    }

    public void clear() throws DataAccessException {
      //  var statement = "TRUNCATE TABLE user";
     //   executeUpdate(statement);
       // var statement = "DELETE FROM user";
     //   executeUpdate(statement);
        // TODO: not implemented
    }

    @Override
    public boolean empty() {
        // TODO: not implemented
        // for testing purposes only
        return false; // change this later
    }

    private final String[] createStatementsUser = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(100) NOT NULL,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsUser) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

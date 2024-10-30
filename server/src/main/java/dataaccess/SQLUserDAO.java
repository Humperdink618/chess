package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }

    // when a user provides a password, hash it before storing it in the database
   /*
    void storeUserPassword(String username, String clearTextPassword) {
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
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try(var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());

                preparedStatement.executeUpdate();
                // note: primary keys that are strings do not require any generated keys, so don't worry about it
            }
        } catch (SQLException e){
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
       // return new UserData(userData.username(), hash, userData.email());

    }

    public UserData getUser(String username) throws DataAccessException {
        // similar to query format
       // try (var conn = DatabaseManager.getConnection()) {
        String statement = "SELECT username, password, email FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
               try (var rs = preparedStatement.executeQuery()) {
                   while (rs.next()){
                       String myUsername = rs.getString("username");
                       String password = rs.getString("password");
                       String email = rs.getString("email");
                       return new UserData(myUsername, password, email);
                   }
               }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void clear() throws DataAccessException {
      //  var statement = "TRUNCATE TABLE user";
     //   executeUpdate(statement);
       // String statement = "DELETE FROM user";
        String statement = "TRUNCATE user";
        try(var conn = DatabaseManager.getConnection()){
            try (var preparedStatement
                         = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e){
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public boolean empty() /*throws DataAccessException */{
        // TODO: not implemented
        // for testing purposes only
        /*
        String statement = "SELECT COUNT(*) FROM user";
        try(var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement
                         = conn.prepareStatement(statement)) {
                if(preparedStatement.executeQuery() = 0){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        } */
        return false;
        // SQL COUNT method might be helpful here
    }
/*
    // may not need this method. Just try to figure out what it does and if I would actually need it.
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s",
                                          statement,
                                          e.getMessage()));
        }
    }

 */
}

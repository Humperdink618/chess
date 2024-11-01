package dataaccess;

import model.UserData;

import java.sql.SQLException;
import java.sql.*;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }

    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());

                preparedStatement.executeUpdate();
                // note: primary keys that are strings do not require any generated keys, so don't worry about it
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        // similar to query format
        String statement = "SELECT username, password, email FROM user WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
               try (ResultSet rs = preparedStatement.executeQuery()) {
                   while (rs.next()) {
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
       // String statement = "DELETE FROM user";
        String statement = "TRUNCATE user";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public boolean empty() {
        // for testing purposes only

        String statement = "SELECT COUNT(*) FROM user";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) == 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the Server");
        } catch (DataAccessException ex) {
            System.out.println("Unable to read data");
        }
        return false;
        // SQL COUNT method might be helpful here
    }
}
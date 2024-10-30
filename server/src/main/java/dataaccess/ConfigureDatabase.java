package dataaccess;

import java.sql.SQLException;

public class ConfigureDatabase {

    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS user (
              username VARCHAR(100) NOT NULL,
              password VARCHAR(100) NOT NULL,
              email VARCHAR(100) NOT NULL,
              PRIMARY KEY (username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
              authToken VARCHAR(100) NOT NULL,
              username VARCHAR(100) NOT NULL,
              PRIMARY KEY (authToken)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS gamedata (
              gameID INT(100) NOT NULL AUTO_INCREMENT,
              whiteUsername VARCHAR(100),
              blackUsername VARCHAR(100),
              gameName VARCHAR(100) NOT NULL,
              game LONGTEXT NOT NULL,
              PRIMARY KEY (gameID)
            )
            """
    };

    static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : CREATE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

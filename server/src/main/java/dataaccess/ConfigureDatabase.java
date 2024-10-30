package dataaccess;

import java.sql.SQLException;

public class ConfigureDatabase {

    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(100) NOT NULL,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
              `authToken` varchar(100) NOT NULL,
              `username` varchar(100), NOT NULL,
              PRIMARY KEY (`authToken`),
              FOREIGN KEY (`username`) REFERENCES user(username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS gamedata (
              `gameID` int(100) NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(100),
              `blackUsername` varchar(100),
              `gameName varchar(100) NOT NULL,
              `game` LONGTEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              FOREIGN KEY (`whiteUsername`) REFERENCES user(username),
              FOREIGN KEY (`blackUsername`) REFERENCES user(username)
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

package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }

    public int createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO gamedata (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame myGame = new ChessGame();
        String game = new Gson().toJson(myGame);
        int gameID = executeUpdate(statement, null, null, gameName, game);
        GameData gameData = new GameData(gameID, null, null, gameName, myGame);
        return gameData.gameID();
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> myGames = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM gamedata";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        myGames.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return myGames;
        // not storing anything in a collection in this DAO, but can still return a collection (data retrieved from
        //  database)
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        return new GameData(gameID, whiteUsername, blackUsername, gameName, null);
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        String statement = "UPDATE gamedata SET whiteUsername=?, " +
                "blackUsername=?, gameName=?, game=? WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                String game = new Gson().toJson(gameData.game());
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, game);
                preparedStatement.setInt(5, gameData.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            String.format("Unable to update database: %s, %s", statement, e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gamedata WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int myGameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String game = rs.getString("game");
                        ChessGame myGame = new Gson().fromJson(game, ChessGame.class);
                        return new GameData(myGameID, whiteUsername, blackUsername, gameName, myGame);
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
        String statement = "TRUNCATE gamedata";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    // for testing purposes only
    public boolean empty() {
        String statement = "SELECT COUNT(*) FROM gamedata";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
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
}
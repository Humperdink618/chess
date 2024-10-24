package server;

import com.google.gson.Gson;
import dataaccess.*;
import exceptionChess.AlreadyTakenException;
import exceptionChess.BadRequestExceptionChess;
import exceptionChess.UnauthorizedException;
import request.*;
import result.CreateResult;
import result.ListResult;
import result.LoginResult;
import result.RegisterResult;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;


public class Server {
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    // for Phase 4, eventually change these

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String clearHandler(Request req, Response res){
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        try{
            clearService.clear();
            // could also do new Gson().toJson(new Object());
            return "{}";
        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

    private Object registerHandler(Request req, Response res) {
        UserService userService = new UserService(userDAO, authDAO);
        try{
            RegisterRequest reqBody = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult resBody = userService.register(reqBody);
            return new Gson().toJson(resBody);

        } catch (BadRequestExceptionChess ex) {
            //if(Objects.equals(ex.getMessage(), "Error: bad request")){
            res.status(400);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (AlreadyTakenException ex) {
            res.status(403);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;
        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

    private Object loginHandler(Request req, Response res) {
        UserService userService = new UserService(userDAO, authDAO);
        try{
            LoginRequest reqBody = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult resBody = userService.login(reqBody);
            return new Gson().toJson(resBody);

        } catch (UnauthorizedException ex) {
            res.status(401);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

    private String logoutHandler(Request req, Response res) {
        UserService userService = new UserService(userDAO, authDAO);
        try{
            LogoutRequest reqHead = new LogoutRequest(req.headers("authorization"));
            userService.logout(reqHead);
            return "{}";

        } catch (UnauthorizedException ex) {
            res.status(401);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

    private Object listGamesHandler(Request req, Response res) {
        GameService gameService = new GameService(authDAO, gameDAO);
        try{
            ListRequest reqHead = new ListRequest(req.headers("authorization"));
            ListResult resBody = gameService.listGames(reqHead);
            return new Gson().toJson(resBody);

        } catch (UnauthorizedException ex) {
            res.status(401);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

    private Object createGameHandler(Request req, Response res) {
        GameService gameService = new GameService(authDAO, gameDAO);
        try{
            String authToken = req.headers("authorization");
            CreateRequest reqBody = new Gson().fromJson(req.body(), CreateRequest.class);
            CreateRequest reqData = new CreateRequest(authToken, reqBody.gameName());
            CreateResult resBody = gameService.createGame(reqData);
            return new Gson().toJson(resBody);

        } catch (BadRequestExceptionChess ex) {
            res.status(400);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (UnauthorizedException ex) {
            res.status(401);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

    private String joinGameHandler(Request req, Response res) {
        GameService gameService = new GameService(authDAO, gameDAO);
        try{
            String authToken = req.headers("authorization");
            JoinRequest reqBody = new Gson().fromJson(req.body(), JoinRequest.class);
            JoinRequest reqData = new JoinRequest(authToken, reqBody.playerColor(), reqBody.gameID());
            gameService.joinGame(reqData);
            return "{}";

        } catch (AlreadyTakenException ex) {
            res.status(403);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (BadRequestExceptionChess ex) {
            res.status(400);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (UnauthorizedException ex) {
            res.status(401);
            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.body(body);
            return body;

        } catch (DataAccessException ex) {
            res.status(500);
            String body = new Gson().toJson(Map.of("message", "Error:" + ex.getMessage()));
            res.body(body);
            return body;
        }
    }

}

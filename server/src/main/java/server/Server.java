package server;

import com.google.gson.Gson;
import dataaccess.*;
import exceptionChess.AlreadyTakenException;
import exceptionChess.BadRequestExceptionChess;
import request.RegisterRequest;
import result.RegisterResult;
import service.ClearService;
import service.UserService;
import spark.*;

import java.util.Map;
import java.util.Objects;

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

    private Object registerHandler(Request req, Response res) throws Exception {
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

}

package ui.serverfacade;

import com.google.gson.Gson;
import exceptions.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;
import java.net.URL;
import java.util.Map;

public class ClientCommunicator {
    // note: this class contains the actual code for the HTTP methods (i.e. GET and POST) that ServerFacade calls

    public static <T> T makeRequest(String method,
                                    String path,
                                    Object request,
                                    Class<T> responseClass,
                                    String serverUrl,
                                    String auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            if (auth != null) {
                conn.addRequestProperty("authorization", auth);
            }

            writeBody(request, conn);
            conn.connect();
            throwIfNotSuccessful(conn);
            return readBody(conn, responseClass);
        } catch (Exception e) {
            //throw new Exception(500, e.getMessage());
            throw new ResponseException(e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection conn) throws IOException {
        if (request != null) {
            conn.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = conn.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void throwIfNotSuccessful(HttpURLConnection conn) throws IOException, ResponseException {
        int status = conn.getResponseCode();
        if (!isSuccessful(status)) {
            String message = readErrorMessage(conn);
            throw new ResponseException(message);
        }
    }


    private static <T> T readBody(HttpURLConnection conn, Class<T> responseClass) throws IOException {
        T response = null;
        if (conn.getContentLength() < 0) {
            try (InputStream respBody = conn.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static String readErrorMessage(HttpURLConnection conn) throws IOException {
        String response = null;
        if (conn.getContentLength() < 0) {
            try (InputStream respBody = conn.getErrorStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                response = new Gson().fromJson(reader, Map.class).get("message").toString();
            }
        }
        return response;
    }


    private static boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
/*
    public void doGet(String urlString, String auth) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("authorization", auth);

        connection.connect();

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            //connection.getHeaderField("Content-Length");

            InputStream responseBody = connection.getInputStream();
            // read and process body from InputStream ...
        } else {
            // SERVER RETURNED AN HTTP ERROR

            InputStream responseBody = connection.getErrorStream();
            // read and process error response body from InputStream ...
        }
    }

    public void doPost(String urlString, boolean isCreate, String auth) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        if(isCreate){
            connection.addRequestProperty("authorization", auth);
        }

        connection.connect();

        try(OutputStream requestBody = connection.getOutputStream();) {
            // write request body to OutputStream ...
        }

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            InputStream responseBody = connection.getInputStream();
            // read response body from InputStream ...
        } else {
            // SERVER RETURNED AN HTTP ERROR

            InputStream responseBody = connection.getErrorStream();
            // read and process error response body from InputStream ...
        }
    }
}
 */

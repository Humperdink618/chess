package ui.serverfacade;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientCommunicator {
    // TODO: implement ClientCommunicator
    // note: this class contains the actual code for the HTTP methods (i.e. GET and POST) that ServerFacade calls

    public void doGet(String urlString, String auth) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Authorization", auth);

        connection.connect();

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            connection.getHeaderField("Content-Length");

            InputStream responseBody = connection.getInputStream();
            // read and process body from InputStream ...
        } else {
            // SERVER RETURNED AN HTTP ERROR

            InputStream responseBody = connection.getErrorStream();
            // read and process error response body from InputStream ...
        }
    }
}

package co.edu.eci.arep.webserver.http.manage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * HttpConnectionExample
 */
public class HttpConnectionExample {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:35000/";

    /**
     * Main method
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        HttpConnectionExample http = new HttpConnectionExample();
        http.makeConnection("GET", "index.html", "");
    }

    /**
     * Constructor
     */
    public HttpConnectionExample() {

    }

    /**
     * This method makes a connection to the server
     * 
     * @param method   the method to use
     * @param endPoint the endpoint to connect
     * @param query    the query to send
     * 
     * @throws IOException
     * @return HttpURLConnection
     */
    public HttpURLConnection makeConnection(String method, String endPoint, String query) throws IOException {
        String newUrl = GET_URL + endPoint + query;
        URL obj = new URL(newUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        return con;
    }

    /**
     * This method gets the response from the server
     * 
     * @param method   the method to use
     * @param endPoint the endpoint to connect
     * @param query    the query to send
     * 
     * @throws IOException
     * @return String
     */
    public String getResponse(String method, String endPoint, String query) throws IOException {
        String newUrl = GET_URL + endPoint + query;
        URL obj = new URL(newUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // The following invocation perform the connection implicitly before getting the
        // code
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            return response.toString();
        } else {
            return "GET request not worked";
        }
    }

    /**
     * This method gets the response from the server
     * 
     * @param method   the method to use
     * @param endPoint the endpoint to connect
     * @param query    the query to send
     * 
     * @throws IOException
     * @return byte[]
     */
    public byte[] getResponseBytes(String method, String endPoint, String query) throws IOException {
        String newUrl = GET_URL + endPoint + query;
        URL url = new URL(newUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = con.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            in.close();
            return baos.toByteArray();
        } else {
            return "GET request not worked".getBytes();
        }

    }
}
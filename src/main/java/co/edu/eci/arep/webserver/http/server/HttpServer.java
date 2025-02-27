package co.edu.eci.arep.webserver.http.server;

/**
 *
 * @author mateo.forero-f
 */
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

import co.edu.eci.arep.webserver.http.manage.HttpRequest;
import co.edu.eci.arep.webserver.http.manage.HttpResponse;

/**
 * HttpServer
 */
public class HttpServer implements Runnable {

    private final int MAX_THREADS;
    private String staticFilesPath;
    private LambdaServer lambdaServer;
    private RestServer restServer;
    private ExecutorService pool;

    public HttpServer(int MAX_THREADS, String staticFilesPath) {
        this.MAX_THREADS = MAX_THREADS;
        if (staticFilesPath == "") {
            staticFilesPath = "/static";
        }

        staticFiles(staticFilesPath);
        this.lambdaServer = new LambdaServer(this);
        lambdaServer.createLambdaExampleEndPoints();
        this.restServer = new RestServer();
        restServer.loadComponents();
    }

    public void staticFiles(String path) {
        String folderPath = ("target/classes" + path);
        staticFilesPath = folderPath;
        Path pathF = Paths.get(folderPath);
        try {
            Files.createDirectories(pathF);
            System.out.println("Directory is created: " + pathF);
        } catch (IOException e) {
            System.out.println("Failed to create directory!");
        }
    }

    public String getStaticRestFilesPath() {
        return staticFilesPath;
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000;
    }

    /**
     * Run method that starts the server in the port 35000 and listens for incoming
     * connections
     */
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + getPort());
            System.exit(1);
        }
        pool = Executors.newFixedThreadPool(MAX_THREADS);
        System.out.println(MAX_THREADS);
        boolean running = true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                final Socket clientSocket = serverSocket.accept();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try (InputStream in = clientSocket.getInputStream()) {
                            OutputStream out = clientSocket.getOutputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            String inputLine;
                            String completeRequest = "";
                            while ((inputLine = reader.readLine()) != null) {
                                completeRequest += inputLine + "\n";
                                if (!reader.ready()) {
                                    break;
                                }
                            }
                            HttpRequest request = HttpRequest.parse(completeRequest);
                            HttpResponse response;
                            if (request != null) {
                                System.out.println(request.getMethod() + " " + request.getUri());
                                response = manageRequestFromEndPoint(request);
                                System.out.println(response.getStatusCode());
                            }
                            else{
                                response = HttpResponse.sendFailureMessage(400, "The request was inavlid.");
                            }

                            out.write(response.getBytes());
                            // Close streams and socket
                            out.close();
                            in.close();
                            clientSocket.close();
                            System.out.println("Connection has been closed");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method manages the request from the endpoint
     * 
     * @param typeRequest the type of request
     * @param resourceURI the URI of the resource
     * @param out         the output stream
     * @throws IOException
     * 
     * @return the response
     */
    public HttpResponse manageRequestFromEndPoint(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return HttpResponse.sendFailureMessage(400, "Bad request: Not valid request.");
        }
        HttpResponse httpResponse;
        URI resourceURI = httpRequest.getUri();
        if (resourceURI.getPath().endsWith(".html")) {
            httpResponse = sendGetHtmlString(httpRequest);
        } else if (resourceURI.getPath().endsWith(".css")) {
            httpResponse = sendGetCssString(httpRequest);
        } else if (resourceURI.getPath().endsWith(".js")) {
            httpResponse = sendGetJsString(httpRequest);
        } else if (resourceURI.getPath().endsWith(".png") || resourceURI.getPath().endsWith(".jpg")
                || resourceURI.getPath().endsWith(".jpeg") || resourceURI.getPath().endsWith(".gif")
                || resourceURI.getPath().endsWith(".svg")
                || resourceURI.getPath().endsWith(".ico")) {
            httpResponse = sendGetImageString(httpRequest);
        } else if (resourceURI.getPath().startsWith("/rest")) {
            httpResponse = restServer.manageRestControllerRequest(httpRequest);
        } else if (resourceURI.getPath().startsWith("/app")) {
            httpResponse = lambdaServer.manageRequestFromRestLambda(httpRequest);
        } else {
            httpResponse = HttpResponse.sendFailureMessage(404, "Not known endpoint.");
        }
        return httpResponse;
    }

    /**
     * This method send a response to the client of a GET request with a HTML file
     * 
     * @param resourceURI the URI of the resource
     * @param out         the output stream
     * 
     * @throws IOException
     * 
     * @return the response
     */
    private HttpResponse sendGetHtmlString(HttpRequest httpRequest) {
        URI resourceURI = httpRequest.getUri();
        int start = resourceURI.getPath().indexOf("/index");
        String fileName = staticFilesPath + resourceURI.getPath().substring(start);
        if (!new File(fileName).exists()) {
            return HttpResponse.sendFailureMessage(404, HttpResponse.NOT_FOUND_FILE);
        }
        HttpResponse response;
        String outputLine = "";
        // Leer el contenido desde el archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLine += line + "\n";
            }
            response = new HttpResponse();
            response.setStatusCode(200);
            response.addHeader("Content-Type", "text/html");
            response.setBody(outputLine);
        } catch (IOException e) {
            e.printStackTrace();
            response = HttpResponse.sendFailureMessage(500, HttpResponse.ERROR_READING_DATA);
        }
        return response;
    }

    /**
     * This method send a response to the client of a GET request with a CSS file
     * 
     * @param resourceURI the URI of the resource
     * @param out         the output stream
     * 
     * @throws IOException
     * 
     * @return the response
     */
    private HttpResponse sendGetCssString(HttpRequest httpRequest) {
        URI resourceURI = httpRequest.getUri();
        int start = resourceURI.getPath().indexOf("/styles");
        String fileName = staticFilesPath + resourceURI.getPath().substring(start);

        if (!new File(fileName).exists()) {
            return HttpResponse.sendFailureMessage(404, HttpResponse.NOT_FOUND_FILE);
        }
        HttpResponse response;
        String outputLine = "";
        // Leer el contenido desde el archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLine += line + "\n";
            }
            response = new HttpResponse();
            response.setStatusCode(200);
            response.addHeader("Content-Type", "text/css");
            response.setBody(outputLine);
        } catch (IOException e) {
            e.printStackTrace();
            response = HttpResponse.sendFailureMessage(500, HttpResponse.ERROR_READING_DATA);
        }
        return response;
    }

    /**
     * This method send a response to the client of a GET request with a JS file
     * 
     * @param resourceURI the URI of the resource
     * @param out         the output stream
     * 
     * @throws IOException
     * 
     * @return the response
     */
    private HttpResponse sendGetJsString(HttpRequest httpRequest) {
        URI resourceURI = httpRequest.getUri();
        int start = resourceURI.getPath().indexOf("/scripts");
        String fileName = staticFilesPath + resourceURI.getPath().substring(start);

        if (!new File(fileName).exists()) {
            return HttpResponse.sendFailureMessage(404, HttpResponse.NOT_FOUND_FILE);
        }
        HttpResponse response;
        String outputLine = "";
        // Leer el contenido desde el archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLine += line + "\n";
            }
            response = new HttpResponse();
            response.setStatusCode(200);
            response.addHeader("Content-Type", "text/javascript");
            response.setBody(outputLine);
        } catch (IOException e) {
            e.printStackTrace();
            response = HttpResponse.sendFailureMessage(500, HttpResponse.ERROR_READING_DATA);
        }
        return response;
    }

    /**
     * This method send a response to the client of a GET request with an image
     * 
     * @param resourceURI the URI of the resource
     * @param out         the output stream
     * 
     * @throws IOException
     * 
     * @return the response
     */
    private HttpResponse sendGetImageString(HttpRequest httpRequest) {
        URI resourceURI = httpRequest.getUri();
        int start = resourceURI.getPath().indexOf("/images");
        String fileName = staticFilesPath + resourceURI.getPath().substring(start);
        File file = new File(fileName);
        if (!file.exists()) {
            return HttpResponse.sendFailureMessage(404, HttpResponse.NOT_FOUND_FILE);
        }
        HttpResponse response;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] imageBytes = inputStream.readAllBytes();
            response = new HttpResponse();
            // Determine the file extension to set the correct Content-Type
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            String contentType = "image/" + extension;
            response.setStatusCode(200);
            response.addHeader("Content-Type", contentType);
            response.addHeader("Content-Length", String.valueOf(file.length()));
            response.setBody(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            response = HttpResponse.sendFailureMessage(500, HttpResponse.ERROR_READING_DATA);
        }
        return response;
    }

}

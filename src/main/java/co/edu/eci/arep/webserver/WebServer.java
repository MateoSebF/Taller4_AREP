package co.edu.eci.arep.webserver;

import co.edu.eci.arep.webserver.controller.ShutdownController;
import co.edu.eci.arep.webserver.http.server.HttpServer;

/**
 *
 * @author mateo.forero-f
 */
public class WebServer {

    private static WebServer singleton;

    private HttpServer httpServer;

    public static void main(String[] args) {
        getWebServerSingleton();
    }

    public static WebServer getWebServerSingleton() {
        if (singleton == null) {
            singleton = new WebServer();
        }
        return singleton;
    }

    public WebServer() {
        httpServer = new HttpServer(10, "/static");

        // Set the HttpServer instance in the ShutdownController
        ShutdownController.setServer(httpServer);

        Thread runningServer = new Thread(httpServer);
        runningServer.start();

        // Add a shutdown hook to stop the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server gracefully...");
            httpServer.stop();
        }));
    }

}

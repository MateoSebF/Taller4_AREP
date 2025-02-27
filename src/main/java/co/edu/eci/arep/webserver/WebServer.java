package co.edu.eci.arep.webserver;

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
        Thread runningServer = new Thread(httpServer);
        runningServer.start();
    }

}

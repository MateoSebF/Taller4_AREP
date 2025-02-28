package co.edu.eci.arep.webserver.controller;

import co.edu.eci.arep.webserver.http.server.HttpServer;
import co.edu.eci.arep.webserver.reflection.annotation.RestController;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.GetMapping;

@RestController
public class ShutdownController {

    private static HttpServer httpServer;

    // Method to set the server instance (call this when starting the server)
    public static void setServer(HttpServer server) {
        httpServer = server;
    }

    @GetMapping("/shutdown")
    public static String shutdownServer() {
        if (httpServer != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Small delay to ensure response is sent
                    httpServer.stop();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            return "Server is shutting down...";
        }
        return "Server instance is not available.";
    }
}

package co.edu.eci.arep.webserver.http.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import co.edu.eci.arep.webserver.http.manage.HttpRequest;
import co.edu.eci.arep.webserver.http.manage.HttpResponse;

public class LambdaServer {

    private HashMap<String, BiFunction<HttpRequest, HttpResponse, HttpResponse>> servicesLambda;
    private HttpServer httpServer;

    public LambdaServer(HttpServer httpServer) {
        this.servicesLambda = new HashMap<>();
        this.httpServer = httpServer;
    }

    public void createLambdaExampleEndPoints() {
        makeNewEndPoint("get", "/hello", (req, resp) -> {
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "text/plain");
            String body = "Hello " + req.getQueryParam("name");
            resp.setBody(body);
            return resp;
        });

        makeNewEndPoint("get", "/pi", (req, resp) -> {
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "text/plain");
            resp.setBody(String.valueOf(Math.PI));
            return resp;
        });

        makeNewEndPoint("get", "/mateo", (req, resp) -> {
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "text/plain");
            resp.setBody("Mateo Forero");
            return resp;
        });

        makeNewEndPoint("get", "/image", (req, resp) -> {
            HttpRequest request = HttpRequest.parse("GET /images/placeholder.png HTTP/1.1");
            resp = httpServer.manageRequestFromEndPoint(request);
            return resp;
        });

        HashMap<String, String> restApiObjects = new HashMap<>();
        makeNewEndPoint("get", "/objectFromQuery", (req, resp) -> {
            Map<String, String> queryParams = req.getQueryParams();
            String name = queryParams.get("name");
            if (name == null || !restApiObjects.containsKey(name)) {
                return HttpResponse.sendFailureMessage(404, "The object not exist.");
            }
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "application/json");
            resp.setBody(restApiObjects.get(name));
            return resp;
        });

        makeNewEndPoint("post", "/objectFromQuery", (req, resp) -> {
            String name = "";
            name = req.getQueryParams().get("name");
            if (name == null || restApiObjects.containsKey(name)) {
                return HttpResponse.sendFailureMessage(404, "The object not exist or is already register.");
            }
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "application/json");
            String object = "{\n";
            Map<String, String> queryParams = req.getQueryParams();
            ArrayList<String> keys = new ArrayList<>(queryParams.keySet());

            for (String key : keys) {
                String value = queryParams.get(key);
                System.out.println("Key: " + key + " Value: " + value);
                // Decode the value
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                if (keys.indexOf(key) != keys.size() - 1) {
                    object += "\"" + key + "\" : \"" + value + "\" ,\n";
                } else {
                    object += "\"" + key + "\" : \"" + value + "\"\n";
                }
            }
            object += "}";
            restApiObjects.put(name, object);
            resp.setBody(object);
            return resp;
        });

        makeNewEndPoint("put", "/objectFromQuery", (req, resp) -> {
            String name = req.getQueryParams().get("name");
            if (name == null || !restApiObjects.containsKey(name)) {
                return HttpResponse.sendFailureMessage(404, "The object not exist.");
            }
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "application/json");
            String object = "{\n";
            Map<String, String> queryParams = req.getQueryParams();
            ArrayList<String> keys = new ArrayList<>(queryParams.keySet());

            for (String key : keys) {
                String value = queryParams.get(key);
                System.out.println("Key: " + key + " Value: " + value);
                // Decode the value
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                if (keys.indexOf(key) != keys.size() - 1) {
                    object += "\"" + key + "\" : \"" + value + "\" ,\n";
                } else {
                    object += "\"" + key + "\" : \"" + value + "\"\n";
                }
            }
            object += "}";
            restApiObjects.put(name, object);
            resp.setBody(object);
            return resp;
        });

        makeNewEndPoint("delete", "/objectFromQuery", (req, resp) -> {
            String name = req.getQueryParams().get("name");
            System.out.println("Name: " + name);
            if (name == null || !restApiObjects.containsKey(name)) {
                return HttpResponse.sendFailureMessage(404, "The object not exist.");
            }
            resp.setStatusCode(200);
            resp.addHeader("Content-Type", "application/json");
            resp.setBody(restApiObjects.get(name));
            restApiObjects.remove(name);
            return resp;
        });
    }

    public void makeNewEndPoint(String method, String route,
            BiFunction<HttpRequest, HttpResponse, HttpResponse> function) {
        if (method.toLowerCase().equals("get")) {
            servicesLambda.put("_get/app" + route, function);
        } else if (method.toLowerCase().equals("post")) {
            servicesLambda.put("_post/app" + route, function);
        } else if (method.toLowerCase().equals("put")) {
            servicesLambda.put("_put/app" + route, function);
        } else if (method.toLowerCase().equals("delete")) {
            servicesLambda.put("_delete/app" + route, function);
        }
    }

    /**
     * This method send a response to the client of a GET request with an object
     * from the query
     * 
     * @param queryParams the query parameters
     * @param out         the output stream
     * 
     * @throws IOException
     * 
     * @return the response
     */
    public HttpResponse manageRequestFromRestLambda(HttpRequest httpRequest) {
        String endPoint = "_" + httpRequest.getMethod().toLowerCase() + httpRequest.getUri().getPath();

        if (!servicesLambda.containsKey(endPoint)) {
            return HttpResponse.sendFailureMessage(404, "A rest controller service not exist for this endpoint.");
        }
        System.out.println(endPoint);
        HttpResponse response = new HttpResponse();
        response = servicesLambda.get(endPoint).apply(httpRequest, response);
        return response;
    }

}

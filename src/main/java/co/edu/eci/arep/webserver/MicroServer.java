package co.edu.eci.arep.webserver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.edu.eci.arep.webserver.http.HttpResponse;
import co.edu.eci.arep.webserver.http.HttpServer;
import co.edu.eci.arep.webserver.reflection.annotation.DeleteMapping;
import co.edu.eci.arep.webserver.reflection.annotation.PutMapping;
import co.edu.eci.arep.webserver.reflection.annotation.RequestParam;
import co.edu.eci.arep.webserver.reflection.annotation.GetMapping;
import co.edu.eci.arep.webserver.reflection.annotation.PostMapping;
import co.edu.eci.arep.webserver.reflection.annotation.RestController;

public class MicroServer {

    public static HashMap<String, Method> servicesAnnotation = new HashMap<>();    

    public static void main(String[] args) {
        loadComponents();
    }

    private static void loadComponents() {
        Path path = Paths.get("target/classes");
        try {
            List<Path> files = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path file : files) {
                makeNewEndPoint(file);
            }
        } catch (Exception e) {

        }
    }

    private static void makeNewEndPoint(Path file) {
        if (!file.toString().endsWith(".class"))
            return;
        String baseClassName = file.toString().substring(15, file.toString().length() - 6);
        String className = baseClassName.replace("\\", ".");
        try {
            Class<?> restClass = Class.forName(className);
            if (!restClass.isAnnotationPresent(RestController.class))
                return;
            System.out.println("The " + restClass.getName() + " is a rest controller class");
            for (Method m : restClass.getDeclaredMethods()) {
                String endPoint = "";
                if (m.isAnnotationPresent(GetMapping.class)) {
                    GetMapping annotation = m.getAnnotation(GetMapping.class);
                    endPoint = "_get" + annotation.value();
                } else if (m.isAnnotationPresent(PostMapping.class)) {
                    PostMapping annotation = m.getAnnotation(PostMapping.class);
                    endPoint = "_post" + annotation.value();
                } else if (m.isAnnotationPresent(PutMapping.class)) {
                    PutMapping annotation = m.getAnnotation(PutMapping.class);
                    endPoint = "_put" + annotation.value();
                } else if (m.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping annotation = m.getAnnotation(DeleteMapping.class);
                    endPoint = "_delete" + annotation.value();
                }
                addServiceAnnotation(endPoint, m);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static HttpResponse simulateRequest(String endpoint) throws IOException {
        System.out.println("\nProcessing request: " + endpoint);

        // Extract path and query parameters
        String[] parts = endpoint.split("\\?", 2);
        String path = parts[0]; // URL path (e.g., "get_/greeting")
        String queryString = parts.length > 1 ? parts[1] : ""; // Query string

        // Convert query string to a map
        Map<String, String> queryParams = Arrays.stream(queryString.split("&"))
                .map(param -> param.split("=", 2))
                .filter(kv -> kv.length == 2)
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

        Method method = servicesAnnotation.get(path);
        if (method == null){
            return HttpServer.sendNotFoundResponse();
        }

        try {
            // Process method parameters dynamically
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    String paramName = requestParam.value();
                    String defaultValue = requestParam.defaultValue();

                    // Get parameter from query string or use default
                    args[i] = queryParams.getOrDefault(paramName, defaultValue);
                }
            }
            HttpResponse httpResponse;
            System.out.println(method.getReturnType().getName());
            if(method.getReturnType().equals(HttpResponse.class)){
                httpResponse = (HttpResponse)method.invoke(null, args);
            }   
            else{
                httpResponse = new HttpResponse();
                httpResponse.setStatusCode(200);
                httpResponse.setBody((String) method.invoke(null, args));
            }
            return httpResponse;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(500);
            httpResponse.setBody("500 Internal Server Error");
            return httpResponse;
        }
    }


    public static void addServiceAnnotation(String endpoint, Method method) {
        servicesAnnotation.put(endpoint, method);
    }

}

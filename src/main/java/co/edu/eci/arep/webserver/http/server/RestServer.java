package co.edu.eci.arep.webserver.http.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.edu.eci.arep.webserver.http.manage.HttpRequest;
import co.edu.eci.arep.webserver.http.manage.HttpResponse;
import co.edu.eci.arep.webserver.reflection.annotation.RequestParam;
import co.edu.eci.arep.webserver.reflection.annotation.RestController;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.DeleteMapping;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.GetMapping;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.PostMapping;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.PutMapping;

public class RestServer {

    private HashMap<String, Method> servicesAnnotation;

    public RestServer() {
        this.servicesAnnotation = new HashMap<>();
    }

    public void addServiceAnnotation(String endpoint, Method method) {
        servicesAnnotation.put(endpoint, method);
    }

    public void loadComponents() {
        Path path = Paths.get("target/classes");
        try {
            List<Path> files = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path file : files) {
                System.out.println(file.toString());
                makeNewEndPoint(file);
            }
        } catch (Exception e) {

        }
    }

    private void makeNewEndPoint(Path file) {
        if (!file.toString().endsWith(".class"))
            return;
        String path = file.toString(); 
        int start = path.indexOf("co");
        int end = path.lastIndexOf(".class");
        String baseClassName = path.substring(start, end);
        String className = baseClassName.replace("\\", ".");
        className = className.replace("/", ".");
        try {
            System.out.println(className);
            Class<?> restClass = Class.forName(className);
            if (!restClass.isAnnotationPresent(RestController.class))
                return;
            System.out.println("The " + restClass.getName() + " is a rest controller class");
            for (Method m : restClass.getDeclaredMethods()) {
                String endPoint = "";
                if (m.isAnnotationPresent(GetMapping.class)) {
                    GetMapping annotation = m.getAnnotation(GetMapping.class);
                    endPoint = "_get/rest" + annotation.value();
                } else if (m.isAnnotationPresent(PostMapping.class)) {
                    PostMapping annotation = m.getAnnotation(PostMapping.class);
                    endPoint = "_post/rest" + annotation.value();
                } else if (m.isAnnotationPresent(PutMapping.class)) {
                    PutMapping annotation = m.getAnnotation(PutMapping.class);
                    endPoint = "_put/rest" + annotation.value();
                } else if (m.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping annotation = m.getAnnotation(DeleteMapping.class);
                    endPoint = "_delete/rest" + annotation.value();
                }
                addServiceAnnotation(endPoint, m);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HttpResponse manageRestControllerRequest(HttpRequest httpRequest) {
        String endPoint = "_" + httpRequest.getMethod().toLowerCase() + httpRequest.getUri().getPath();
        Map<String, String> queryParams = httpRequest.getQueryParams();
        Method method = servicesAnnotation.get(endPoint);
        if (method == null) {
            return HttpResponse.sendFailureMessage(404, "A rest controller service not exist for this endpoint.");
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
            if (method.getReturnType().equals(HttpResponse.class)) {
                httpResponse = (HttpResponse) method.invoke(null, args);
            } else {
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
}

package co.edu.eci.arep.webserver;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Annotation;
import java.util.HashMap;

import co.edu.eci.arep.webserver.reflection.annotation.DeleteMapping;
import co.edu.eci.arep.webserver.reflection.annotation.PutMapping;
import co.edu.eci.arep.webserver.reflection.annotation.RequestParam;
import co.edu.eci.arep.webserver.reflection.annotation.GetMapping;
import co.edu.eci.arep.webserver.reflection.annotation.PostMapping;
import co.edu.eci.arep.webserver.reflection.annotation.RestController;

public class MicroServer {

    public static HashMap<String, Method> services = new HashMap<>();
    public static void main(String[] args) {
        loadComponents(args);
        System.out.println(simulateRequests("get_/greeting?name=mateo"));
        System.out.println(simulateRequests("get_/pi"));
        System.out.println(simulateRequests("post_/goodbye"));
        System.out.println(simulateRequests("put_/pi2"));
    }

    private static void loadComponents(String[] args){
        String path = "src/main/java/" + args[0].replace(".", "/");
        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("The directory " + dir.getPath() + " not exist");
            return;
        }
        File[] files = dir.listFiles();
        System.out.println("Endpoints:");
        System.out.println();
        for (File file : files){
            try {
                String classNameBase = file.getPath().replace("\\", ".");
                String className = classNameBase.substring(14, classNameBase.length() - 5);
                System.out.println(className);
                Class<?> class1 = Class.forName(className);
                if (!class1.isAnnotationPresent(RestController.class)) continue;
                for (Method m : class1.getDeclaredMethods()){
                    String endPoint = "";
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        GetMapping annotation = m.getAnnotation(GetMapping.class);
                        endPoint = "get_" + annotation.value();
                    } else if (m.isAnnotationPresent(PostMapping.class)){
                        PostMapping annotation = m.getAnnotation(PostMapping.class);
                        endPoint = "post_" + annotation.value();
                    }
                    else if (m.isAnnotationPresent(PutMapping.class)){
                        PutMapping annotation = m.getAnnotation(PutMapping.class);
                        endPoint = "put_" + annotation.value();
                    }
                    else if (m.isAnnotationPresent(DeleteMapping.class)){
                        DeleteMapping annotation = m.getAnnotation(DeleteMapping.class);
                        endPoint = "delete_" + annotation.value();
                    }
                    System.out.println(endPoint);
                    System.out.println(m.getName());
                    services.put(endPoint, m);

                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
        }
        System.out.println();
    }

    private static String simulateRequests(String endpoint){
        System.out.println();
        String[] variables = endpoint.split("?");
        for(String x : variables){
            System.out.println(x);
        }
        String path = variables[0];
        String query = "";
        if (variables[1].length() > 0){
            //query = variables[1].split("=")[1];
        }
        System.out.println(path);
        Method method = services.get(path);
        try {
            return (String) method.invoke(null, query);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }
}

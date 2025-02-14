package co.edu.eci.arep.webserver.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import co.edu.eci.arep.webserver.http.HttpResponse;
import co.edu.eci.arep.webserver.reflection.annotation.GetMapping;
import co.edu.eci.arep.webserver.reflection.annotation.RequestParam;
import co.edu.eci.arep.webserver.reflection.annotation.RestController;

@RestController
public class GreetingController {

	@GetMapping("/greeting")
	public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola " + name;
	}

    @GetMapping("/pi")
	public static String pi(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola " + Math.PI;
	}

	@GetMapping("/index")
	public static HttpResponse index(){
		HttpResponse httpResponse = new HttpResponse();
		String fileName = "src/main/resources/static/index.html";
		String outputLine = "";
        // Leer el contenido desde el archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLine += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
			httpResponse.setStatusCode(404);
			return httpResponse;
        }
		httpResponse.setStatusCode(200);
		httpResponse.addHeader("Content-type", "text/html");
		httpResponse.setBody(outputLine);
		return httpResponse;
	}

	@GetMapping("/image")
	public static HttpResponse image(){
		String fileName = "src/main/resources/static/images/placeholder.png";
        File file = new File(fileName);
		HttpResponse response = new HttpResponse();
        // Determine the file extension to set the correct Content-Type

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] imageBytes = inputStream.readAllBytes();
            response.setBody(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
			response.setStatusCode(404);
			return response;
		}
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        String contentType = "image/" + extension;
        response.setStatusCode(200);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(file.length()));
        return response;
	}

}
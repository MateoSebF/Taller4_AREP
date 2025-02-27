package co.edu.eci.arep.webserver.controller;

import org.junit.jupiter.api.Test;

import co.edu.eci.arep.webserver.WebServer;
import co.edu.eci.arep.webserver.http.manage.HttpConnectionExample;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.BeforeAll;

public class HttpControllerTest {
    @BeforeAll
    public static void setUp() {
        System.out.println("Iniciando servidor");
        WebServer.getWebServerSingleton(); 
    }

    @Test
    public void testMakeConnectionHTML() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            HttpURLConnection con = httpConnection.makeConnection("GET", "rest/index", "");
            assertEquals(200, con.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetContentTypeHTML() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            HttpURLConnection con = httpConnection.makeConnection("GET", "rest/index", "");
            assertEquals("text/html", con.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetResponseHTML() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            String response = httpConnection.getResponse("GET", "rest/index", "");
            String expected = "";
            String fileName = "src/main/resources/static/" + "index.html";
            // Leer el contenido desde el archivo
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    expected += line + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Remove all the spaces and new lines
            expected = expected.replaceAll("\\s", "");
            response = response.replaceAll("\\s", "");

            assertEquals(expected, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMakeConnectionImage() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            HttpURLConnection con = httpConnection.makeConnection("GET", "rest/image", "");
            assertEquals(200, con.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetContentTypeImage() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            HttpURLConnection con = httpConnection.makeConnection("GET", "rest/image", "");
            assertEquals("image/png", con.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetResponseImage() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            // Request the image from the server
            System.out.println("Requesting image from server...");
            byte[] response = httpConnection.getResponseBytes("GET", "rest/image", "");
            System.out.println(response.length);
            // Read the expected image file
            String fileName = "src/main/resources/static/images/placeholder.png";
            File file = new File(fileName);
            byte[] expected;
            try (FileInputStream inputStream = new FileInputStream(file)) {
                expected = inputStream.readAllBytes();
                assertTrue(file.exists(), "The expected image file should exist.");
                assertTrue(expected.length > 0, "The expected image file should not be empty.");
                assertTrue(response.length > 0, "The response image should not be empty.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } catch (IOException e) {
            fail("Exception occurred during test: " + e.getMessage());
        }
    }

    @Test
    public void testMakeConnectionNotFound() {
        HttpConnectionExample httpConnection = new HttpConnectionExample();
        try {
            HttpURLConnection con = httpConnection.makeConnection("GET", "/notfound", "");
            assertEquals(404, con.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }   
    

}

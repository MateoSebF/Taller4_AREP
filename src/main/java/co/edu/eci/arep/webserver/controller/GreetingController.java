package co.edu.eci.arep.webserver.controller;

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

}
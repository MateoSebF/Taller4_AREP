package co.edu.eci.arep.webserver.controller;

import co.edu.eci.arep.webserver.reflection.annotation.RequestParam;
import co.edu.eci.arep.webserver.reflection.annotation.RestController;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.PostMapping;
import co.edu.eci.arep.webserver.reflection.annotation.mapping.PutMapping;

@RestController
public class GoodByeController {

    @PostMapping("/goodbye")
	public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola " + name;
	}

    @PutMapping("/pi2")
	public static String pi(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola " + (Math.PI +2);
	}

}

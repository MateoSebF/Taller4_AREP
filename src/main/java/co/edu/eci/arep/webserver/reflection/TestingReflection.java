package co.edu.eci.arep.webserver.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import static java.lang.System.out;

public class TestingReflection {

    public static void main(String[] args){
        System.out.println("Hola");
    }

    @SuppressWarnings("rawtypes")
    public static void printMembers(Member[] mbrs, String s) {
        out.format("%s:%n", s);
        for (Member mbr : mbrs) {
            if (mbr instanceof Field)
                out.format(" %s%n", ((Field) mbr).toGenericString());
            else if (mbr instanceof Constructor)
                out.format(" %s%n", ((Constructor) mbr).toGenericString());
            else if (mbr instanceof Method)
                out.format(" %s%n", ((Method) mbr).toGenericString());
        }
        if (mbrs.length == 0)
            out.format(" -- No %s --%n", s);
        out.format("%n");
    }
}

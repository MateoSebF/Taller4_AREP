package co.edu.eci.arep.webserver.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;

public class InvokePrintMembers {
    public static void main(String ... args) {
        try {
            Class<?> class1 = Class.forName(args[0]);
            @SuppressWarnings("rawtypes")
            Class[] argTypes = new Class[] { Member[].class, String.class };

            Method method = class1.getDeclaredMethod("printMembers", argTypes);

            Class<?> class2 = HashMap.class;

            System.out.format("invoking %s.printMemebers()%n", class1.getName());
            method.invoke(null, class2.getDeclaredFields(), "fields");

        } catch (ClassNotFoundException x) {

        } catch (NoSuchMethodException w) {

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}

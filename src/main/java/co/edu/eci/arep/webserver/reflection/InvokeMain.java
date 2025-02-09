package co.edu.eci.arep.webserver.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InvokeMain{

    public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        try{
            Class<?> c = Class.forName(args[0]);
            @SuppressWarnings("rawtypes")
            Class[] argTypes = new Class[] { String[].class };
            Method main = c.getDeclaredMethod("main", argTypes);
            String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
            System.out.format("invoking %s.main()%n", c.getName());
            main.invoke(null, (Object)mainArgs);

        } catch(ClassNotFoundException x){

        } catch(NoSuchMethodException w){

        }
    }
}
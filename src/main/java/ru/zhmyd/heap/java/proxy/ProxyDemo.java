package ru.zhmyd.heap.java.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

interface If {
    default String original(String s) {
        return s;
    }
}

class Original implements If {
    @Override
    public String original(String s) {
        return s;
    }
}

class Handler implements InvocationHandler {
    private If original;

    Handler(If original) {
        this.original = original;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return "BEFORE\n" + method.invoke(original, args) + "\nAFTER";
    }
}

public class ProxyDemo {

    public static void main(String[] args) {
        If original = new Original();

        //use invocation handler instance
        InvocationHandler handlerInstance = new Handler(original);
        If proxyInstance = (If) Proxy.newProxyInstance(
                If.class.getClassLoader(),
                new Class[]{If.class},
                handlerInstance);

        If proxyLambda = (If) Proxy.newProxyInstance(
                If.class.getClassLoader(),
                new Class[]{If.class},
                (proxy, method, handlerArgs) -> invoke(original, method, handlerArgs));

        If proxyDefault = (If) Proxy.newProxyInstance(
                If.class.getClassLoader(),
                new Class[]{If.class},
                (proxy, method, args1) -> MethodHandles
                        .lookup()
                        .findSpecial(If.class, method.getName(), MethodType.methodType(String.class, String.class), If.class)
                        .bindTo(proxy)
                        .invokeWithArguments(args1)
        );


        System.out.println(Proxy.isProxyClass(proxyInstance.getClass()));
        System.out.println(Proxy.isProxyClass(original.getClass()));
        System.out.println(Proxy.isProxyClass(handlerInstance.getClass()));
        System.out.println(Arrays.toString(proxyInstance.getClass().getMethods()));
        System.out.println(proxyInstance.original("Hello"));
        System.out.println(proxyLambda.original("Hello"));
        System.out.println(proxyDefault.original("Hello"));
    }


    private static Object invoke(Object original, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return "BEFORE_LAMBDA\n" + method.invoke(original, args) + "\nAFTER_LAMBDA";
    }

}
package ru.zhmyd.heap.java.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface If {
    void original(String s);
}

class Original implements If {
    @Override
    public void original(String s) {
        System.out.println(s);
    }
}

class Handler implements InvocationHandler {
    private final If original;

    Handler(If original) {
        this.original = original;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("BEFORE");
        method.invoke(original, args);
        System.out.println("AFTER");
        return null;
    }
}

public class ProxyDemo {
    public static void main(String[] args) {
        If original = new Original();
        Handler handler = new Handler(original);
        If proxy = (If) Proxy.newProxyInstance(
                If.class.getClassLoader(),
                new Class[]{If.class},
                handler);

        proxy.original("Hello");
    }
}
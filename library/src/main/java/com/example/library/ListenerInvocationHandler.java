package com.example.library;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ListenerInvocationHandler implements InvocationHandler {

    //需要拦截Activty中的某种方法
    private Object target;

    //拦截键值对
    private HashMap<String, Method> methodMap = new HashMap<>();

    public ListenerInvocationHandler(Object target) {
        this.target = target;
    }

    @Override

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (target != null) {

            String methodName = method.getName();//假如是OnClick

            //重新赋值,并拦截onClick方法
            method = methodMap.get(methodName); //如果找到了，就执行自定义OnClick方法；

            if (method != null) {
                return method.invoke(target, objects);
            }
        }
        return null;
    }

    /**
     * 拦截的添加
     * @param methodName 本应该执行的方法 拦截掉
     * @param method 执行自定义方法
     */
    public void addMethod(String methodName, Method method) {
        methodMap.put(methodName, method);
    }
}

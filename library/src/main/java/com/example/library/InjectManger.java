package com.example.library;

import android.app.Activity;
import android.view.View;

import com.example.library.annotation.ContentView;
import com.example.library.annotation.EventBase;
import com.example.library.annotation.InjectView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectManger {
    public static void inject(Activity activity) {
        //布局注入
        injectLayout(activity);
        
        //控件注入
        injectViews(activity);
        
        //事件注入
        injectEvents(activity);
        
    }

    //布局注入
    private static void injectLayout(Activity activity) {
        //获取类
        Class<? extends Activity> clazz = activity.getClass();

        //获取类的注解
        ContentView contentView = clazz.getAnnotation(ContentView.class);

        if (contentView != null) {
            int layoutId = contentView.value();

            //第一种方法
//            activity.setContentView(layoutId);
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.invoke(activity, layoutId);//setContentView 是void返回类型
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //控件注入
    private static void injectViews(Activity activity) {
        //获取类
        Class<? extends Activity> clazz = activity.getClass();

        //获取类的所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {//不是所有的属性都有注解
                //获取注解的值
                int valueId = injectView.value();

                try {
                    //执行方法： findViewById(R.id.xx)
                    Method method = clazz.getMethod("findViewById", int.class);

                    //第一种方法
//                    Object view = activity.findViewById(valueId)
                    //执行方法得到View对象
                    Object view = method.invoke(activity, valueId);//不是Void返回类型

                    //属性的值赋给控件，在当前Activiy
                    //
                    field.setAccessible(true);
                    //当属性为private，赋值闪退
                    field.set(activity, view);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }



    }
    
    //控件注入
    private static void injectEvents(Activity activity) {
        //获取类
        Class<? extends Activity> clazz = activity.getClass();
        //获取当前所有方法
        Method[] methods = clazz.getDeclaredMethods();

        //遍历
        for (Method method : methods) {

            //获取每个方法的注解，可能存在多个；
            Annotation[] annotations = method.getAnnotations();

            //遍历注解
            for (Annotation annotation : annotations) {
                //获取onClick注解上的注解类型
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                    //事件的3个重要成员
                    String listenerSetter = eventBase.listenerSetter();
                    Class<?> listenerType = eventBase.listenerType();
                    String callBackListener = eventBase.callBackListener();


                    try {
                        //通过annotationType获取onClick注解的Value值，拿到R.id.xxx
                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        int[] viewIds  = (int[]) valueMethod.invoke(annotation);

                        //拦截方法，执行自定义方法
                        ListenerInvocationHandler handler = new ListenerInvocationHandler(activity);
                        handler.addMethod(callBackListener, method);

                        //代理方法完成
                        Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);

                        for (int viewId : viewIds) {
                            //获取当前的view
                            View view = activity.findViewById(viewId);
                            //获取方法
                            Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                            setter.invoke(view,listener);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

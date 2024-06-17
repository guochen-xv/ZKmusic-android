package com.android.music.util;

import android.app.Activity;
import android.view.View;

import com.android.music.annotation.OnClickView;
import com.android.music.annotation.ViewById;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 皮皮虾出击 on 2021/3/2.
 * qq邮箱： 1272447726@qq.com
 * Describe:该类具体实现了如何通过反射，来给注解的变量创建实例
 */
public class ViewBind {
    public static void inJect(Activity activity) {
        findViewById(activity);
    }

    private static void findViewById(Activity activity) {
        //获取Activity的class
        Class<? extends Activity> clazz = activity.getClass();
        //获取该类中的所有声明的属性
        Field[] declaredFields = clazz.getDeclaredFields();
        //遍历所有属性，找到用@ViewById注解了的属性
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            //获取属性上的注解对象
//            @ViewById(R.id.textView) R.id.textView--value
//            TextView textView;//属性
            ViewById annotation = field.getAnnotation(ViewById.class);
            if (annotation != null) {
                int viewId = annotation.value();
                View view = activity.findViewById(viewId);
                try {
                    //私有属性也可以动态注入（不写该句代码，private声明的属性会报异常）
                    field.setAccessible(true);
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 处理OnClick注解 代优化
     */
    private static void onClick(final Activity activity) {
        // findViewById  setOnClick
        // 1.获取该Activity的所有方法
        Class<?> clazz = activity.getClass();

//        Method[] methods = clazz.getDeclaredMethods();


        // 2.遍历方法获取所有的值
//        for (final Method method:methods){
//            // 2.1 获取OnClick注解
//            OnClickView onClick = method.getAnnotation(OnClickView.class);
        try {
            final Method method = clazz.getMethod("onClick", View.class);
            OnClickView onClick = method.getAnnotation(OnClickView.class);
            // 2.2 该方法上是否有OnClick注解
            if (onClick != null) {
                // 2.3 获取OnClick里面所有的值
                int[] viewIds = onClick.value();// @OnClick({R.id.text_view,R.id.button})


                // 2.4 先findViewById , setOnclick
                for (int viewId : viewIds) {
                    // 先findViewById
                    final View view = activity.findViewById(viewId);
                    // 后设置setOnclick
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 首先需要判断 方法是否需要检测网络
                            // 3.反射调用原来配置了OnClick的方法
                            method.setAccessible(true);// 私有的方法
                            try {
                                method.invoke(activity);// 调用无参的方法
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    method.invoke(activity, view);// 调用有参的方法 view 代表当前点击的View
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                }
//            }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

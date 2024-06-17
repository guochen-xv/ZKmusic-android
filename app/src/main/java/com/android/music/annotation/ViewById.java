package com.android.music.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe: 用于bind各种视图的view
 */
@Retention(RetentionPolicy.RUNTIME)//CLASS 编译时注解  RUNTIME运行时注解 SOURCE 源码注解
@Target(ElementType.FIELD)//注解作用范围:FIELD 属性  METHOD方法  TYPE 放在类上
public @interface ViewById {
    int value(); //表示@ViewById() 注解时，括号里面的编写的为int类型的值
}

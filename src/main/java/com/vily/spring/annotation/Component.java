package com.vily.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Author:vily
 * Date: 2022/11/22
 */


/**
 * interface:  接口可以理解成一种特殊的类，在这个类中只能有常量和抽象方法；接口不可以实例化，接口中的方法没有方法体，继承接口的类必须实现接口中定义的方法。
 * @interface: 一个继承了java.lang.annotation.Annotation接口的自定义注解，定义注释类型。
 *
 *
 * Retention: 生命周期
 * RetentionPolicy.SOURCE： 注解只在源代码中存在，编译成class之后，就没了。@Override 就是这种注解。
 * RetentionPolicy.CLASS： 注解在java文件编程成.class文件后，依然存在，但是运行起来后就没了。@Retention的默认值，即当没有显式指定@Retention的时候，就会是这种类型。
 * RetentionPolicy.RUNTIME： 注解在运行起来之后依然存在，程序可以通过反射获取这些信息
 *
 *
 * Target: 目标可以放在什么位置上
 * ElementType.TYPE：能修饰类、接口或枚举类型
 * ElementType.FIELD：能修饰成员变量
 * ElementType.METHOD：能修饰方法
 * ElementType.PARAMETER：能修饰参数
 * ElementType.CONSTRUCTOR：能修饰构造器
 * ElementType.LOCAL_VARIABLE：能修饰局部变量
 * ElementType.ANNOTATION_TYPE：能修饰注解
 * ElementType.PACKAGE：能修饰包
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    String value() default "";
}

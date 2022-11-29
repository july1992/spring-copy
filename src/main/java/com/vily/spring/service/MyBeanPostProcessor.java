package com.vily.spring.service;

import com.vily.spring.annotation.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;


/**
 * Description:
 * Author:vily
 * Date: 2022/11/28
 */

@Component("myBeanPostProcessor")
public class MyBeanPostProcessor implements BeanPostProcessor{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("MyBeanPostProcessor before. bean = " + beanName + ",beanName = " + beanName);
        if (bean instanceof UserService) {
            ((UserService)bean).setAge(14);
        }
        if(bean instanceof OrderService){
            ((OrderService)bean).setOrder("hahahahahah");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("MyBeanPostProcessor after. bean = " + beanName + ",beanName = " + beanName);

        if(Objects.equals(beanName,"userService")){
            System.out.println("---代理逻辑1");
            Object proxyInstance = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(), bean.getClass().getClasses(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    System.out.println("---代理逻辑2");

                    return method.invoke(bean, args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}

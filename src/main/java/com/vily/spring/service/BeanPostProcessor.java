package com.vily.spring.service;

/**
 * Description:Bean对象的后置处理器，这里主要负责AOP
 * Author:vily
 * Date: 2022/11/28
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);

}

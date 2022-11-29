package com.vily;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vily.spring.annotation.Autowired;
import com.vily.spring.annotation.Component;
import com.vily.spring.annotation.ComponentScan;
import com.vily.spring.annotation.Scope;
import com.vily.spring.bean.BeanDefinition;
import com.vily.spring.config.AppConfig;
import com.vily.spring.service.BeanPostProcessor;
import com.vily.spring.service.InitializingBean;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:  仿写一个spring容器
 * Author:vily
 * Date: 2022/11/22
 */
public class CustomApplicationContext {

    private Class configClass;

    private Map<String, BeanDefinition> beanDefinitionMap= Maps.newHashMap();

    private Map<String,Object> singletonObjects=Maps.newHashMap();

    /**
     * 后置处理器容器
     */
    private List<BeanPostProcessor> beanPostProcessorList = Lists.newArrayList();

    public CustomApplicationContext(Class configClass ) {
        this.configClass=configClass;

        // 解析配置累
        // ComponentScan注解-----》扫描路径-----》扫描
        san(configClass);
        //单例对象，优先初始化
        initSingletonObjects();

    }

    private void san(Class configClass) {
        if (!configClass.isAnnotationPresent(ComponentScan.class)) {
            System.out.println("此配置文件，缺少ComponentScan注解");
            return;
        }
        ComponentScan componentScan=(ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String value = componentScan.value();
        System.out.println("path:"+value);

        ClassLoader classLoader = CustomApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource("com/vily/spring/service");
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f:files) {
                System.out.println(f);
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    System.out.println("扫描的文件 类路径：" + className);
                    className = className.replace("/", ".");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            // 表示当前这个类是一个Bean

                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.newInstance();
                                beanPostProcessorList.add(beanPostProcessor);
                                System.out.println("扫描的文件+++++++++++++++++++++++++类路径：BeanPostProcessor" );
                            }

                            System.out.println("当前类 包含Component注解： className = " + className);
                            Component component = clazz.getAnnotation(Component.class);
                            System.out.println("注解beanName： " + component.value());
                            if (StringUtils.isBlank(component.value())) {
                                System.out.println("注解beanName是空的："+component.value());
                            }
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scope = clazz.getAnnotation(Scope.class);
                                beanDefinition.setScope(scope.value());
                            }else{
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(component.value(),beanDefinition);
                            System.out.println("向对象池，进行注入新对象. beanName: " + component.value());
                            System.out.println("集合："+ JSON.toJSONString(beanDefinitionMap));

                        }else{
                            System.out.println("当前类 不包含Component注解： className = " + className);
                        }

                    } catch (ClassNotFoundException e) {
                        System.out.println("解析类失败1 className = " + className);
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void initSingletonObjects() {

        if(beanDefinitionMap.size()==0){
            System.out.println("对象为空");
            return;
        }
        beanDefinitionMap.forEach((k,v)-> {

            if (!Objects.equals(v.getScope(),"singleton")) {
                return;
            }

            Object bean = createBean(k, v);
            singletonObjects.put(k,bean);
        });
    }


    public Object getBean(String beanName){

        try {

            if (!beanDefinitionMap.containsKey(beanName)) {
                throw new Exception("类名：" + beanName + " 有误，获取实例失败。beanDefinitionMap = " + JSON.toJSONString(beanDefinitionMap));

            }
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(Objects.equals(beanDefinition.getScope(),"singleton")){
                // 单例
                System.out.println(beanName+"开始获取单例");
                return singletonObjects.get(beanName);
            }else{
                // 多例
                System.out.println(beanName + " 开始获取多例");
                return createBean(beanName, beanDefinition);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {

        Class clazz = beanDefinition.getClazz();
        Object instance=null;

        try {
            instance=clazz.newInstance();

            // 依赖注入
            for (Field field:clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    System.out.println("-----field.getName():"+field.getName());
                    Object bean = getBean(field.getName());
                    Autowired autowired = field.getAnnotation(Autowired.class);

                    if (bean==null && autowired.required()) {
                        System.out.println("依赖注入失败！！！ 名称： " + field.getName());
                    }else{
                        System.out.println("依赖注入成功！！！ 名称： " + field.getName());
                        //访问控制检查
                        field.setAccessible(true);
                        // 赋值，给instance这个对象单field属性赋值
                        field.set(instance,bean);

                    }
                }
            }

            //AOP-初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 设置配置完成，初始化一些内部对象
            if (instance instanceof InitializingBean) {
                ((InitializingBean)instance).afterPropertiesSet();
            }

            // AOP-初始化之后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }


        }catch (Exception e){
            e.printStackTrace();
        }


        return instance;
    }
}

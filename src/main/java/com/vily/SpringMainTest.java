package com.vily;

import com.vily.spring.config.AppConfig;
import com.vily.spring.service.UserService;

/**
 * Description:
 * Author:vily
 * Date: 2022/11/22
 */
public class SpringMainTest {

    public static void main(String[] args) {
        CustomApplicationContext customApplicationContext=new CustomApplicationContext(AppConfig.class);

        UserService userService1 = (UserService) customApplicationContext.getBean("userService");
//        UserService userService2 = (UserService) customApplicationContext.getBean("userService");
//        UserService userService3 = (UserService) customApplicationContext.getBean("userService");
        System.out.println(userService1);
//        System.out.println(userService2);
//        System.out.println(userService3);

        userService1.test();
    }

}

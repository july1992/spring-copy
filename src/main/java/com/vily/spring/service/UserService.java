package com.vily.spring.service;

import com.vily.spring.annotation.Autowired;
import com.vily.spring.annotation.Component;
import com.vily.spring.annotation.Scope;

/**
 * Description:
 * Author:vily
 * Date: 2022/11/22
 */
@Component("userService")
//@Scope("prototype")
@Scope("singleton")
public class UserService  implements InitializingBean{

    @Autowired
    private OrderService orderService;


    private String name;

    private int age;

    public void test() {

        System.out.println("userService-----1:"+orderService);
        System.out.println("userService-----4:"+orderService.getOrder());
        System.out.println("userService-----2:"+name);
        System.out.println("userService-----3:"+age);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        name="我树谁。。。。。";
    }

    public void setAge(int aa){
        age=aa;
    }
}

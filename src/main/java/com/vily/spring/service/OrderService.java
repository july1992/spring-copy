package com.vily.spring.service;

import com.vily.spring.annotation.Component;
import com.vily.spring.annotation.Scope;

/**
 * Description:
 * Author:vily
 * Date: 2022/11/22
 */
@Component("orderService")
//@Scope("prototype")
@Scope("singleton")
public class OrderService {

    private String order;

    public void setOrder(String or){
       this.order=or;
    }

    public String getOrder(){
        return order;
    }



}

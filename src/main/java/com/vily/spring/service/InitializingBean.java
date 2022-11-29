package com.vily.spring.service;

/**
 * Description:
 * Author:vily
 * Date: 2022/11/28
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}

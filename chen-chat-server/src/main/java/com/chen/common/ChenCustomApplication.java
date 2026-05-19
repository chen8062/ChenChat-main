package com.chen.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author zhongzb
 * @date 2021/05/27
 */
@SpringBootApplication(scanBasePackages = {"com.chen"})
@MapperScan({"com.chen.common.**.mapper"})
@ServletComponentScan
public class ChenCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChenCustomApplication.class,args);
    }

}
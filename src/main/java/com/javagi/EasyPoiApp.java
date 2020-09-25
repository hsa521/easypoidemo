package com.javagi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 * @Author kuiwang
 * @Date 2019/7/1 15:54
 * @Version 1.0
 */
@SpringBootApplication
@AutoConfigureAfter
@MapperScan("com.javagi.mapper")
public class EasyPoiApp {
    public static void main(String[] args) {
        SpringApplication.run(EasyPoiApp.class, args);
    }
}

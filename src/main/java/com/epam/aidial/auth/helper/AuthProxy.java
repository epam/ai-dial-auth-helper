package com.epam.aidial.auth.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.validation.constraints.NotNull;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@ComponentScan({"com.epam.aidial.auth.helper"})
public class AuthProxy {

    public static void main(String[] args) {
        SpringApplication.run(AuthProxy.class, args);
    }

    @Value("${scheduled.poolSize:2}")
    @NotNull
    private Integer poolSize;

    @SuppressWarnings("unused")
    @Bean
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler ret = new ThreadPoolTaskScheduler();
        ret.setPoolSize(poolSize);
        return ret;
    }
}
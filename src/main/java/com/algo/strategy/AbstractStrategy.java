package com.algo.strategy;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public abstract class AbstractStrategy {

    public static void init(Class<? extends Strategy> clazz) {
        SpringApplication application = new SpringApplication(clazz);
        ApplicationContext context = application.run();
        Strategy strategy = context.getBean(clazz);
        strategy.run();
    }
}

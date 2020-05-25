package org.archer.rpc.processor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;


@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext springContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        springContext = applicationContext;
    }

    public static ApplicationContext getSpringContext(){
        return springContext;
    }
}

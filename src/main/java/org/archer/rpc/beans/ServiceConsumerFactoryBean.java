package org.archer.rpc.beans;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.util.Objects;

@Deprecated
public class ServiceConsumerFactoryBean extends AbstractFactoryBean {

    private final String serviceClassName;

    private Class<?> serviceClass;


    public ServiceConsumerFactoryBean(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }


    @Override
    public Class<?> getObjectType() {
        try {
            if (Objects.isNull(this.serviceClass)) {
                this.serviceClass = Class.forName(serviceClassName);
            }
            return this.serviceClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    @Override
    protected Object createInstance() throws Exception {
        System.out.println(serviceClass.getName());
        return null;
    }
}

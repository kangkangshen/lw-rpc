package org.archer.rpc.processor;

import org.apache.commons.lang3.StringUtils;
import org.archer.rpc.InterfaceDiscovery;
import org.archer.rpc.ServiceFactory;
import org.archer.rpc.constants.Delimiters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;

@Component
public class DefaultServiceFactory implements ServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

    @Autowired
    private InterfaceDiscovery interfaceDiscovery;

    @Autowired
    private RestTemplate restTemplate;


    @SuppressWarnings("unchecked")
    @Override
    public <T> T prepareService(Class<T> interfaceClazz, String version) {
        Assert.notNull(interfaceClazz, "interface cannot null");
        Assert.isTrue(StringUtils.isNotBlank(version), "version must specify");
        HttpRpcInvocationProxyHandler proxyHandler = new HttpRpcInvocationProxyHandler(interfaceClazz.getName(), version, interfaceDiscovery, restTemplate);
        T proxyInstance = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{interfaceClazz}, proxyHandler);
        logger.debug(interfaceClazz.getName() + Delimiters.COLON + version + " proxy instance create successfully");
        return proxyInstance;

    }
}

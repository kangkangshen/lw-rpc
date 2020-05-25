package org.archer.rpc.processor;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.archer.rpc.InterfaceDiscovery;
import org.archer.rpc.constants.Protocols;
import org.archer.rpc.meta.InterfaceMetaData;
import org.archer.rpc.meta.InvocationMetaData;
import org.archer.rpc.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Objects;

public class HttpRpcInvocationProxyHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(InvocationHandler.class);

    private final InterfaceDiscovery interfaceDiscovery;

    private final ParameterNameDiscoverer parameterNameDiscoverer;

    private final String serviceName;

    private final String version;

    private final Class<?> service;

    private final String toString;

    private final int hashcode;

    private final RestTemplate restTemplate;

    @SneakyThrows
    public HttpRpcInvocationProxyHandler(String serviceName, String version, InterfaceDiscovery interfaceDiscovery, RestTemplate restTemplate) {
        Assert.isTrue(StringUtils.isNotBlank(serviceName), "service name has'not set");
        Assert.isTrue(StringUtils.isNotBlank(version), "service version has'not set");
        this.serviceName = serviceName;
        this.version = version;
        this.service = Class.forName(serviceName);
        Assert.isTrue(service.isInterface(), "class " + service.getName() + "not a interface");
        this.toString = serviceName + version;
        this.hashcode = toString.hashCode();
        this.interfaceDiscovery = interfaceDiscovery;
        this.restTemplate = restTemplate;
        this.parameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //0.调用拦截
        if (shouldRpc(method)) {
            logger.debug("method " + method.getName() + " in " + serviceName + " is intercepted cause rpc");
        } else {
            //Object的方法
            logger.debug("method " + method.getName() + " in " + serviceName + " is filtered cause local invocation");
            //模拟Object的hashcode,toString,equals方法调用
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return this.hashcode;
            } else if (ReflectionUtils.isToStringMethod(method)) {
                return this.toString;
            } else if (ReflectionUtils.isEqualsMethod(method)) {
                return Objects.equals(hashcode, args[0]);
            }
        }

        //1.选择调用实例
        InterfaceMetaData interfaceMetaData = Objects.requireNonNull(interfaceDiscovery.choose(service, version), "no service impl can be invoked");
        logger.debug("interface " + interfaceMetaData.getInterfaceName() + " version" + interfaceMetaData.getInterfaceVersion() + " accessed,the service impl is " + interfaceMetaData.getInterfaceImplClazz());

        //2.构造调用信息
        InvocationMetaData invocation = new InvocationMetaData();
        invocation.setInterfaceName(this.serviceName);
        invocation.setMethodName(method.getName());
        invocation.setParamNames(parameterNameDiscoverer.getParameterNames(method));
        invocation.setParams(SerializeUtils.serialize(args));

        //3.发起调用
        String httpUrl = MessageFormat.format(
                Protocols.HTTP_URL_PATTERN,
                interfaceMetaData.getHost(),
                Integer.toString(interfaceMetaData.getPort()),
                this.serviceName/*interfaceName*/,
                this.version/*version*/);
        Object result = restTemplate.postForObject(httpUrl, invocation, method.getReturnType());
        logger.debug("rpc invoke success,result is " + JSON.toJSONString(result));
        return result;
        //4.处理返回结果
    }


    private boolean shouldRpc(Method method) {
        Method[] methods = this.service.getDeclaredMethods();
        for (Method interfaceMethod : methods) {
            if (Objects.equals(method, interfaceMethod)) {
                return true;
            }
        }
        return false;
    }


}


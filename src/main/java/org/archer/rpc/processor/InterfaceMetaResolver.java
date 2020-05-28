package org.archer.rpc.processor;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.archer.rpc.annotations.ServiceProvider;
import org.archer.rpc.constants.Delimiters;
import org.archer.rpc.constants.PropertyKeys;
import org.archer.rpc.constants.Protocols;
import org.archer.rpc.meta.InterfaceMetaData;
import org.archer.rpc.meta.MethodMetaData;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

/**
 * 接口元信息解析器
 */
@Component
public class InterfaceMetaResolver implements BeanPostProcessor {

    private final ParameterNameDiscoverer parameterNameDiscoverer =
            new DefaultParameterNameDiscoverer();
    @Autowired
    private Environment environment;
    @Autowired
    private InterfaceMetaRegister register;

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization
            (Object bean, String beanName) throws BeansException {
        ServiceProvider serviceProvider = AnnotationUtils.
                findAnnotation(bean.getClass(), ServiceProvider.class);
        if (Objects.nonNull(serviceProvider)) {
            InterfaceMetaData interfaceMetaData = new InterfaceMetaData();
            interfaceMetaData.setHost(
                    environment.getProperty(PropertyKeys.SERVICE_PROVIDER_HOST,
                            InetAddress.getLocalHost().getHostAddress()));
            interfaceMetaData.setPort
                    (Integer.parseInt(environment.getRequiredProperty(PropertyKeys.SERVICE_PROVIDER_PORT)));
            Class<?> interfaceClazz = serviceProvider.value();
            if (!interfaceClazz.isInterface()) {
                throw new RuntimeException(interfaceClazz.getName() + "is not a interface");
            }
            interfaceMetaData.setInterfaceClazz(serviceProvider.value());
            interfaceMetaData.setInterfaceImplClazz(bean.getClass().getName());
            interfaceMetaData.setImplBeanName(beanName);
            interfaceMetaData.setInterfaceName(serviceProvider.value().getName());
            interfaceMetaData.setInterfaceVersion(
                    polishVersion(serviceProvider.version()));
            interfaceMetaData.setProtocol(Protocols.DEFAULT_PROTOCOL);
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(interfaceClazz);
            List<MethodMetaData> methodMetaData = Lists.newArrayList();
            for (Method method : methods) {
                MethodMetaData metaData = resolveMethodMetaData(method);
                methodMetaData.add(metaData);
            }
            interfaceMetaData.setMethodMetaData(methodMetaData);
            register.registerInstance(
                    interfaceMetaData.getInterfaceName() +
                            Delimiters.COLON +
                            interfaceMetaData.getInterfaceVersion(),
                    interfaceMetaData);
            return bean;
        } else {
            return bean;
        }
    }

    public String polishVersion(String version) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 1) {
            return version + Delimiters.PERIOD + activeProfiles[0];
        } else {
            //cannot determine the environment
            return version;
        }
    }

    private MethodMetaData resolveMethodMetaData(Method method) {
        MethodMetaData methodMetaData = new MethodMetaData();
        methodMetaData.setMethodName(method.getName());
        Class<?>[] paramTypes = method.getParameterTypes();
        String[] paramTypeNames = new String[method.getParameterCount()];
        String[] paramNames =
                parameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypeNames[i] = paramTypes[i].getName();
        }
        methodMetaData.setParamTypes(paramTypeNames);
        methodMetaData.setParamNames(paramNames);
        return methodMetaData;
    }
}

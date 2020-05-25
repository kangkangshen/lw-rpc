package org.archer.rpc.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.archer.rpc.constants.Delimiters;
import org.archer.rpc.meta.ExecutionException;
import org.archer.rpc.meta.InvocationMetaData;
import org.archer.rpc.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@RestController("rpcInvoker")
public class GlobalRpcInvoker {

    private static final Logger logger = LoggerFactory.getLogger(GlobalRpcInvoker.class);

    @Autowired
    private ApplicationContext springContext;


    @RequestMapping("/rpc/{interfaceName}/{version}")
    public Object rpcInvoke(@PathVariable String interfaceName,
                            @PathVariable String version,
                            @Nonnull @RequestBody InvocationMetaData invocationMetaData)
    {
        logger.debug(interfaceName + Delimiters.COLON + version + "accessed");
        try {
            Object bean;
            Class<?> beanClass = Class.forName(invocationMetaData.getInterfaceName());
            if (StringUtils.isNotBlank(invocationMetaData.getBeanName())) {
                bean = springContext.getBean(invocationMetaData.getBeanName());
            } else {
                bean = springContext.getBean(beanClass);
            }
            String targetMethodName = invocationMetaData.getMethodName();
            Method targetMethod = ReflectionUtils.getUniqueDeclaredMethods(
                    beanClass,
                    method -> Objects.equals(method.getName(), targetMethodName))[0];
            if (Objects.isNull(targetMethod)) {
                throw new NoSuchMethodException("There is not any method named " + targetMethodName+
                                                " matched in " + invocationMetaData.getInterfaceName());
            }

            Object result = ReflectionUtils.invokeMethod(targetMethod,
                                                        bean,
                                                        SerializeUtils.toArray(invocationMetaData.getParams()));
            logger.debug(interfaceName +
                    Delimiters.COLON +
                    version +
                    Delimiters.APOSTROPHE +
                    invocationMetaData.getMethodName() +
                    Arrays.toString(invocationMetaData.getParamNames()) + Delimiters.WHITESPACE + "invoke success");
            return result;
        } catch (Exception e) {
            logger.debug(interfaceName +
                    Delimiters.COLON +
                    version +
                    Delimiters.APOSTROPHE +
                    Objects.requireNonNull(invocationMetaData).getMethodName() +
                    Arrays.toString(invocationMetaData.getParamNames()) +
                    Delimiters.WHITESPACE +
                    "invoke failed");
            e.printStackTrace();
            ExecutionException executionException = new ExecutionException();
            executionException.setExceptionClass(e.getClass().getName());
            executionException.setExceptionStack(ExceptionUtils.getStackTrace(e));
            return executionException;
        }
    }
}

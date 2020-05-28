package org.archer.rpc.meta;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InterfaceMetaData implements Serializable {
    private Class<?> interfaceClazz;
    private String interfaceName;
    private String interfaceImplClazz;
    private String implBeanName;
    private String interfaceVersion;
    private List<MethodMetaData> methodMetaData;
    private String host;
    private int port;
    private String protocol;
}

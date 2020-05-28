package org.archer.rpc.meta;

import lombok.Data;

import java.io.Serializable;

@Data
public class MethodMetaData implements Serializable {
    private String methodName;
    private String[] paramTypes;
    private String[] paramNames;
}

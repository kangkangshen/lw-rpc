package org.archer.rpc.meta;


import lombok.Data;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class InvocationMetaData implements Serializable {
    @Nullable
    private String beanName;
    @NotNull
    private String interfaceName;
    @NotNull
    private String methodName;
    @NotNull
    private String[] paramNames;
    @NotNull
    private byte[] params;
}

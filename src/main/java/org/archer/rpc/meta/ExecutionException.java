package org.archer.rpc.meta;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExecutionException implements Serializable {
    private String exceptionClass;
    private String exceptionStack;

}

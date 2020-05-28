package org.archer.rpc.utils;


import lombok.Data;

@Data
public class Result<T> {
    private Integer exceptionCode;
    private String exceptionMessage;
    private String exceptionStack;
    private boolean success;
    private T data;
}

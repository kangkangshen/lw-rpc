package org.archer.rpc.utils;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public final class HttpUtils {

    private HttpUtils() {
    }


    @SuppressWarnings("unchecked")
    public static <T> Result<T> postSync(String url, Object params) {
        RestTemplate client = new RestTemplate();
        return client.postForObject(url, params, Result.class);
    }
}
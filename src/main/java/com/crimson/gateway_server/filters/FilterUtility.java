package com.crimson.gateway_server.filters;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpHeaders;

import java.util.Objects;
import java.util.UUID;

@Component
public class FilterUtility {
    public String getCorrelationId(HttpHeaders requestHeaders){
        if(requestHeaders.get("correlation-id") !=null){
            return requestHeaders.get("correlation-id").stream().findFirst().get();
        }
        return null;
    }
    public boolean isCorrelationIdPresent(HttpHeaders requestHeaders){
        return Objects.nonNull(getCorrelationId(requestHeaders));
    }
    public String generateCorrelationId(){
        return UUID.randomUUID().toString();
    }
}

package com.crimson.gateway_server.mapper;

import com.crimson.gateway_server.filters.ResponseTraceFilter;
import com.crimson.gateway_server.model.ApiRestCall;
import com.crimson.gateway_server.repository.ApiRestCallRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class ApiRestCallMapper {

    private static final Logger logger = LoggerFactory.getLogger(ApiRestCallMapper.class);

    public ApiRestCall mapRequest(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();

        ApiRestCall apiRestCall = new ApiRestCall();

        String[] pathArray = request.getPath().toString().split("/");

        for(int i =0;i< pathArray.length;i++){
            if(pathArray[i].equals("project-red")){
                apiRestCall.setApplication(pathArray[i+1]);
                apiRestCall.setApiName(pathArray[pathArray.length-1]);
                break;
            } 
        }
        apiRestCall.setHeaders(request.getHeaders().toString());
        apiRestCall.setMethod(request.getMethod().toString());
        apiRestCall.setParameters(request.getQueryParams().toString());
        apiRestCall.setRequestTime(LocalDateTime.now());
        apiRestCall.setEndPoint(request.getPath().toString());

        logger.info("Rest API request info: {} {} {} ",LocalDateTime.now(), request.getMethod(), request.getPath());

        return apiRestCall;
    }

    public ApiRestCall mapResponse(ApiRestCall apiRestCall, ServerWebExchange exchange) {
        apiRestCall.setResponseTime(LocalDateTime.now());
        if(Objects.nonNull(exchange.getResponse().getStatusCode())){
            apiRestCall.setStatus(exchange.getResponse().getStatusCode().toString());
        }
        return apiRestCall;
    }
}

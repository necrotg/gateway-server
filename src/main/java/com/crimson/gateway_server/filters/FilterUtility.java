package com.crimson.gateway_server.filters;

import com.crimson.gateway_server.mapper.ApiRestCallMapper;
import com.crimson.gateway_server.model.ApiRestCall;
import com.crimson.gateway_server.repository.ApiRestCallRepository;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FilterUtility {

    private final ApiRestCallMapper apiRestCallMapper;
    private final ApiRestCallRepository apiRestCallRepository;

    private static final Logger logger = LoggerFactory.getLogger(FilterUtility.class);

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

    public void persistApiRequestCall(ServerWebExchange exchange) {
        ApiRestCall apiRestCall = apiRestCallMapper.mapRequest(exchange);
        apiRestCallRepository.save(apiRestCall);
        exchange.getAttributes().put("api-call-id", apiRestCall.getApiCallId());
    }

    public void setCorrelationId(ServerWebExchange exchange){
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if(isCorrelationIdPresent(requestHeaders)){
            logger.debug("RequestTraceFilter::filter: Correlation ID {} is present", getCorrelationId(requestHeaders));
        }else{
            exchange.mutate().request(exchange.getRequest().mutate().header("correlation-id",generateCorrelationId()).build()).build();
            logger.debug("RequestTraceFilter::filter: setting Correlation ID {}", getCorrelationId(requestHeaders));
        }
    }

    public void setResponseCorrelationId(ServerWebExchange exchange) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        String correlationId = getCorrelationId(requestHeaders);
        logger.debug("ResponseTraceFilter::postGlobalFilter: Setting Correlation ID {} in response header", correlationId);
        exchange.getResponse().getHeaders().add("correlation-id",correlationId);
    }

    public void persistApiResponseInfo(ServerWebExchange exchange) {
        ApiRestCall apiRestCall = apiRestCallRepository
                .findById((UUID) exchange.getAttributes().get("api-call-id"))
                .orElseThrow(()->new InternalServerErrorException("API call not found"));

        apiRestCall = apiRestCallMapper.mapResponse(apiRestCall,exchange);
        apiRestCallRepository.save(apiRestCall);
    }
}

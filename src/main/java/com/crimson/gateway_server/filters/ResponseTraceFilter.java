package com.crimson.gateway_server.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResponseTraceFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    @Bean
    public GlobalFilter postGlobalFilter(){
        return (exchange,chain) ->{
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = filterUtility.getCorrelationId(requestHeaders);
                logger.debug("ResponseTraceFilter::postGlobalFilter: Setting Correlation ID {} in response header", filterUtility.getCorrelationId(requestHeaders));
                exchange.getResponse().getHeaders().add("correlation-id",correlationId);
            }));
        };
    }
}

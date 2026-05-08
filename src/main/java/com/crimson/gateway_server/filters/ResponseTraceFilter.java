package com.crimson.gateway_server.filters;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ResponseTraceFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

    private final FilterUtility filterUtility;

    @Bean
    public GlobalFilter postGlobalFilter(){
        return (exchange,chain) -> chain.filter(exchange).then(Mono.fromRunnable(()->{
            filterUtility.setResponseCorrelationId(exchange);
            filterUtility.persistApiResponseInfo(exchange);
        }));
    }
}

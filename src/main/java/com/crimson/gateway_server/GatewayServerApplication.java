package com.crimson.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableJpaAuditing
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

	@Bean
	public RouteLocator customProjectRouteConfig(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes().route(path -> path
						.path("/project-red/ordering/**")
						.filters(filter -> filter.rewritePath("/project-red/ordering/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
										.setKeyResolver(userKeyResolver()))
								.retry(retryConfig -> retryConfig.setRetries(3)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100),Duration.ofMillis(100),2,true))
								.circuitBreaker(config -> config.setName("orderingCircuitBreaker")
										.setFallbackUri("forward:/contactSupport")))
						.uri("lb://ORDERING")).build();

	}
	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(1,1,1);
	}
	@Bean
	public KeyResolver userKeyResolver(){
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}

}

package com.crimson.gateway_server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
    @RequestMapping("/contactSupport")
    public Mono<String> fallbackContact(){
        return Mono.just("System unavailable, please try again in a few minutes. If issue persists contact system admin.");
    }

}

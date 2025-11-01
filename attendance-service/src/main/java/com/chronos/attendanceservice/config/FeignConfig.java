package com.chronos.attendanceservice.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@Configuration
public class FeignConfig {
    private static final List<String> FORWARDED_HEADERS = List.of(
            "Authorization",
            "X-User-Email",
            "X-User-Role",
            "X-User-UUID",
            "X-User-EmployeeId"
    );

    @Bean
    public RequestInterceptor requestInterceptor(){
        return template -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            System.out.println("ATTRS: " + attrs);
            if (attrs == null) {
                return;
            }
            HttpServletRequest req = (HttpServletRequest) attrs.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            if(req == null) return;

            FORWARDED_HEADERS.forEach(h -> {
                String v = req.getHeader(h);
                if(v != null) {
                    template.header(h ,v);
                }
            });
        };
    }
}

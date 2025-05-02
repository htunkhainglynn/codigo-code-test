package com.codigo.code.test.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@ConfigurationProperties(prefix = "security")
@Component
public class SecurityProperties {
    private List<String> unauthorizedUrls;

    public void setUnauthorizedUrls(List<String> unauthorizedUrls) {
        this.unauthorizedUrls = unauthorizedUrls;
    }
}

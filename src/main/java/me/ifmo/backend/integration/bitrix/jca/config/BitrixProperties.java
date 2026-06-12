package me.ifmo.backend.integration.bitrix.jca.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data

@Component
@ConfigurationProperties(prefix = "bitrix")
public class BitrixProperties {
    private String webhookUrl;
}

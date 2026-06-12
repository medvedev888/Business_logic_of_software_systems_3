package me.ifmo.backend.integration.bitrix.jca.config;

import jakarta.resource.ResourceException;
import me.ifmo.backend.integration.bitrix.jca.BitrixConnectionFactory;
import me.ifmo.backend.integration.bitrix.jca.BitrixManagedConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BitrixJcaConfig {
    @Bean
    public BitrixConnectionFactory bitrixConnectionFactory(
            BitrixManagedConnectionFactory managedConnectionFactory
    ) throws ResourceException {
        return (BitrixConnectionFactory) managedConnectionFactory.createConnectionFactory();
    }
}
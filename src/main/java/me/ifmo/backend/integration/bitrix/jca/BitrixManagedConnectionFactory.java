package me.ifmo.backend.integration.bitrix.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.integration.bitrix.jca.config.BitrixProperties;
import me.ifmo.backend.integration.bitrix.jca.impl.BitrixConnectionFactoryImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Set;


@RequiredArgsConstructor

@Component
public class BitrixManagedConnectionFactory implements ManagedConnectionFactory {
    private final BitrixProperties bitrixProperties;
    private final RestTemplate restTemplate;
    private PrintWriter logWriter;

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        return new BitrixConnectionFactoryImpl(this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new BitrixConnectionFactoryImpl(this, new BitrixConnectionManager());
    }

    @Override
    public ManagedConnection createManagedConnection(
            Subject subject,
            ConnectionRequestInfo cxRequestInfo
    ) throws ResourceException {
        return new BitrixManagedConnection(
                bitrixProperties.getWebhookUrl(),
                restTemplate
        );
    }

    @Override
    public ManagedConnection matchManagedConnections(
            Set connectionSet,
            Subject subject,
            ConnectionRequestInfo cxRequestInfo
    ) throws ResourceException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BitrixManagedConnectionFactory;
    }

    @Override
    public int hashCode() {
        return BitrixManagedConnectionFactory.class.hashCode();
    }

}
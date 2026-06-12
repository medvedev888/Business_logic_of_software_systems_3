package me.ifmo.backend.integration.bitrix.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnectionFactory;


public class BitrixConnectionManager implements ConnectionManager {
    @Override
    public Object allocateConnection(
            ManagedConnectionFactory managedConnectionFactory,
            ConnectionRequestInfo connectionRequestInfo
    ) throws ResourceException {
        return managedConnectionFactory
                .createManagedConnection(null, connectionRequestInfo)
                .getConnection(null, connectionRequestInfo);
    }
}
package me.ifmo.backend.integration.bitrix.jca.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionSpec;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResourceAdapterMetaData;
import jakarta.resource.spi.ConnectionManager;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.integration.bitrix.jca.BitrixConnectionFactory;
import me.ifmo.backend.integration.bitrix.jca.BitrixManagedConnectionFactory;

import javax.naming.NamingException;
import javax.naming.Reference;


@RequiredArgsConstructor
public class BitrixConnectionFactoryImpl implements BitrixConnectionFactory {
    private final BitrixManagedConnectionFactory managedConnectionFactory;
    private final ConnectionManager connectionManager;
    private Reference reference;

    @Override
    public Connection getConnection() throws ResourceException {
        return (Connection) connectionManager.allocateConnection(
                managedConnectionFactory,
                null
        );
    }

    @Override
    public Connection getConnection(ConnectionSpec connectionSpec) throws ResourceException {
        return getConnection();
    }

    @Override
    public RecordFactory getRecordFactory() throws ResourceException {
        throw new ResourceException("RecordFactory is not supported for Bitrix connection");
    }

    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        throw new ResourceException("ResourceAdapterMetaData is not supported for Bitrix connection");
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Reference getReference() throws NamingException {
        return reference;
    }
}
package me.ifmo.backend.integration.bitrix.jca.impl;

import jakarta.resource.NotSupportedException;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.ConnectionMetaData;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.LocalTransaction;
import jakarta.resource.cci.ResultSetInfo;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.integration.bitrix.jca.BitrixConnection;
import me.ifmo.backend.integration.bitrix.jca.BitrixManagedConnection;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealRequest;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealResponse;


@RequiredArgsConstructor
public class BitrixConnectionImpl implements BitrixConnection {
    private final BitrixManagedConnection managedConnection;

    @Override
    public BitrixDealResponse createDeal(BitrixDealRequest bitrixDealRequest) throws ResourceException {
        return managedConnection.createDeal(bitrixDealRequest);
    }

    @Override
    public Interaction createInteraction() throws ResourceException {
        throw new NotSupportedException("Interactions are not supported for Bitrix connection");
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("Local transactions are not supported for Bitrix REST connection");
    }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException {
        throw new NotSupportedException("Connection metadata is not supported");
    }

    @Override
    public ResultSetInfo getResultSetInfo() throws ResourceException {
        throw new NotSupportedException("Result sets are not supported for Bitrix connection");
    }

    @Override
    public void close() throws ResourceException {
        managedConnection.closeConnection(this);
    }
}
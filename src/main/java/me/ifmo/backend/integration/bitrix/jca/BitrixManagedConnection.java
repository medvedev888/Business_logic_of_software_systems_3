package me.ifmo.backend.integration.bitrix.jca;

import jakarta.resource.NotSupportedException;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.spi.ConnectionEvent;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.integration.bitrix.jca.impl.BitrixConnectionImpl;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealRequest;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class BitrixManagedConnection implements ManagedConnection {
    private final String webhookUrl;
    private final RestTemplate restTemplate;
    private final List<ConnectionEventListener> listeners = new ArrayList<>();
    private PrintWriter logWriter;


    public BitrixDealResponse createDeal(BitrixDealRequest request) throws ResourceException {
        String url = webhookUrl + "crm.deal.add.json";

        Map<String, Object> body = Map.of(
                "fields", Map.of(
                        "TITLE", request.title(),
                        "OPPORTUNITY", request.opportunity(),
                        "CURRENCY_ID", request.currencyId(),
                        "COMMENTS", request.comments()
                )
        );

        try {
            return restTemplate.postForObject(url, body, BitrixDealResponse.class);
        } catch (RestClientException exception) {
            throw new ResourceException("Failed to create Bitrix deal", exception);
        }
    }


    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) {
        return new BitrixConnectionImpl(this);
    }


    @Override
    public void destroy() {
        listeners.clear();
    }


    @Override
    public void cleanup() {
    }


    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof BitrixConnection)) {
            throw new ResourceException("Unsupported Bitrix connection handle");
        }
    }


    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }


    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }


    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException("XA transactions are not supported for Bitrix REST connection");
    }


    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("Local transactions are not supported for Bitrix REST connection");
    }


    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        throw new NotSupportedException("Managed connection metadata is not supported");
    }


    @Override
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }


    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }


    public void closeConnection(Connection connection) {
        ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        event.setConnectionHandle(connection);

        for (ConnectionEventListener listener : listeners) {
            listener.connectionClosed(event);
        }
    }

}
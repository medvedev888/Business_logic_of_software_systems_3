package me.ifmo.backend.integration.bitrix.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealRequest;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealResponse;

public interface BitrixConnection extends Connection {
    BitrixDealResponse createDeal(BitrixDealRequest bitrixDealRequest) throws ResourceException;
}

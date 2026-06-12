package me.ifmo.backend.integration.bitrix.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BitrixDealResponse (
        @JsonProperty("result")
        Long dealId
){
}

package com.nn_group.currencyaccount.infrastructure.external;

import lombok.Data;

import java.util.List;

@Data
public class NbpExchangeRateResponse {
    private String table;
    private String currency;
    private String code;
    private List<NbpRate> rates;
}
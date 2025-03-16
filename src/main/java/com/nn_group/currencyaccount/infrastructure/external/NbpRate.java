package com.nn_group.currencyaccount.infrastructure.external;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NbpRate {
    private String no;
    private String effectiveDate;
    private BigDecimal mid;
}
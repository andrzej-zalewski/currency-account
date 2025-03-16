package com.nn_group.currencyaccount.infrastructure.persistence;

import com.nn_group.currencyaccount.domain.model.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class AccountEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Currency baseCurrency;

    @Enumerated(EnumType.STRING)
    private Currency targetCurrency;

    private BigDecimal baseAmount;
    private BigDecimal targetAmount;
}
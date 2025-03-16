package com.nn_group.currencyaccount.infrastructure.config;

import com.nn_group.currencyaccount.application.service.AccountService;
import com.nn_group.currencyaccount.domain.model.Currency;
import com.nn_group.currencyaccount.domain.port.AccountRepositoryPort;
import com.nn_group.currencyaccount.domain.port.ExchangeRatePort;
import com.nn_group.currencyaccount.infrastructure.persistence.AccountRepositoryAdapter;
import com.nn_group.currencyaccount.infrastructure.persistence.JpaAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.nn_group.currencyaccount.infrastructure.external")
public class CurrencyAccountConfig {
    @Bean
    public AccountService accountService(AccountRepositoryPort accountRepositoryPort,
                                         ExchangeRatePort exchangeRatePort,
                                         @Value("${currency.default-base}") String defaultBaseCurrency) {
        return new AccountService(accountRepositoryPort, exchangeRatePort, Currency.valueOf(defaultBaseCurrency));
    }

    @Bean
    public AccountRepositoryPort accountRepositoryPort(JpaAccountRepository jpaAccountRepository) {
        return new AccountRepositoryAdapter(jpaAccountRepository);
    }
}
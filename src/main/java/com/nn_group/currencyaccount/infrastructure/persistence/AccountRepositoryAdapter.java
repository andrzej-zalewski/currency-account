package com.nn_group.currencyaccount.infrastructure.persistence;

import com.nn_group.currencyaccount.domain.model.Account;
import com.nn_group.currencyaccount.domain.port.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {
    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity savedEntity = jpaAccountRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaAccountRepository.findById(id).map(this::toDomain);
    }

    private AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setFirstName(account.getFirstName());
        entity.setLastName(account.getLastName());
        entity.setBaseCurrency(account.getBaseCurrency());
        entity.setTargetCurrency(account.getTargetCurrency());
        entity.setBaseAmount(account.getBaseAmount());
        entity.setTargetAmount(account.getTargetAmount());
        return entity;
    }

    private Account toDomain(AccountEntity entity) {
        Account account = new Account();
        account.setId(entity.getId());
        account.setFirstName(entity.getFirstName());
        account.setLastName(entity.getLastName());
        account.setBaseCurrency(entity.getBaseCurrency());
        account.setTargetCurrency(entity.getTargetCurrency());
        account.setBaseAmount(entity.getBaseAmount());
        account.setTargetAmount(entity.getTargetAmount());
        return account;
    }
}
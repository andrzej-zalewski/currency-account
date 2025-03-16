package com.nn_group.currencyaccount.domain.port;

import com.nn_group.currencyaccount.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);
    Optional<Account> findById(UUID id);
}
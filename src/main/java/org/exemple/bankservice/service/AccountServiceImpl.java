package org.exemple.bankservice.service;

import org.exemple.bankservice.OperationRepository;
import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.NegativeAmountException;
import org.exemple.bankservice.model.Amount;
import org.exemple.bankservice.model.Operation;
import org.exemple.bankservice.model.OperationType;

import java.time.Clock;
import java.time.LocalDateTime;

public class AccountServiceImpl implements AccountService {

    private final OperationRepository operationRepository;
    private final Clock clock;

    public AccountServiceImpl(OperationRepository operationRepository, Clock clock) {
        this.operationRepository = operationRepository;
        this.clock = clock;
    }

    @Override
    public void deposit(String accountId, Amount amount) throws AccountNotFoundException, NegativeAmountException {

        if (amount.isLesserThan(Amount.ZERO)) {
            throw new NegativeAmountException(amount);
        }

        Amount balance = operationRepository
                .findLast(accountId)
                .map(Operation::balance)
                .orElse(Amount.ZERO);

        Amount newBalance = balance.add(amount);
        operationRepository.add(new Operation(OperationType.DEPOSIT, accountId, LocalDateTime.now(clock), amount, newBalance));
    }
}

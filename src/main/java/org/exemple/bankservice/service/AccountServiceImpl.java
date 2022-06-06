package org.exemple.bankservice.service;

import org.exemple.bankservice.OperationRepository;
import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.InsufficientBalanceException;
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
        Amount balance = getBalance(accountId, amount);
        Amount newBalance = balance.add(amount);
        operationRepository.add(new Operation(OperationType.DEPOSIT, accountId, LocalDateTime.now(clock), amount, newBalance));
    }

    @Override
    public void withdraw(String accountId, Amount amount) throws AccountNotFoundException, InsufficientBalanceException, NegativeAmountException {
        Amount balance = getBalance(accountId, amount);

        if (balance.isLesserThan(amount)) {
            throw new InsufficientBalanceException(balance, amount);
        }

        Amount newBalance = balance.subtract(amount);
        operationRepository.add(new Operation(OperationType.WITHDRAWAL, accountId, LocalDateTime.now(clock), amount, newBalance));

    }

    @Override
    public void printAccountStatement(String accountId, AccountStatementFormatter accountStatementFormatter,
                                      AccountStatementPrinter accountStatementPrinter) throws AccountNotFoundException {
        String formattedAccountStatement = accountStatementFormatter.format(operationRepository.findAll(accountId));
        accountStatementPrinter.print(formattedAccountStatement);
    }

    private Amount getBalance(String accountId, Amount amount) throws NegativeAmountException, AccountNotFoundException {
        if (amount.isLesserThan(Amount.ZERO)) {
            throw new NegativeAmountException(amount);
        }

        return operationRepository
                .findLast(accountId)
                .map(Operation::balance)
                .orElse(Amount.ZERO);
    }
}

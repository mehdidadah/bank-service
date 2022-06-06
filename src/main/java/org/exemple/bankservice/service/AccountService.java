package org.exemple.bankservice.service;

import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.InsufficientBalanceException;
import org.exemple.bankservice.error.NegativeAmountException;
import org.exemple.bankservice.model.Amount;

public interface AccountService {

    void deposit(String accountId, Amount amount) throws AccountNotFoundException, NegativeAmountException;

    void withdraw(String accountId, Amount amount) throws AccountNotFoundException, InsufficientBalanceException, NegativeAmountException;

    void printAccountStatement(String accountId, AccountStatementFormatter accountStatementFormatter,
                               AccountStatementPrinter accountStatementPrinter) throws AccountNotFoundException;
}

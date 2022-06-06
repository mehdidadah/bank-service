package org.exemple.bankservice.service;

import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.NegativeAmountException;
import org.exemple.bankservice.model.Amount;

public interface AccountService {

    void deposit(String accountId, Amount amount) throws AccountNotFoundException, NegativeAmountException;

}
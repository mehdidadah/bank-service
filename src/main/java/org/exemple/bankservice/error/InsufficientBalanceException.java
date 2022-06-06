package org.exemple.bankservice.error;

import org.exemple.bankservice.model.Amount;

public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException(Amount balance, Amount amount) {
        super(String.format("Balance of %.2f is insufficient to withdraw amount of %.2f", balance.getValue(), amount.getValue()));
    }

}

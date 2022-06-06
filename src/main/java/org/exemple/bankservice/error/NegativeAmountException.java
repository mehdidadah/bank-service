package org.exemple.bankservice.error;

import org.exemple.bankservice.model.Amount;

public class NegativeAmountException extends Exception {

    public NegativeAmountException(Amount amount) {
        super(String.format("The amount is negative, value : %.2f", amount.getValue()));
    }

}

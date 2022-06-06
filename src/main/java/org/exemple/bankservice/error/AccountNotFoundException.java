package org.exemple.bankservice.error;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String accountId) {
        super(String.format("Specified account of id: %s does not exist", accountId));
    }
}

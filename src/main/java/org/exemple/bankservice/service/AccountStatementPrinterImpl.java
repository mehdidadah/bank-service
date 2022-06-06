package org.exemple.bankservice.service;

import java.io.PrintStream;

public class AccountStatementPrinterImpl implements AccountStatementPrinter {

    private final PrintStream printStream;

    public AccountStatementPrinterImpl(PrintStream printStream) {

        this.printStream = printStream;

    }

    @Override
    public void print(String accountStatement) {
        printStream.print(accountStatement);
    }

}

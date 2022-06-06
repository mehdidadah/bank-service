package org.exemple.bankservice;

import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.InsufficientBalanceException;
import org.exemple.bankservice.error.NegativeAmountException;
import org.exemple.bankservice.model.Amount;
import org.exemple.bankservice.service.AccountService;
import org.exemple.bankservice.service.AccountServiceImpl;
import org.exemple.bankservice.service.AccountStatementFormatter;
import org.exemple.bankservice.service.AccountStatementFormatterImpl;
import org.exemple.bankservice.service.AccountStatementPrinter;
import org.exemple.bankservice.service.AccountStatementPrinterImpl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankAccountUserStoriesTest {

    private final Clock fixedClock = Clock.fixed(Instant.ofEpochMilli(1583851594000L), ZoneId.systemDefault());

    @Test
    void shouldPrintFormattedAccountStatementFollowingMultipleDepositAndWithdrawOperations() throws AccountNotFoundException, NegativeAmountException, InsufficientBalanceException {
        // Given
        String accountId = "Jean";

        InMemoryOperationRepository operationRepository = new InMemoryOperationRepository(List.of(accountId));

        AccountStatementFormatter accountStatementFormatter = new AccountStatementFormatterImpl('.');

        ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputByteArray);
        AccountStatementPrinter accountStatementPrinter = new AccountStatementPrinterImpl(printStream);

        AccountService bankService = new AccountServiceImpl(operationRepository, fixedClock);

        bankService.deposit(accountId, new Amount(500));
        bankService.deposit(accountId, new Amount(200));
        bankService.withdraw(accountId, new Amount(500));

        // When - PRINT
        bankService.printAccountStatement(accountId, accountStatementFormatter, accountStatementPrinter);

        // Then
        String expectedStatement =
                        "                        |             Date |        Operation |          Details |  Account Balance |"
                        + System.lineSeparator() +
                        "                        |       10/03/2020 |          Deposit |           500.00 |           500.00 |"
                        + System.lineSeparator() +
                        "                        |       10/03/2020 |          Deposit |           200.00 |           700.00 |"
                        + System.lineSeparator() +
                        "                        |       10/03/2020 |       Withdrawal |          -500.00 |           200.00 |";

        assertEquals(expectedStatement, outputByteArray.toString());
    }
}

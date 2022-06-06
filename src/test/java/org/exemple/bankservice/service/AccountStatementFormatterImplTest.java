package org.exemple.bankservice.service;

import org.exemple.bankservice.model.Amount;
import org.exemple.bankservice.model.Operation;
import org.exemple.bankservice.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.exemple.bankservice.model.OperationType.DEPOSIT;
import static org.exemple.bankservice.model.OperationType.WITHDRAWAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountStatementFormatterImplTest {

    private AccountStatementFormatter accountStatementFormatter;

    @BeforeEach
    public void setup() {
        accountStatementFormatter = new AccountStatementFormatterImpl('.');
    }

    @Test
    void shouldReturnFormattedAccountStatementAsStringGivenListOfUnSortedOperations() {

        // Given
        String expectedStatement =
                "                        |             Date |        Operation |          Details |  Account Balance |" + System.lineSeparator() +
                "                        |       23/11/1995 |          Deposit |          5000.00 |          5000.00 |" + System.lineSeparator() +
                "                        |       24/11/1995 |       Withdrawal |          -500.00 |          4500.00 |" + System.lineSeparator() +
                "                        |       25/11/1995 |       Withdrawal |          -500.00 |          4000.00 |" + System.lineSeparator() +
                "                        |       26/11/1995 |          Deposit |           600.00 |          4600.00 |" + System.lineSeparator() +
                "                        |       27/11/1995 |       Withdrawal |          -800.00 |          3800.00 |" + System.lineSeparator() +
                "                        |       27/11/1995 |       Withdrawal |          -700.00 |          3100.00 |";

        String accountId = "client123";
        LocalDateTime time = LocalDateTime.of(LocalDate.of(1995, 11, 23), LocalTime.of(1, 1));

        List<Operation> operations = List.of(
                new Operation(WITHDRAWAL, accountId, time.plusDays(4), new Amount(800), new Amount(3800)),
                new Operation(DEPOSIT, accountId, time, new Amount(5000), new Amount(5000)),
                new Operation(WITHDRAWAL, accountId, time.plusDays(2), new Amount(500), new Amount(4000)),
                new Operation(WITHDRAWAL, accountId, time.plusDays(1), new Amount(500), new Amount(4500)),
                new Operation(DEPOSIT, accountId, time.plusDays(3), new Amount(600), new Amount(4600)),
                new Operation(WITHDRAWAL, accountId, time.plusDays(4).plusSeconds(30), new Amount(700), new Amount(3100)));

        // When
        String actualStatement = accountStatementFormatter.format(operations);

        // Then
        assertEquals(expectedStatement, actualStatement);
    }

    @Test
    void shouldReturnEmptyTabGivenEmptyOperationList() {

        // Given
        String expected = "                        |             Date |        Operation |          Details |  Account Balance |";

        // When
        String actual = accountStatementFormatter.format(Collections.emptyList());

        // Then
        assertEquals(expected, actual);
    }

}

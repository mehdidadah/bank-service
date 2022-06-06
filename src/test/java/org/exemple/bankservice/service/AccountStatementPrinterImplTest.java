package org.exemple.bankservice.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountStatementPrinterImplTest {

    @Test
    void shouldPrintGivenStringToPrintStream() {

        // Given
        String input = "String test";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);
        AccountStatementPrinter accountStatementPrinter = new AccountStatementPrinterImpl(printStream);

        // When
        accountStatementPrinter.print(input);

        // Then
        assertEquals(input, output.toString());

    }

}

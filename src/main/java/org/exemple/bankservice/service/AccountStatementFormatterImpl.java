package org.exemple.bankservice.service;

import org.exemple.bankservice.model.Operation;
import org.exemple.bankservice.model.OperationType;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.exemple.bankservice.model.OperationType.DEPOSIT;

public class AccountStatementFormatterImpl implements AccountStatementFormatter {

    private static final String LINE_FORMAT = "%24s|%17s |%17s |%17s |%17s |";
    private static final String HEADER = String.format(LINE_FORMAT, "", "Date", "Operation", "Details", "Account Balance");
    private static final String DEPOSIT_STRING = "Deposit";
    private static final String WITHDRAWAL_STRING = "Withdrawal";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AccountStatementFormatterImpl(char separator) {
        DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(separator);
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
    }

    public String format(List<Operation> operations) {

        StringBuilder stringBuilder = new StringBuilder();

        List<Operation> sortedOperations = new ArrayList<>(operations);

        sortedOperations.sort(Comparator.comparing(Operation::dateTime));

        stringBuilder.append(HEADER);
        for (Operation operation : sortedOperations) {
            stringBuilder.append(System.lineSeparator()).append(formatOperation(operation));
        }

        return stringBuilder.toString();
    }

    private String formatOperation(Operation operation) {

        String sign = operation.type() == DEPOSIT ? "" : "-";
        String date = operation.dateTime().format(DATE_FORMAT);

        return String.format(LINE_FORMAT, "", date, printOperationType(operation.type()), sign
                + decimalFormat.format(operation.amount().getValue()), decimalFormat.format(operation.balance().getValue()));

    }

    private String printOperationType(OperationType type) {
        return switch (type) {
            case DEPOSIT -> DEPOSIT_STRING;
            case WITHDRAWAL -> WITHDRAWAL_STRING;
        };
    }
}

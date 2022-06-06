package org.exemple.bankservice.service;

import org.exemple.bankservice.model.Operation;

import java.util.List;

public interface AccountStatementFormatter {

    String format(List<Operation> operations);

}

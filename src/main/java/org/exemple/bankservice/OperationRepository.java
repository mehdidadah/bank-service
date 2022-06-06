package org.exemple.bankservice;

import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.model.Operation;

import java.util.List;
import java.util.Optional;

public interface OperationRepository {

    void add(Operation operation) throws AccountNotFoundException;

    Optional<Operation> findLast(String accountId) throws AccountNotFoundException;

    List<Operation> findAll(String accountId) throws AccountNotFoundException;
}

package org.exemple.bankservice;

import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.model.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryOperationRepository implements OperationRepository {

    private final Map<String, List<Operation>> operations;

    public InMemoryOperationRepository(List<String> accounts) {
        this.operations = accounts.stream()
                .collect(Collectors.toMap(Function.identity(), x -> new ArrayList<>()));

    }

    @Override
    public void add(Operation operation) throws AccountNotFoundException {
        String accountId = operation.accountId();
        List<Operation> accountOperations = operations.get(accountId);

        if (accountOperations == null) {
            throw new AccountNotFoundException(accountId);
        }
        accountOperations.add(operation);
    }

    @Override
    public Optional<Operation> findLast(String accountId) throws AccountNotFoundException {
        List<Operation> accountOperations = operations.get(accountId);

        if (accountOperations == null) {
            throw new AccountNotFoundException(accountId);
        }
        return accountOperations.stream().reduce((first, second) -> second);
    }
}
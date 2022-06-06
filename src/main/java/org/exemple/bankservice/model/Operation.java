package org.exemple.bankservice.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Operation(OperationType type, String accountId, LocalDateTime dateTime, Amount amount, Amount balance) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Objects.equals(type, operation.type) && Objects.equals(accountId, operation.accountId) && Objects.equals(dateTime, operation.dateTime) && Objects.equals(amount, operation.amount) && Objects.equals(balance, operation.balance);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "type=" + type +
                ", accountId='" + accountId + '\'' +
                ", dateTime=" + dateTime +
                ", amount=" + amount +
                ", balance=" + balance +
                '}';
    }
}

package org.exemple.bankservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Amount {

    private final BigDecimal value;

    public static final Amount ZERO = new Amount(BigDecimal.ZERO);

    public Amount(BigDecimal value) {
        this.value = value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Amount(int value) {
        this(new BigDecimal(value));
    }

    public BigDecimal getValue() {
        return value;
    }

    public Amount add(Amount toAdd) {
        return new Amount(this.value.add(toAdd.getValue()));
    }

    public Amount subtract(Amount toSubtract) {
        return new Amount(this.value.subtract(toSubtract.getValue()));
    }

    public boolean isLesserThan(Amount toCompare) {
        return this.value.compareTo(toCompare.getValue()) < 0;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return Objects.equals(value, amount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

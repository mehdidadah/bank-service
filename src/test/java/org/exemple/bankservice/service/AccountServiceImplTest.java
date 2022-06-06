package org.exemple.bankservice.service;

import org.exemple.bankservice.OperationRepository;
import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.NegativeAmountException;
import org.exemple.bankservice.model.Amount;
import org.exemple.bankservice.model.Operation;
import org.exemple.bankservice.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private OperationRepository operationRepository;

    private AccountService accountService;

    private final Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    private final LocalDateTime time = LocalDateTime.now(fixedClock);

    @BeforeEach
    void setup() {
        this.accountService = new AccountServiceImpl(operationRepository, fixedClock);
    }

    //TEST USER STORY 1 : DEPOSIT
    @Test
    void shouldSaveOperationWhenDepositIsMadeGivenPositiveAmountAndExistingAccount()
            throws AccountNotFoundException, NegativeAmountException {

        // Given
        String accountId = "client1";
        Amount initialBalance = new Amount(500);
        Amount deposit = new Amount(100);
        Amount newBalance = new Amount(600);

        //find last operation to know current balance
        when(operationRepository.findLast(anyString())).thenReturn(Optional.of(new Operation(
                OperationType.DEPOSIT,
                accountId,
                time,
                new Amount(50),
                initialBalance
        )));

        // When
        accountService.deposit(accountId, deposit);

        // Then
        verify(operationRepository, times(1)).findLast(accountId);
        verify(operationRepository, times(1)).add(new Operation(
                OperationType.DEPOSIT,
                accountId,
                time,
                deposit,
                newBalance
        ));
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void shouldThrowNegativeAmountExceptionWhenMakingDepositOfNegativeAmount() {
        // Given
        String accountId = "client1";
        Amount amount = new Amount(-1);
        String expectedExceptionMessage = "The amount is negative, value : -1,00";

        // When
        Throwable exception = assertThrows(NegativeAmountException.class, () -> accountService.deposit(accountId, amount));

        // Then
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verifyNoInteractions(operationRepository);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenMakingDepositOnNonExistingAccount() throws AccountNotFoundException {
        // Given
        String accountId = "accountX";
        Amount amount = new Amount(50);
        String expectedExceptionMessage = "Specified account of id: accountX does not exist";
        doThrow(new AccountNotFoundException(accountId)).when(operationRepository).findLast(accountId);

        // When
        Throwable exception = assertThrows(AccountNotFoundException.class, () -> accountService.deposit(accountId, amount));
        assertEquals(exception.getMessage(), expectedExceptionMessage);

        // Then
        verify(operationRepository, times(1)).findLast(accountId);
        verifyNoMoreInteractions(operationRepository);
    }
}
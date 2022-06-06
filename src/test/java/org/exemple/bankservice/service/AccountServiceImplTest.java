package org.exemple.bankservice.service;

import org.exemple.bankservice.OperationRepository;
import org.exemple.bankservice.error.AccountNotFoundException;
import org.exemple.bankservice.error.InsufficientBalanceException;
import org.exemple.bankservice.error.NegativeAmountException;
import org.exemple.bankservice.model.Amount;
import org.exemple.bankservice.model.Operation;
import org.exemple.bankservice.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private AccountStatementPrinter accountStatementPrinter;

    @Mock
    private AccountStatementFormatter accountStatementFormatter;

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

    //TEST USER STORY 2 : WITHDRAW
    @Test
    void shouldSaveOperationWhenWithdrawIsMadeGivenEnoughBalanceAndPositiveAmountAndExistingAccount()
            throws AccountNotFoundException, InsufficientBalanceException, NegativeAmountException {

        // Given
        String accountId = "client1";
        Amount initialBalance = new Amount(500);
        Amount withdrawal = new Amount(100);
        Amount newBalance = new Amount(400);

        //find last operation to know current balance
        when(operationRepository.findLast(accountId)).thenReturn(Optional.of(new Operation(
                OperationType.WITHDRAWAL,
                accountId,
                time,
                new Amount(50),
                initialBalance
        )));

        // When
        accountService.withdraw(accountId, withdrawal);

        // Then
        verify(operationRepository, times(1)).findLast(accountId);
        verify(operationRepository, times(1)).add(new Operation(
                OperationType.WITHDRAWAL,
                accountId,
                time,
                withdrawal,
                newBalance
        ));
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void shouldThrowNegativeAmountExceptionWhenMakingWithdrawOfNegativeAmount() {

        // Given
        String accountId = "client1";
        Amount amount = new Amount(-1);
        String expectedExceptionMessage = "The amount is negative, value : -1,00";

        // When
        Throwable exception = assertThrows(NegativeAmountException.class, () -> accountService.withdraw(accountId, amount));

        // Then
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verifyNoInteractions(operationRepository);

    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenWithdrawingWithInsufficientBalance() throws AccountNotFoundException {

        // Given
        String accountId = "client1";
        Amount initialBalance = Amount.ZERO;
        Amount withdrawal = new Amount(10);
        String expectedExceptionMessage = "Balance of 0,00 is insufficient to withdraw amount of 10,00";

        when(operationRepository.findLast(anyString())).thenReturn(Optional.of(new Operation(
                OperationType.WITHDRAWAL,
                accountId,
                time,
                new Amount(50),
                initialBalance
        )));

        // When
        Throwable exception = assertThrows(InsufficientBalanceException.class, () -> accountService.withdraw(accountId, withdrawal));

        // Then
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(operationRepository, times(1)).findLast(accountId);
        verifyNoMoreInteractions(operationRepository);
    }


    @Test
    void shouldThrowAccountNotFoundExceptionWhenWithdrawingFromNonExistingAccount() throws AccountNotFoundException {
        // Given
        String accountId = "accountX";
        Amount amount = new Amount(50);
        String expectedExceptionMessage = "Specified account of id: accountX does not exist";
        doThrow(new AccountNotFoundException(accountId)).when(operationRepository).findLast(accountId);

        // When
        Throwable exception = assertThrows(AccountNotFoundException.class, () -> accountService.withdraw(accountId, amount));

        // Then
        assertEquals(exception.getMessage(), expectedExceptionMessage);
        verify(operationRepository, times(1)).findLast(accountId);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void shouldPrintAccountStatementWhenCalledForExistingAccountWithOperationHistory() throws AccountNotFoundException {

        // Given
        String accountId = "client123";
        List<Operation> operations = List.of(
                new Operation(OperationType.DEPOSIT, accountId, time, new Amount(500), new Amount(5000)),
                new Operation(OperationType.WITHDRAWAL, accountId, time, new Amount(500), new Amount(4500)));
        String dummyFormattedStatement = "formatted statement";

        when(operationRepository.findAll(accountId)).thenReturn(operations);
        when(accountStatementFormatter.format(operations)).thenReturn(dummyFormattedStatement);

        // When
        accountService.printAccountStatement(accountId, accountStatementFormatter, accountStatementPrinter);

        // Then
        InOrder inOrder = inOrder(operationRepository, accountStatementFormatter, accountStatementPrinter);
        inOrder.verify(operationRepository, times(1)).findAll(accountId);
        inOrder.verify(accountStatementFormatter, times(1)).format(operations);
        inOrder.verify(accountStatementPrinter, times(1)).print(dummyFormattedStatement);
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenPrintAccountStatementIsCalledForNonExistingAccount() throws AccountNotFoundException {

        // Given
        String accountId = "accountX";
        String expectedExceptionMessage = "Specified account of id: accountX does not exist";
        doThrow(new AccountNotFoundException(accountId)).when(operationRepository).findAll(accountId);

        // When
        Throwable exception = assertThrows(AccountNotFoundException.class, () -> accountService.printAccountStatement(accountId,
                accountStatementFormatter, accountStatementPrinter));

        // Then
        assertEquals(exception.getMessage(), expectedExceptionMessage);
        verify(operationRepository, times(1)).findAll(accountId);
        verifyNoMoreInteractions(operationRepository, accountStatementFormatter, accountStatementPrinter);
    }
}
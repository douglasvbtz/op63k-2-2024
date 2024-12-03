package br.edu.utfpr.bankapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.utfpr.bankapi.dto.TransferDTO;
import br.edu.utfpr.bankapi.dto.WithdrawDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import br.edu.utfpr.bankapi.repository.TransactionRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;
import br.edu.utfpr.bankapi.validations.AvailableBalanceValidation;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AvailableAccountValidation accountValidation;

    @Mock
    private AvailableBalanceValidation balanceValidation;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Test
    void deveriaSacar() throws NotFoundException {
        // ARRANGE
        double saldoInicial = 1000.0;
        var withdrawDTO = new WithdrawDTO(12345, 200);
        var sourceAccount = new Account("John Doe", 12345, saldoInicial, 0);

        BDDMockito.given(accountValidation.validate(sourceAccount.getNumber()))
                .willReturn(sourceAccount);

        // ACT
        transactionService.withdraw(withdrawDTO);

        // ASSERT
        BDDMockito.then(transactionRepository).should().save(BDDMockito.any());
        BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
        Transaction transacaoSalva = transactionCaptor.getValue();

        Assertions.assertEquals(sourceAccount, transacaoSalva.getSourceAccount());
        Assertions.assertEquals(withdrawDTO.amount(), transacaoSalva.getAmount());
        Assertions.assertEquals(TransactionType.WITHDRAW, transacaoSalva.getType());
        Assertions.assertEquals(saldoInicial - withdrawDTO.amount(), 
            transacaoSalva.getSourceAccount().getBalance());
    }

    @Test
    void deveriaTransferir() throws NotFoundException {
        // ARRANGE
        double saldoOrigem = 1000.0;
        double saldoDestino = 500.0;
        var transferDTO = new TransferDTO(12345, 67890, 200);
        var sourceAccount = new Account("John Doe", 12345, saldoOrigem, 0);
        var receiverAccount = new Account("Jane Doe", 67890, saldoDestino, 0);

        BDDMockito.given(accountValidation.validate(sourceAccount.getNumber()))
                .willReturn(sourceAccount);
        BDDMockito.given(accountValidation.validate(receiverAccount.getNumber()))
                .willReturn(receiverAccount);

        // ACT
        transactionService.transfer(transferDTO);

        // ASSERT
        BDDMockito.then(transactionRepository).should().save(BDDMockito.any());
        BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
        Transaction transacaoSalva = transactionCaptor.getValue();

        Assertions.assertEquals(sourceAccount, transacaoSalva.getSourceAccount());
        Assertions.assertEquals(receiverAccount, transacaoSalva.getReceiverAccount());
        Assertions.assertEquals(transferDTO.amount(), transacaoSalva.getAmount());
        Assertions.assertEquals(TransactionType.TRANSFER, transacaoSalva.getType());
        Assertions.assertEquals(saldoOrigem - transferDTO.amount(), 
            transacaoSalva.getSourceAccount().getBalance());
        Assertions.assertEquals(saldoDestino + transferDTO.amount(), 
            transacaoSalva.getReceiverAccount().getBalance());
    }
} 
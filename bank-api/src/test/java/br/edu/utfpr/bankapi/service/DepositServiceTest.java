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

import br.edu.utfpr.bankapi.dto.DepositDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import br.edu.utfpr.bankapi.repository.TransactionRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;

@ExtendWith(MockitoExtension.class)
public class DepositServiceTest {
    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    AvailableAccountValidation accountValidation;

    @InjectMocks
    TransactionService transactionService; // Objeto a ser testado

    @Captor
    ArgumentCaptor<Transaction> transactionCaptor;

    @Test
    void deveriaDepositar() throws NotFoundException {
        // ARRANGE
        double saldoInicial = 145.34;
        var depositDTO = new DepositDTO(12345, 1000);
        var receiverAccount = new Account("John Smith",
                12345, saldoInicial, 0);

        BDDMockito.given(accountValidation.validate(receiverAccount.getNumber()))
                .willReturn(receiverAccount);

        // ACT
        transactionService.deposit(depositDTO);

        // ASSERT
        // Verifica se o método save(...) do repositório (TransactionRepository)
        // foi invocado, sendo passado qualquer objeto como parâmetro.
        BDDMockito.then(transactionRepository).should().save(BDDMockito.any());

        // Verifica se o método save(...) foi executado recebendo com parametro
        // um objeto do tipo Transaction. 
        // O obj Transaction será capturado neste teste.
        BDDMockito
                .then(transactionRepository)
                .should().save(transactionCaptor.capture());
        Transaction transacaoSalva = transactionCaptor.getValue();

        // Analisando os valores que estão na transação

        // Verificando se a conta destinatária é a mesma que está na transação
        Assertions.assertEquals(receiverAccount, transacaoSalva.getReceiverAccount());

        // Varificando se o valor da transção é o mesmo que foi informado no depósito (DTO)
        Assertions.assertEquals(depositDTO.amount(), transacaoSalva.getAmount()); 

        // Verificando se a operação definida na transação é DEPOSIT
        Assertions.assertEquals(TransactionType.DEPOSIT, transacaoSalva.getType());

        // Verificar se o saldo foi alterado na conta de destino
        Assertions.assertEquals(saldoInicial + depositDTO.amount(), 
            transacaoSalva.getReceiverAccount().getBalance());
    }
}

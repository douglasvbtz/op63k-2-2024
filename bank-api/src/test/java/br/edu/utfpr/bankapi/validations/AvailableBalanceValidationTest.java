package br.edu.utfpr.bankapi.validations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.utfpr.bankapi.exception.WithoutBalanceException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;

@ExtendWith(MockitoExtension.class)
public class AvailableBalanceValidationTest {
    @InjectMocks
    AvailableBalanceValidation validation;

    @Mock
    Transaction transaction;

    @Mock
    Account sourceAccount;

    @Test
    void deveriaValidarSaldoSuficiente() {
        // ARRANGE
        double saldo = 1000.0;
        double limiteEspecial = 500.0;
        double valorTransacao = 800.0;

        // Configurando a conta mockada
        BDDMockito.given(sourceAccount.getBalanceWithLimit()).willReturn(saldo + limiteEspecial);
        BDDMockito.given(transaction.getSourceAccount()).willReturn(sourceAccount);
        BDDMockito.given(transaction.getAmount()).willReturn(valorTransacao);

        // ACT + ASSERT
        Assertions.assertDoesNotThrow(() -> validation.validate(transaction));
    }

    @Test
    void deveriaLancarWithoutBalanceExceptionQuandoSaldoInsuficiente() {
        // ARRANGE
        double saldo = 1000.0;
        double limiteEspecial = 500.0;
        double valorTransacao = 1600.0; // Valor maior que saldo + limite

        // Configurando a conta mockada
        BDDMockito.given(sourceAccount.getBalanceWithLimit()).willReturn(saldo + limiteEspecial);
        BDDMockito.given(transaction.getSourceAccount()).willReturn(sourceAccount);
        BDDMockito.given(transaction.getAmount()).willReturn(valorTransacao);

        // ACT + ASSERT
        Assertions.assertThrows(WithoutBalanceException.class, () -> validation.validate(transaction));
    }
} 
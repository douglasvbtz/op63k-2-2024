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

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Test
    void deveriaRetornarContaPorNumero() {
        // ARRANGE
        long accountNumber = 12345;
        Account account = new Account("John Doe", accountNumber, 1000, 500);
        BDDMockito.given(accountRepository.getByNumber(accountNumber)).willReturn(Optional.of(account));

        // ACT
        Optional<Account> result = accountService.getByNumber(accountNumber);

        // ASSERT
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(account, result.get());
    }

    @Test
    void deveriaRetornarTodasAsContas() {
        // ARRANGE
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("John Doe", 12345, 1000, 500));
        accounts.add(new Account("Jane Doe", 67890, 2000, 1000));
        BDDMockito.given(accountRepository.findAll()).willReturn(accounts);

        // ACT
        List<Account> result = accountService.getAll();

        // ASSERT
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(accounts, result);
    }

    @Test
    void deveriaSalvarUmaConta() {
        // ARRANGE
        AccountDTO accountDTO = new AccountDTO("John Doe", 12345L, 0, 500);
        Account account = new Account(accountDTO);
        BDDMockito.given(accountRepository.save(BDDMockito.any(Account.class))).willReturn(account);

        // ACT
        Account result = accountService.save(accountDTO);

        // ASSERT
        Assertions.assertEquals(account, result);
        BDDMockito.then(accountRepository).should().save(accountCaptor.capture());
        Assertions.assertEquals(accountDTO.name(), accountCaptor.getValue().getName());
    }

    @Test
    void deveriaAtualizarUmaConta() throws NotFoundException {
        // ARRANGE
        long accountId = 1L;
        Account existingAccount = new Account("John Doe", 12345, 1000, 500);
        AccountDTO updatedAccountDTO = new AccountDTO("John Smith", 12345L, 0, 600);
        BDDMockito.given(accountRepository.findById(accountId)).willReturn(Optional.of(existingAccount));
        BDDMockito.given(accountRepository.save(BDDMockito.any(Account.class))).willReturn(existingAccount);

        // ACT
        Account result = accountService.update(accountId, updatedAccountDTO);

        // ASSERT
        Assertions.assertEquals(existingAccount, result);
        Assertions.assertEquals(updatedAccountDTO.name(), existingAccount.getName());
        BDDMockito.then(accountRepository).should().save(accountCaptor.capture());
        Assertions.assertEquals(updatedAccountDTO.specialLimit(), accountCaptor.getValue().getSpecialLimit());
    }

    @Test
    void deveriaLancarNotFoundExceptionAoAtualizarContaInexistente() {
        // ARRANGE
        long accountId = 1L;
        AccountDTO updatedAccountDTO = new AccountDTO("John Smith", 12345L, 0, 600);
        BDDMockito.given(accountRepository.findById(accountId)).willReturn(Optional.empty());

        // ACT & ASSERT
        Assertions.assertThrows(NotFoundException.class, () -> accountService.update(accountId, updatedAccountDTO));
    }
} 
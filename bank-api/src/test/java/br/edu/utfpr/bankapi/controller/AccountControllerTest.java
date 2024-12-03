package br.edu.utfpr.bankapi.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.model.Account;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    EntityManager entityManager;

    @Test
    void deveriaRetornarTodasAsContas() throws Exception {
        // ARRANGE
        Account account1 = new Account("Ricardo Sobjak", 12345, 1000, 500);
        Account account2 = new Account("Juca Silva", 67890, 2000, 1000);
        entityManager.persist(account1);
        entityManager.persist(account2);

        // ACT
        var res = mvc.perform(MockMvcRequestBuilders.get("/account"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
        // Verifica se o número de contas retornadas é 2
        Assertions.assertTrue(res.getContentAsString().contains("Ricardo Sobjak"));
        Assertions.assertTrue(res.getContentAsString().contains("Juca Silva"));
    }

    @Test
    void deveriaRetornarContaPorNumero() throws Exception {
        // ARRANGE
        Account account = new Account("Ana Campos", 12346, 500, 0);
        entityManager.persist(account);

        // ACT
        var res = mvc.perform(MockMvcRequestBuilders.get("/account/12346"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
        Assertions.assertTrue(res.getContentAsString().contains("Ana Campos"));
    }

    @Test
    void deveriaRetornar404ParaContaInexistente() throws Exception {
        // ACT
        var res = mvc.perform(MockMvcRequestBuilders.get("/account/99999"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(404, res.getStatus());
    }

    @Test
    void deveriaCriarUmaConta() throws Exception {
        // ARRANGE
        var json = """
                    {
                        "name": "Ricardo Sobjak",
                        "number": 12345,
                        "balance": 1000,
                        "specialLimit": 500
                    }
                """;

        // ACT
        var res = mvc.perform(MockMvcRequestBuilders.post("/account")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
    }

    @Test
    void deveriaAtualizarUmaConta() throws Exception {
        // ARRANGE
        Account account = new Account("Ricardo Sobjak", 12345, 1000, 500);
        entityManager.persist(account);

        var json = """
                    {
                        "name": "Ricardo Sobjak Atualizado",
                        "number": 12345,
                        "balance": 1500,
                        "specialLimit": 600
                    }
                """;

        // ACT
        var res = mvc.perform(MockMvcRequestBuilders.put("/account/1")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
        Assertions.assertTrue(res.getContentAsString().contains("Ricardo Sobjak Atualizado"));
    }

    @Test
    void deveriaRetornar404AoAtualizarContaInexistente() throws Exception {
        // ARRANGE
        var json = """
                    {
                        "name": "Conta Inexistente",
                        "number": 99999,
                        "balance": 1000,
                        "specialLimit": 500
                    }
                """;

        // ACT
        var res = mvc.perform(MockMvcRequestBuilders.put("/account/99999")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(404, res.getStatus());
    }
} 
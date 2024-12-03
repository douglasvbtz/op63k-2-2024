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

import br.edu.utfpr.bankapi.dto.TransferDTO;
import br.edu.utfpr.bankapi.dto.WithdrawDTO;
import br.edu.utfpr.bankapi.model.Account;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    EntityManager entityManager;

    @Test
    void deveriaRetornar201ParaTransferenciaValida() throws Exception {
        // ARRANGE
        Account sourceAccount = new Account("John Doe", 12345, 1000, 500);
        Account receiverAccount = new Account("Jane Doe", 67890, 500, 300);
        entityManager.persist(sourceAccount);
        entityManager.persist(receiverAccount);

        var json = """
                    {
                        "sourceAccountNumber": 12345,
                        "receiverAccountNumber": 67890,
                        "amount": 200
                    }
                """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
    }

    @Test
    void deveriaRetornar400ParaTransferenciaInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Dados para requisição

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void deveriaRetornar201ParaSaqueValido() throws Exception {
        // ARRANGE
        Account sourceAccount = new Account("John Doe", 12345, 1000, 500);
        entityManager.persist(sourceAccount);

        var json = """
                    {
                        "sourceAccountNumber": 12345,
                        "amount": 200
                    }
                """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
    }

    @Test
    void deveriaRetornar400ParaSaqueInvalido() throws Exception {
        // ARRANGE
        var json = "{}"; // Dados para requisição

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }
} 
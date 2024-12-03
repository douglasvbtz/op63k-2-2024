package br.edu.utfpr.bankapi.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.edu.utfpr.bankapi.model.Account;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class DepositControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    EntityManager entityManager;

    @Test
    void deveriaRetornar400ParaRequisicaoInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Dados para requisição

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders
                        .post("/transaction/deposit").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void deveriaRetornar201ParaRequisicaoValida() throws Exception {
        // ARRANGE
        Account account = new Account("Felizberto",
                987654321, 0, 1000);

        entityManager.persist(account); // Salvando a conta

        var json = """
                    {
                        "receiverAccountNumber": 987654321,
                        "amount": 200
                    }
                """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders
                        .post("/transaction/deposit").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
    }

    @Test
    void deveriaRetornarDadosCorretosParaRequisicaoValida() throws Exception {
        // ARRANGE
        Account account = new Account("Felizberto",
                987654321, 0, 1000);

        entityManager.persist(account); // Salvando a conta

        var json = """
                    {
                        "receiverAccountNumber": 987654321,
                        "amount": 200
                    }
                """;

        // ACT + ASSERT
        mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.jsonPath("$.receiverAccount.number",
                        Matchers.equalTo(987654321)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.receiverAccount.balance",
                        Matchers.equalTo(account.getBalance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.equalTo(200.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.equalTo("DEPOSIT")));
    }

}

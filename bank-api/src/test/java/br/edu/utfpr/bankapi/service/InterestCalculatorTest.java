package br.edu.utfpr.bankapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InterestCalculatorTest {

    @Test
    void deveriaCalcularJurosCompostosPorMes() {
        // ARRANGE (configurações para o teste)
        float valor = 1000f;
        float taxa = 1.5f;
        int prazo = 6; // meses

        // ACT (executar a funcionalidade a ser testada)
        var res = InterestCalculator.calcularJuros(valor, taxa, prazo);

        // ASSERT (verificar o resultado com o esperado)
        Assertions.assertEquals(93.44, res);
    }

    @Test
    void deveriaCalcularJurosCompostosPorDia() {
        // ARRANGE (configurações para o teste)
        float valor = 1000f;
        float taxa = 0.05f;
        int prazo = 30; // dias

        // ACT (executar a funcionalidade a ser testada)
        var res = InterestCalculator.calcularJuros(valor, taxa, prazo);

        // ASSERT (verificar o resultado com o esperado)
        Assertions.assertEquals(15.11, res);
    }



}

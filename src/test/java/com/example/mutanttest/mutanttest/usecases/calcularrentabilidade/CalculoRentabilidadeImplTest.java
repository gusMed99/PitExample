package com.example.mutanttest.mutanttest.usecases.calcularrentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIofRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIrRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.CalculoRentabilidadeImpl;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.ValorCdiRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;

@RunWith(SpringRunner.class)
public class CalculoRentabilidadeImplTest {

    @Mock
    private  AliquotaIofRest aliquotaIofRest;

    @Mock
    private  AliquotaIrRest aliquotaIrRest;

    @Mock
    private  ValorCdiRest valorCdiRest;

    @InjectMocks
    private CalculoRentabilidadeImpl calculoRentabilidade;

    @Test
    public void testObterCalculoRentabilidadeOkSemIof(){
        final int quantidadeDiasOperacao = 365;
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 1);
        final LocalDate dataPrevisaoResgate = dataAplicacao.plusDays(quantidadeDiasOperacao);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(50000))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        final BigDecimal aliquotaIofEsperada = BigDecimal.ZERO;
        final BigDecimal aliquotaIrEsperada = BigDecimal.valueOf(0.175);
        final BigDecimal rendimentoDiarioCdiEsperado = BigDecimal.valueOf(0.000331);
        final BigDecimal rendimentoAnualCdiEsperado = BigDecimal.valueOf(0.1265);
        this.executarChamadaIof(aliquotaIofEsperada);
        this.executarChamadaIr(aliquotaIrEsperada);
        this.executarChamadaTaxaCdi(rendimentoDiarioCdiEsperado, rendimentoAnualCdiEsperado);
        final CalculoRentabilidadeResponse response = this.calculoRentabilidade.obterCalculoRentabilidade(request);

        this.assertDadosChamadaIof(quantidadeDiasOperacao);
        this.asserDadosChamadaIr(quantidadeDiasOperacao);

        Mockito.verify(this.valorCdiRest).obterTaxaCdi();
        
        Assert.assertEquals(BigDecimal.valueOf(56419.68), response.getValorBruto());
        Assert.assertEquals(BigDecimal.valueOf(55296.24), response.getValorLiquido());
        Assert.assertEquals(BigDecimal.valueOf(1123.44), response.getImpostoDeRenda());
        Assert.assertEquals(BigDecimal.ZERO.setScale(2), response.getIof());
        Assert.assertEquals(aliquotaIrEsperada.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP), response.getAliquotaImpostoRendaAplicada());
        Assert.assertEquals(aliquotaIofEsperada.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP), response.getAliquotaIofAplicada());

    }

    private void assertDadosChamadaIof(final Integer quantidadeDiasOperacaoEsperada){
        final ArgumentCaptor<AliquotaIofRestRequest> aliquotaIofRestCaptor = ArgumentCaptor.forClass(AliquotaIofRestRequest.class);
        Mockito.verify(this.aliquotaIofRest).obterAliquotaIof(aliquotaIofRestCaptor.capture());
        final AliquotaIofRestRequest dadosChamadaMockIof = aliquotaIofRestCaptor.getValue();
        Assert.assertEquals(quantidadeDiasOperacaoEsperada, dadosChamadaMockIof.getQuantidadeDiasOperacao());
    }

    private void asserDadosChamadaIr(final Integer quantidadeDiasOperacaoEsperada){
        final ArgumentCaptor<AliquotaIrRestRequest> aliquotaIrRestCaptor = ArgumentCaptor.forClass(AliquotaIrRestRequest.class);
        Mockito.verify(this.aliquotaIrRest).obterAliquotaImpostoRenda(aliquotaIrRestCaptor.capture());
        final AliquotaIrRestRequest dadosChamadaMockIr = aliquotaIrRestCaptor.getValue();
        Assert.assertEquals(quantidadeDiasOperacaoEsperada, dadosChamadaMockIr.getQuantidadeDiasOperacao());
    }

    private void executarChamadaIof(final BigDecimal aliquotaIofEsperada){
        Mockito.when(this.aliquotaIofRest.obterAliquotaIof(Mockito.any()))
                .thenReturn(AliquotaIofRestResponse.builder()
                        .aliquotaIof(aliquotaIofEsperada)
                        .build());
    }

    private void executarChamadaIr(final BigDecimal aliquotaIrEsperada){
        Mockito.when(this.aliquotaIrRest.obterAliquotaImpostoRenda(Mockito.any()))
                .thenReturn(AliquotaIrRestResponse.builder()
                        .valorAliquotaIr(aliquotaIrEsperada)
                        .build());
    }

    private void executarChamadaTaxaCdi(final BigDecimal rendimentoDiarioEsperado, final BigDecimal rendimentoAnualEsperado){
        Mockito.when(this.valorCdiRest.obterTaxaCdi())
                .thenReturn(ValorCdiRestResponse.builder()
                        .taxaAnual(rendimentoAnualEsperado)
                        .taxaDiaria(rendimentoDiarioEsperado)
                        .build());
    }
}

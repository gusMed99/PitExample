package com.example.mutanttest.mutanttest.usecases.calcularrentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIofRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIrRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.CalculoRentabilidadeImpl;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.ValorCdiRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.*;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.exception.DadosInvalidosException;
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

    @Test
    public void testErroSaldoNegativo(){
        final int quantidadeDiasOperacao = 22;
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = dataAplicacao.plusDays(quantidadeDiasOperacao);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(-2000))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        Assert.assertThrows(DadosInvalidosException.class,() ->this.calculoRentabilidade.obterCalculoRentabilidade(request));
    }

    @Test
    public void testErroDataResgateAnteriorAplicacao(){
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = LocalDate.of(2022,Month.MAY,8);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(2000))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        Assert.assertThrows(DadosInvalidosException.class,() ->this.calculoRentabilidade.obterCalculoRentabilidade(request));
    }

    @Test
    public void testObterCalculoRentabilidadeOkComIof(){
        final int quantidadeDiasOperacao = 22;
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = dataAplicacao.plusDays(quantidadeDiasOperacao);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(2000))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        final BigDecimal aliquotaIofEsperada = BigDecimal.valueOf(0.26);
        final BigDecimal aliquotaIrEsperada = BigDecimal.valueOf(0.225);
        final BigDecimal rendimentoDiarioCdiEsperado = BigDecimal.valueOf(0.000331);
        final BigDecimal rendimentoAnualCdiEsperado = BigDecimal.valueOf(0.1265);
        this.executarChamadaIof(aliquotaIofEsperada);
        this.executarChamadaIr(aliquotaIrEsperada);
        this.executarChamadaTaxaCdi(rendimentoDiarioCdiEsperado, rendimentoAnualCdiEsperado);
        final CalculoRentabilidadeResponse response = this.calculoRentabilidade.obterCalculoRentabilidade(request);

        this.assertDadosChamadaIof(quantidadeDiasOperacao);
        this.asserDadosChamadaIr(quantidadeDiasOperacao);

        Mockito.verify(this.valorCdiRest).obterTaxaCdi();

        Assert.assertEquals(BigDecimal.valueOf(2014.61), response.getValorBruto());
        Assert.assertEquals(BigDecimal.valueOf(2008.38), response.getValorLiquido());
        Assert.assertEquals(BigDecimal.valueOf(2.43), response.getImpostoDeRenda());
        Assert.assertEquals(BigDecimal.valueOf(3.80).setScale(2,RoundingMode.HALF_UP), response.getIof());
        Assert.assertEquals(aliquotaIrEsperada.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP), response.getAliquotaImpostoRendaAplicada());
        Assert.assertEquals(aliquotaIofEsperada.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP), response.getAliquotaIofAplicada());

    }

    @Test
    public void testObterCalculoRentabilidadeSaldoIgualAUm(){
        final int quantidadeDiasOperacao = 2000;
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = dataAplicacao.plusDays(quantidadeDiasOperacao);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(1))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        final BigDecimal aliquotaIofEsperada = BigDecimal.ZERO;
        final BigDecimal aliquotaIrEsperada = BigDecimal.valueOf(0.15);
        final BigDecimal rendimentoDiarioCdiEsperado = BigDecimal.valueOf(0.000331);
        final BigDecimal rendimentoAnualCdiEsperado = BigDecimal.valueOf(0.1265);
        this.executarChamadaIof(aliquotaIofEsperada);
        this.executarChamadaIr(aliquotaIrEsperada);
        this.executarChamadaTaxaCdi(rendimentoDiarioCdiEsperado, rendimentoAnualCdiEsperado);
        final CalculoRentabilidadeResponse response = this.calculoRentabilidade.obterCalculoRentabilidade(request);

        this.assertDadosChamadaIof(quantidadeDiasOperacao);
        this.asserDadosChamadaIr(quantidadeDiasOperacao);

        Mockito.verify(this.valorCdiRest).obterTaxaCdi();

        Assert.assertEquals(BigDecimal.valueOf(1.94), response.getValorBruto());
        Assert.assertEquals(BigDecimal.valueOf(1.80).setScale(2, RoundingMode.HALF_UP), response.getValorLiquido());
        Assert.assertEquals(BigDecimal.valueOf(0.14), response.getImpostoDeRenda());
        Assert.assertEquals(BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP), response.getIof());
        Assert.assertEquals(aliquotaIrEsperada.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP), response.getAliquotaImpostoRendaAplicada());
        Assert.assertEquals(aliquotaIofEsperada.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP), response.getAliquotaIofAplicada());

    }

    @Test
    public void testErroSaldoZerado(){
        final int quantidadeDiasOperacao = 22;
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = dataAplicacao.plusDays(quantidadeDiasOperacao);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.ZERO)
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        Assert.assertThrows(DadosInvalidosException.class,() ->this.calculoRentabilidade.obterCalculoRentabilidade(request));
    }


    @Test
    public void testErroDatasIguais(){
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = LocalDate.of(2022,Month.MAY,9);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(2000))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(100))
                .build();
        Assert.assertThrows(DadosInvalidosException.class,() ->this.calculoRentabilidade.obterCalculoRentabilidade(request));
    }



    @Test
    public void testObterCalculoRentabilidadeOkComVariacaoDePercentualRendimento(){
        final int quantidadeDiasOperacao = 365;
        final LocalDate dataAplicacao = LocalDate.of(2022, Month.MAY, 9);
        final LocalDate dataPrevisaoResgate = dataAplicacao.plusDays(quantidadeDiasOperacao);
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(20000))
                .dataAplicacao(dataAplicacao)
                .dataPrevisaoResgate(dataPrevisaoResgate)
                .percentualRendimentoCdi(BigDecimal.valueOf(155))
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

        Assert.assertEquals(BigDecimal.valueOf(24117.74), response.getValorBruto());
        Assert.assertEquals(BigDecimal.valueOf(23397.14), response.getValorLiquido());
        Assert.assertEquals(BigDecimal.valueOf(720.60).setScale(2, RoundingMode.HALF_UP), response.getImpostoDeRenda());
        Assert.assertEquals(BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP), response.getIof());
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

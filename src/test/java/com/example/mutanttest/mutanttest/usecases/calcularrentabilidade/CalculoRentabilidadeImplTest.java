package com.example.mutanttest.mutanttest.usecases.calcularrentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIofRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIrRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.CalculoRentabilidadeImpl;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.ValorCdiRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        final CalculoRentabilidadeRequest request = CalculoRentabilidadeRequest.builder()
                .valorInvestido(BigDecimal.valueOf(50000))
                .dataAplicacao(LocalDate.of(2022, Month.MAY,1))
                .dataPrevisaoResgate(LocalDate.of(2023,Month.MAY,1))
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
        Assert.assertEquals(BigDecimal.valueOf(56419.68), response.getValorBruto());
        Assert.assertEquals(BigDecimal.valueOf(55296.24), response.getValorLiquido());
        Assert.assertEquals(BigDecimal.valueOf(1123.44), response.getImpostoDeRenda());
        //Assert.assertEquals(BigDecimal.ZERO, response.getIof());
        Assert.assertEquals(aliquotaIrEsperada.multiply(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)), response.getAliquotaImpostoRendaAplicada());
        Assert.assertEquals(aliquotaIofEsperada.multiply(BigDecimal.valueOf(100)), response.getAliquotaIofAplicada());

    }

    private void executarChamadaIof(final BigDecimal aliquotaIofEsperada){
        Mockito.when(this.aliquotaIofRest.obterAliquotaIof(Mockito.any()))
                .thenReturn(AliquotaIofRestResponse.builder()
                        .aliquotaIof(aliquotaIofEsperada)
                        .build());
    }

    private void executarChamadaIr(final BigDecimal aliquotaIrEsperada){
        Mockito.when(this.aliquotaIrRest.obterAlqiuotaImpostoRenda(Mockito.any()))
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

package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;


import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CalculoRentabilidadeImpl implements CalculoRentabilidade {

    private final AliquotaIofRest aliquotaIofRest;
    private final AliquotaIrRest aliquotaIrRest;
    private final ValorCdiRest valorCdiRest;

    @Override
    public CalculoRentabilidadeResponse obterCalculoRentabilidade(final CalculoRentabilidadeRequest request) {
        final int quantidadeDiasOperacao = (int) ChronoUnit.DAYS.between(request.getDataAplicacao(), request.getDataPrevisaoResgate());
        final AliquotaIofRestResponse aliquotaIofRestResponse = this.aliquotaIofRest.obterAliquotaIof(AliquotaIofRestRequest.builder()
                .quantidadeDiasOperacao(quantidadeDiasOperacao)
                .build());
        final AliquotaIrRestResponse aliquotaIrRestResponse = this.aliquotaIrRest.obterAlqiuotaImpostoRenda(AliquotaIrRestRequest.builder()
                .quantidadeDiasOperacao(quantidadeDiasOperacao)
                .build());
        final ValorCdiRestResponse valorCdiRestResponse = this.valorCdiRest.obterTaxaCdi();

        BigDecimal percentualRendimentoAjustado = request.getPercentualRendimentoCdi().divide(BigDecimal.valueOf(100D));
        final BigDecimal rendimentoDiarioAjustado = valorCdiRestResponse.getTaxaDiaria().multiply(percentualRendimentoAjustado);

        final BigDecimal brutoTotal = this.calcularValorBruto(request.getValorInvestido(), rendimentoDiarioAjustado, quantidadeDiasOperacao);

        final BigDecimal rendimentoBruto = brutoTotal.subtract(request.getValorInvestido());

        final BigDecimal totalIof = this.calcularTotalIof(rendimentoBruto, aliquotaIofRestResponse.getAliquotaIof());

        final BigDecimal totalIr = this.calcularTotalIr(rendimentoBruto, totalIof, aliquotaIrRestResponse.getValorAliquotaIr());


        return CalculoRentabilidadeResponse.builder()
                .valorBruto(brutoTotal)
                .valorLiquido(brutoTotal.subtract(totalIof).subtract(totalIr))
                .impostoDeRenda(totalIr)
                .aliquotaImpostoRendaAplicada(aliquotaIrRestResponse.getValorAliquotaIr().multiply(BigDecimal.valueOf(100)))
                .iof(totalIof)
                .aliquotaIofAplicada(aliquotaIofRestResponse.getAliquotaIof())
                .build();
    }

    private BigDecimal calcularValorBruto(final BigDecimal valorInvestido, final BigDecimal rendimentoDiario,
                                          final int quantidadeDiasOperacao){

        final BigDecimal resultadoPotencia = rendimentoDiario.add(BigDecimal.valueOf(1)).pow(quantidadeDiasOperacao);
        return valorInvestido.multiply(resultadoPotencia).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularTotalIof(final BigDecimal rendimentoBruto, final BigDecimal aliquotaIof){
        return (rendimentoBruto.multiply(aliquotaIof));
    }

    private BigDecimal calcularTotalIr(final BigDecimal rendimentoBruto, final BigDecimal totalIof, final BigDecimal aliquotaIr){
        final BigDecimal burotDescontadoIof = rendimentoBruto.subtract(totalIof);
        return burotDescontadoIof.multiply(aliquotaIr);
    }


}

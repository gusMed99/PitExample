package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;


import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.*;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.exception.DadosInvalidosException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CalculoRentabilidadeImpl implements CalculoRentabilidade {

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final int PRECISAO_ARREDONDAMENTO = 2;
    private final AliquotaIofRest aliquotaIofRest;
    private final AliquotaIrRest aliquotaIrRest;
    private final ValorCdiRest valorCdiRest;

    @Override
    public CalculoRentabilidadeResponse obterCalculoRentabilidade(final CalculoRentabilidadeRequest request) {
        this.validarDadosRecebidos(request);
        final int quantidadeDiasOperacao = (int) ChronoUnit.DAYS.between(request.getDataAplicacao(), request.getDataPrevisaoResgate());
        final AliquotaIofRestResponse aliquotaIofRestResponse = this.aliquotaIofRest.obterAliquotaIof(AliquotaIofRestRequest.builder()
                .quantidadeDiasOperacao(quantidadeDiasOperacao)
                .build());
        final AliquotaIrRestResponse aliquotaIrRestResponse = this.aliquotaIrRest.obterAliquotaImpostoRenda(AliquotaIrRestRequest.builder()
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
                .valorBruto(brutoTotal.setScale(PRECISAO_ARREDONDAMENTO, ROUNDING_MODE))
                .valorLiquido(brutoTotal.subtract(totalIof).subtract(totalIr).setScale(PRECISAO_ARREDONDAMENTO,ROUNDING_MODE))
                .impostoDeRenda(totalIr.setScale(PRECISAO_ARREDONDAMENTO, ROUNDING_MODE))
                .aliquotaImpostoRendaAplicada(aliquotaIrRestResponse.getValorAliquotaIr().multiply(BigDecimal.valueOf(100)).setScale(PRECISAO_ARREDONDAMENTO,ROUNDING_MODE))
                .iof(totalIof.setScale(PRECISAO_ARREDONDAMENTO,ROUNDING_MODE))
                .aliquotaIofAplicada(aliquotaIofRestResponse.getAliquotaIof().multiply(BigDecimal.valueOf(100)).setScale(PRECISAO_ARREDONDAMENTO,ROUNDING_MODE))
                .build();
    }

    private BigDecimal calcularValorBruto(final BigDecimal valorInvestido, final BigDecimal rendimentoDiario,
                                          final int quantidadeDiasOperacao){

        final BigDecimal resultadoPotencia = rendimentoDiario.add(BigDecimal.valueOf(1)).pow(quantidadeDiasOperacao);
        return valorInvestido.multiply(resultadoPotencia).setScale(PRECISAO_ARREDONDAMENTO, ROUNDING_MODE);
    }

    private BigDecimal calcularTotalIof(final BigDecimal rendimentoBruto, final BigDecimal aliquotaIof){
        return (rendimentoBruto.multiply(aliquotaIof));
    }

    private BigDecimal calcularTotalIr(final BigDecimal rendimentoBruto, final BigDecimal totalIof, final BigDecimal aliquotaIr){
        final BigDecimal burotDescontadoIof = rendimentoBruto.subtract(totalIof);
        return burotDescontadoIof.multiply(aliquotaIr);
    }

    private void validarDadosRecebidos(CalculoRentabilidadeRequest calculoRentabilidadeRequest){
        if(calculoRentabilidadeRequest.getDataAplicacao().compareTo(calculoRentabilidadeRequest.getDataPrevisaoResgate()) >=0 ||
           calculoRentabilidadeRequest.getValorInvestido().doubleValue()<=0D){
            throw new DadosInvalidosException();
        }
    }


}

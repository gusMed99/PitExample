package com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CalculoRentabilidadeResponse {
    private BigDecimal valorBruto;
    private BigDecimal valorLiquido;
    private BigDecimal impostoDeRenda;
    private BigDecimal iof;
}

package com.example.mutanttest.mutanttest.adapters.controller.calcularrentabilidade.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class CalcularRentabilidadeRestResponse {
    private BigDecimal valorBruto;
    private BigDecimal valorLiquido;
    private BigDecimal impostoDeRenda;
    private BigDecimal iof;
}

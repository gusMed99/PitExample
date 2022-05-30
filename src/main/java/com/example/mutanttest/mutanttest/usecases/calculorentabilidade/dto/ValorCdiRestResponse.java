package com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class ValorCdiRestResponse {

    private BigDecimal taxaDiaria;
    private BigDecimal taxaAnual;
}

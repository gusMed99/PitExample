package com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class CalculoRentabilidadeRequest {
    private BigDecimal valorInvestido;
    private BigDecimal percentualRendimentoCdi;
    private LocalDate dataAplicacao;
    private LocalDate dataPrevisaoResgate;
}

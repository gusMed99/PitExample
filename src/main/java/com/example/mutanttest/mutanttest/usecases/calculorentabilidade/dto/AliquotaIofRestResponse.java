package com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class AliquotaIofRestResponse {

    private BigDecimal aliquotaIof;
}

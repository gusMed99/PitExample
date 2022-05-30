package com.example.mutanttest.mutanttest.adapters.api.valorcdi;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.ValorCdiRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.ValorCdiRestResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValorCdiRestImpl implements ValorCdiRest {


    @Override
    public ValorCdiRestResponse obterTaxaCdi() {
        return ValorCdiRestResponse.builder()
                .taxaAnual(BigDecimal.valueOf(0.1265))
                .taxaDiaria(BigDecimal.valueOf(0.000331))
                .build();
    }
}

package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.ValorCdiRestRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.ValorCdiRestResponse;

public interface ValorCdiRest {

    ValorCdiRestResponse obterTaxaCdi();
}

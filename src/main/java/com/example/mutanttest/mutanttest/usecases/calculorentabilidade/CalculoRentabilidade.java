package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.CalculoRentabilidadeRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.CalculoRentabilidadeResponse;

public interface CalculoRentabilidade {

    CalculoRentabilidadeResponse obterCalculoRentabilidade(CalculoRentabilidadeRequest request);
}

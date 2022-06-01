package com.example.mutanttest.mutanttest.adapters.controller.calcularrentabilidade;

import com.example.mutanttest.mutanttest.adapters.controller.calcularrentabilidade.dto.CalcularRentabilidadeRestRequest;
import com.example.mutanttest.mutanttest.adapters.controller.calcularrentabilidade.dto.CalcularRentabilidadeRestResponse;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.CalculoRentabilidade;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.CalculoRentabilidadeRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.CalculoRentabilidadeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalcularRentabilidadeRestController {

    private final CalculoRentabilidade calculoRentabilidade;


    @GetMapping(value = "/api/calcularRentabilidade",
            produces = "application/json" ,
            consumes = "application/json" )
    public CalcularRentabilidadeRestResponse obterRentabilidadeInvestimento(@RequestBody CalcularRentabilidadeRestRequest request){
        final CalculoRentabilidadeResponse calculoRentabilidadeResponse = this.calculoRentabilidade.obterCalculoRentabilidade(CalculoRentabilidadeRequest.builder()
                .valorInvestido(request.getValorInvestido())
                .percentualRendimentoCdi(request.getPercentualRendimentoCdi())
                .dataAplicacao(request.getDataAplicacao())
                .dataPrevisaoResgate(request.getDataPrevisaoResgate())
                .build());
        return CalcularRentabilidadeRestResponse.builder()
                .valorBruto(calculoRentabilidadeResponse.getValorBruto())
                .valorLiquido(calculoRentabilidadeResponse.getValorLiquido())
                .impostoDeRenda(calculoRentabilidadeResponse.getImpostoDeRenda())
                .aliquotaImpostoRendaAplicada(calculoRentabilidadeResponse.getAliquotaImpostoRendaAplicada())
                .iof(calculoRentabilidadeResponse.getIof())
                .aliquotaIofAplicada(calculoRentabilidadeResponse.getAliquotaIofAplicada())
                .build();
    }
}

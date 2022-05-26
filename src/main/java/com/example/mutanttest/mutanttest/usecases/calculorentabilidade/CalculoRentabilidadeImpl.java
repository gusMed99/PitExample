package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;


import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CalculoRentabilidadeImpl implements CalculoRentabilidade {

    private final AliquotaIofRest aliquotaIofRest;
    private final AliquotaIrRest aliquotaIrRest;

    @Override
    public CalculoRentabilidadeResponse obterCalculoRentabilidade(final CalculoRentabilidadeRequest request) {
        final int quantidadeDiasOperacao = (int) ChronoUnit.DAYS.between(request.getDataAplicacao(), request.getDataPrevisaoResgate());
        final AliquotaIofRestResponse aliquotaIofRestResponse = this.aliquotaIofRest.obterAliquotaIof(AliquotaIofRestRequest.builder()
                .quantidadeDiasOperacao(quantidadeDiasOperacao)
                .build());
        final AliquotaIrRestResponse aliquotaIrRestResponse = this.aliquotaIrRest.obterAlqiuotaImpostoRenda(AliquotaIrRestRequest.builder()
                .quantidadeDiasOperacao(quantidadeDiasOperacao)
                .build());

        return null;
    }
}

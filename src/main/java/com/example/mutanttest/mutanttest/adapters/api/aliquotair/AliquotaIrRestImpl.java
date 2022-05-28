package com.example.mutanttest.mutanttest.adapters.api.aliquotair;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIrRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIrRestRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIrRestResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AliquotaIrRestImpl implements AliquotaIrRest {

    @Override
    public AliquotaIrRestResponse obterAlqiuotaImpostoRenda(AliquotaIrRestRequest aliquotaIrRestRequest) {
        final int quantidadeDiasOperacao = aliquotaIrRestRequest.getQuantidadeDiasOperacao();
        double aliquotaIrFinal;
        if(quantidadeDiasOperacao <= 180){
            aliquotaIrFinal = 0.225D;
        }
        else if(quantidadeDiasOperacao<= 360){
            aliquotaIrFinal = 0.2D;
        }
        else if(quantidadeDiasOperacao <= 720){
            aliquotaIrFinal = 0.175D;
        }
        else{
            aliquotaIrFinal = 0.15D;
        }
        return AliquotaIrRestResponse.builder()
                .valorAliquotaIr(BigDecimal.valueOf(aliquotaIrFinal))
                .build();
    }
}

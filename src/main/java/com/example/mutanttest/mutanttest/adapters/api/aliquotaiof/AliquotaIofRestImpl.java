package com.example.mutanttest.mutanttest.adapters.api.aliquotaiof;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.AliquotaIofRest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIofRestRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIofRestResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class AliquotaIofRestImpl implements AliquotaIofRest {


    @Override
    public AliquotaIofRestResponse obterAliquotaIof(final AliquotaIofRestRequest aliquotaIofRestRequest) {
        //Numa implementacao real, isso poderia ser a chamada a outro sistema que retorna as aliquotas
        if(aliquotaIofRestRequest.getQuantidadeDiasOperacao() >= 30){
            return AliquotaIofRestResponse.builder()
                    .aliquotaIof(BigDecimal.ZERO)
                    .build();
        }
        int aliquotaInicial = 100;
        int contadorPrazo;
        final Map<Integer,Integer> aliquotasPorPrazo = new HashMap<>();
        int acumuladorSubtracao = 2;
        for (contadorPrazo= 1;contadorPrazo <= 30; contadorPrazo++){
            if(acumuladorSubtracao < 2 ){
                aliquotaInicial =  aliquotaInicial - 3;
                acumuladorSubtracao++;
            }
            else{
                aliquotaInicial = aliquotaInicial - 4;
                acumuladorSubtracao = 0;
            }
            aliquotasPorPrazo.put(contadorPrazo, aliquotaInicial);
        }
        return AliquotaIofRestResponse.builder()
                .aliquotaIof(BigDecimal.valueOf(aliquotasPorPrazo.get(aliquotaIofRestRequest.getQuantidadeDiasOperacao())/100D))
                .build();

    }
}

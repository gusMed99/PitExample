package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIrRestRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIrRestResponse;

public interface AliquotaIrRest {

    AliquotaIrRestResponse obterAliquotaImpostoRenda(AliquotaIrRestRequest aliquotaIrRestRequest);

}

package com.example.mutanttest.mutanttest.usecases.calculorentabilidade;

import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIofRestRequest;
import com.example.mutanttest.mutanttest.usecases.calculorentabilidade.dto.AliquotaIofRestResponse;

public interface AliquotaIofRest {

    AliquotaIofRestResponse obterAliquotaIof(AliquotaIofRestRequest aliquotaIofRestRequest) ;
}

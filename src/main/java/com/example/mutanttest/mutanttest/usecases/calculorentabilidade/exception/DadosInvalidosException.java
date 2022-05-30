package com.example.mutanttest.mutanttest.usecases.calculorentabilidade.exception;

public class DadosInvalidosException extends RuntimeException{

    public DadosInvalidosException(){
        super("Dados recebidos na entrada invalidos");
    }
}

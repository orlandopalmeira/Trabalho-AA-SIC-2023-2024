package com.grupo6.votingapp.dtos.interfaces;

public interface DTO<E> {
    public E toEntity(); //* Obriga a implementar o método toEntity que converte um DTO na respectiva entidade
}

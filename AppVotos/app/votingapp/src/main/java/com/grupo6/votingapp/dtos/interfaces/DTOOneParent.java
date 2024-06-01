package com.grupo6.votingapp.dtos.interfaces;

public interface DTOOneParent<E,P>{
    public E toEntity(P parent);
}

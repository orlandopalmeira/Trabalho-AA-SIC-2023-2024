package com.grupo6.votingapp.dtos.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OptionStats {
    //* Dados da opção (option)
    private Long id;
    private String description;
    private String image;
    //* Quantidade de votos que a opção recebeu
    private Long count;

}

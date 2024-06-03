package com.grupo6.votingapp.dtos.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionStats {
    //* Dados da opção (option)
    private Long id;
    private String description;
    private String image;
    //* Quantidade de votos que a opção recebeu
    private Long count;

    public OptionStats(Long id, String description, String image, Long count) {
        this.id = id;
        this.description = description;
        this.image = image;
        this.count = count;
    }

}

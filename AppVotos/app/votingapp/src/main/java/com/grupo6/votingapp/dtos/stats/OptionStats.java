package com.grupo6.votingapp.dtos.stats;

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

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Long getCount() {
        return count;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

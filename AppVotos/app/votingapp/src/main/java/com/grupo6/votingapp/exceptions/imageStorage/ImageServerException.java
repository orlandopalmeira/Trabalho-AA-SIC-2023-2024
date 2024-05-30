package com.grupo6.votingapp.exceptions.imageStorage; // Replace with your actual package name

public class ImageServerException extends RuntimeException {

    /**
     * Para quando ocorrer um erro interno no servidor de imagens.
     * @param message A mensagem de erro.
     */
    public ImageServerException(String message) {
        super(message);
    }
}

package br.com.producer;

import lombok.Data;

import java.io.Serializable;

@Data
public class Mensagem implements Serializable {

    private String tipo;
    private String nome;
}

package com.joaonardi.gerenciadorocupacional.util;

import java.util.function.Function;

public class Coluna<T, V> {

    private final String nomeColuna;
    private final Function<T, V> dadosColuna;

    public Coluna(String nomeColuna, Function<T, V> dadosColuna) {
        this.nomeColuna = nomeColuna;
        this.dadosColuna = dadosColuna;
    }

    public String nomeColuna() {
        return nomeColuna;
    }

    public Function<T, V> dadosColuna() {
        return dadosColuna;
    }
}



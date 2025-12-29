package com.joaonardi.gerenciadorocupacional.util;

import java.util.function.Function;

public record Coluna<T>(
        String nomeColuna,
        Function<T, ?> dadosColuna
) {}

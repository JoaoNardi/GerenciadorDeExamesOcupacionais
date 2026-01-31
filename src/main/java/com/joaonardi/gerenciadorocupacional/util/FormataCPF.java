package com.joaonardi.gerenciadorocupacional.util;

public final class FormataCPF {

    private FormataCPF() {
        // impede instanciação
    }

    public static String inputCPF(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("\\D", "");
    }

    public static String outPutCPF(String cpf) {
        if (cpf == null) return null;

        String numeros = inputCPF(cpf);

        if (numeros.length() != 11) {
            return cpf;
        }

        return numeros.replaceFirst(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4"
        );
    }
}

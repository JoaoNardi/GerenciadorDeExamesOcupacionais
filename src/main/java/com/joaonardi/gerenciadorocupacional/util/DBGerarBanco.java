package com.joaonardi.gerenciadorocupacional.util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBGerarBanco {

    private static final String DB_ORIGINAL = "src/main/resources/_db/db_gerenciador.db";

    public static void GerarDB() {
        String sql = """
                BEGIN TRANSACTION;
                CREATE TABLE IF NOT EXISTS "certificados" (
                    "id" INTEGER NOT NULL UNIQUE,
                    "tipo_certificado_id" INTEGER NOT NULL,
                    "funcionario_id" INTEGER NOT NULL,
                    "data_emissao" DATE NOT NULL,
                    "data_validade" DATE NOT NULL,
                    "atualizado_por" INTEGER,
                    PRIMARY KEY("id" AUTOINCREMENT),
                    FOREIGN KEY("atualizado_por") REFERENCES "certificados"("id"),
                    FOREIGN KEY("funcionario_id") REFERENCES "funcionarios"("id"),
                    FOREIGN KEY("tipo_certificado_id") REFERENCES "tipos_certificado"("id")
                );
                CREATE TABLE IF NOT EXISTS "condicoes" (
                    "id" INTEGER NOT NULL UNIQUE,
                    "tipo_exame_id" INTEGER NOT NULL,
                    "referencia" VARCHAR(32) NOT NULL,
                    "operador" VARCHAR(4) NOT NULL,
                    "parametro" VARCHAR(32) NOT NULL,
                    "periodicidade" INTEGER NOT NULL,
                    PRIMARY KEY("id" AUTOINCREMENT),
                    FOREIGN KEY("tipo_exame_id") REFERENCES "tipos_exame"("id")
                );
                CREATE TABLE IF NOT EXISTS "exames" (
                    "id" INTEGER,
                    "tipo_exame_id" INTEGER NOT NULL,
                    "funcionario_id" INTEGER NOT NULL,
                    "data_emissao" DATE NOT NULL,
                    "data_validade" DATE,
                    "atualizado_por" INTEGER,
                    PRIMARY KEY("id" AUTOINCREMENT),
                    FOREIGN KEY("atualizado_por") REFERENCES "exames"("id"),
                    FOREIGN KEY("funcionario_id") REFERENCES "funcionarios"("id"),
                    FOREIGN KEY("tipo_exame_id") REFERENCES "tipos_exame"("id")
                );
                CREATE TABLE IF NOT EXISTS "funcionarios" (
                    "id" INTEGER NOT NULL UNIQUE,
                    "nome" VARCHAR(255) NOT NULL,
                    "cpf" VARCHAR(11) NOT NULL UNIQUE,
                    "data_nascimento" DATE NOT NULL,
                    "setor_id" INTEGER,
                    "data_admissao" DATE NOT NULL,
                    "ativo" BOOLEAN NOT NULL DEFAULT 1,
                    PRIMARY KEY("id" AUTOINCREMENT),
                    CONSTRAINT "fk_setor" FOREIGN KEY("setor_id") REFERENCES "setores"("id")
                );
                CREATE TABLE IF NOT EXISTS "setores" (
                    "id" INTEGER NOT NULL UNIQUE,
                    "area" VARCHAR(255) NOT NULL UNIQUE,
                    PRIMARY KEY("id" AUTOINCREMENT)
                );
                CREATE TABLE IF NOT EXISTS "tipos_certificado" (
                    "id" INTEGER UNIQUE,
                    "nome" TEXT UNIQUE,
                    "periodicidade" INTEGER NOT NULL,
                    PRIMARY KEY("id" AUTOINCREMENT)
                );
                CREATE TABLE IF NOT EXISTS "tipos_exame" (
                    "id" INTEGER,
                    "nome" TEXT NOT NULL UNIQUE,
                    "periodicidade" INTEGER,
                    PRIMARY KEY("id" AUTOINCREMENT)
                );
                COMMIT;
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_ORIGINAL);
             Statement stmt = conn.createStatement()) {

            String[] commands = sql.split(";");

            for (String command : commands) {
                command = command.trim();
                if (!command.isEmpty()) {
                    stmt.execute(command);
                }
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Banco de dados criado com sucesso!",
                    "Banco Criado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao criar banco de dados:\n" + e.getMessage(),
                    "Erro de Banco",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}



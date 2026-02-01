package com.joaonardi.gerenciadorocupacional.util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBGerarBanco {

    private static final String DB_ORIGINAL = Config.DB_FULL_PATH;

    public static void GerarDB() {

        String sql = """
                CREATE TABLE IF NOT EXISTS "setores" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "area" VARCHAR(255) NOT NULL UNIQUE
                );
                
                CREATE TABLE IF NOT EXISTS "funcionarios" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "nome" VARCHAR(255) NOT NULL,
                    "cpf" VARCHAR(11) NOT NULL UNIQUE,
                    "data_nascimento" DATE NOT NULL,
                    "setor_id" INTEGER,
                    "data_admissao" DATE NOT NULL,
                    "ativo" BOOLEAN NOT NULL DEFAULT 1,
                    FOREIGN KEY("setor_id") REFERENCES "setores"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                CREATE TABLE IF NOT EXISTS "tipos_exame" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "nome" TEXT NOT NULL UNIQUE
                );
                
                CREATE TABLE IF NOT EXISTS "conjuntos" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "tipo_exame_id" INTEGER NOT NULL,
                    "periodicidade" INTEGER NOT NULL,
                    FOREIGN KEY("tipo_exame_id") REFERENCES "tipos_exame"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                CREATE TABLE IF NOT EXISTS "condicoes" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "conjunto_id" INTEGER NOT NULL,
                    "referencia" VARCHAR(32) NOT NULL,
                    "operador" VARCHAR(4) NOT NULL,
                    "parametro" VARCHAR(32) NOT NULL,
                    FOREIGN KEY("conjunto_id") REFERENCES "conjuntos"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                CREATE TABLE IF NOT EXISTS "tipos_certificado" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "nome" TEXT UNIQUE,
                    "periodicidade" INTEGER NOT NULL
                );
                
                CREATE TABLE IF NOT EXISTS "certificados" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "tipo_certificado_id" INTEGER NOT NULL,
                    "funcionario_id" INTEGER NOT NULL,
                    "data_emissao" DATE NOT NULL,
                    "data_validade" DATE,
                    "atualizado_por" INTEGER,
                    FOREIGN KEY("atualizado_por") REFERENCES "certificados"("id") ON DELETE SET NULL ON UPDATE CASCADE,
                    FOREIGN KEY("funcionario_id") REFERENCES "funcionarios"("id") ON DELETE RESTRICT ON UPDATE RESTRICT,
                    FOREIGN KEY("tipo_certificado_id") REFERENCES "tipos_certificado"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                CREATE TABLE IF NOT EXISTS "exames" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "tipo_exame_id" INTEGER NOT NULL,
                    "funcionario_id" INTEGER NOT NULL,
                    "data_emissao" DATE NOT NULL,
                    "data_validade" DATE,
                    "atualizado_por" INTEGER,
                    FOREIGN KEY("atualizado_por") REFERENCES "exames"("id") ON DELETE SET NULL ON UPDATE CASCADE,
                    FOREIGN KEY("funcionario_id") REFERENCES "funcionarios"("id") ON DELETE RESTRICT ON UPDATE RESTRICT,
                    FOREIGN KEY("tipo_exame_id") REFERENCES "tipos_exame"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                CREATE TABLE IF NOT EXISTS "particularidades" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "nome" TEXT UNIQUE NOT NULL,
                    "descricao" TEXT UNIQUE,
                    "tipo_exame_id" INTEGER NOT NULL,
                    "periodicidade" INTEGER NOT NULL,
                    FOREIGN KEY("tipo_exame_id") REFERENCES "tipos_exame"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                CREATE TABLE IF NOT EXISTS "vinculos_particularidades" (
                    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                    "funcionario_id" INTEGER NOT NULL,
                    "particularidade_id" INTEGER NOT NULL,
                    "motivo" TEXT,
                    "data_inclusao" DATE NOT NULL,
                    "data_exclusao" DATE,
                    FOREIGN KEY("funcionario_id") REFERENCES "funcionarios"("id") ON DELETE RESTRICT ON UPDATE RESTRICT,
                    FOREIGN KEY("particularidade_id") REFERENCES "particularidades"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
                );
                
                -- indices
                
                CREATE INDEX idx_exames_funcionario_id ON exames(funcionario_id);
                CREATE INDEX idx_exames_tipo_exame_id ON exames(tipo_exame_id);
                CREATE INDEX idx_exames_atualizado_por ON exames(atualizado_por);
                CREATE INDEX idx_exames_datas ON exames(data_emissao, data_validade);
                
                CREATE INDEX idx_certificados_funcionario_id ON certificados(funcionario_id);
                CREATE INDEX idx_certificados_tipo_certificado_id ON certificados(tipo_certificado_id);
                CREATE INDEX idx_certificados_atualizado_por ON certificados(atualizado_por);
                CREATE INDEX idx_certificados_datas ON certificados(data_emissao, data_validade);
                
                CREATE INDEX idx_funcionarios_setor_id ON funcionarios(setor_id);
                CREATE INDEX idx_funcionarios_ativo ON funcionarios(ativo);
                CREATE INDEX idx_funcionarios_setor_ativo ON funcionarios(setor_id, ativo);
                
                CREATE INDEX idx_vinculos_funcionario_id ON vinculos_particularidades(funcionario_id);
                CREATE INDEX idx_vinculos_particularidade_id ON vinculos_particularidades(particularidade_id);
                CREATE INDEX idx_vinculos_datas ON vinculos_particularidades(data_inclusao, data_exclusao);
                
                CREATE INDEX idx_particularidades_tipo_exame_id ON particularidades(tipo_exame_id);
                
                CREATE INDEX idx_conjuntos_tipo_exame_id ON conjuntos(tipo_exame_id);
                
                CREATE INDEX idx_condicoes_conjunto_id ON condicoes(conjunto_id);
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_ORIGINAL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(
                    null,
                    "Banco de dados criado com sucesso!",
                    "Banco Criado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao criar banco de dados:" + e.getMessage(),
                    "Erro de Banco",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

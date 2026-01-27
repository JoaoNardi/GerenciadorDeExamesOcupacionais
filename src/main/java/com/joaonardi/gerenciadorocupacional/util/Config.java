package com.joaonardi.gerenciadorocupacional.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {

    public static final String DB_NAME = "db_gerenciador.db";
    public static final String BASE_DIR;
    public static final String DB_FULL_PATH;
    public static final String BACKUP_DIR;

    static {

        boolean isDev = false; //true or false para distribuicao com exe

        if (isDev) {
            BASE_DIR = "src/main/resources/_db";
        } else {
            BASE_DIR = System.getProperty("user.home")
                    + File.separator + "AppData"
                    + File.separator + "Roaming"
                    + File.separator + "GerenciadorOcupacional"
                    + File.separator + "database";
        }

        DB_FULL_PATH = BASE_DIR + File.separator + DB_NAME;
        BACKUP_DIR = BASE_DIR + File.separator + "backups";

        try {
            Files.createDirectories(Paths.get(BACKUP_DIR));
        } catch (IOException e) {
            System.err.println("Erro ao criar pastas do banco: " + e.getMessage());
        }
    }
}
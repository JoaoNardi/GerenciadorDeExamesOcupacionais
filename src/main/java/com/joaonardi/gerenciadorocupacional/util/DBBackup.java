package com.joaonardi.gerenciadorocupacional.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javafx.application.Platform;

public class DBBackup {

    private static final int MAX_BACKUPS = 7;

    public static void verificarOuRestaurarBanco() {
        try {
            Files.createDirectories(Paths.get(Config.BASE_DIR));
            Files.createDirectories(Paths.get(Config.BACKUP_DIR));

            File db = new File(Config.DB_FULL_PATH);

            if (!db.exists()) {
                File maisRecente = obterBackupMaisRecente();
                if (maisRecente != null) {
                    abrirJanelaRestauracao();
                } else {
                    criarBancoNovo();
                    JOptionPane.showMessageDialog(
                            null,
                            "Nenhum backup encontrado.\nUm novo banco de dados foi criado.",
                            "Banco Criado",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Config.DB_FULL_PATH)) {
                Statement stmt = conn.createStatement();
                stmt.execute("SELECT 1");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "O banco de dados com erro/corrompido.\nVocê poderá escolher um backup para restaurar.",
                        "Banco Corrompido",
                        JOptionPane.WARNING_MESSAGE
                );
                abrirJanelaRestauracao();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao verificar o banco de dados:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void criarBackupDiario() {
        try {
            Files.createDirectories(Paths.get(Config.BACKUP_DIR));

            String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String arquivoBackup = Config.BACKUP_DIR + "/backup_" + dataHoje + ".db";
            File backupFile = new File(arquivoBackup);

            if (backupFile.exists()) {
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Config.DB_FULL_PATH);
                 Statement stmt = conn.createStatement()) {
                stmt.execute("VACUUM INTO '" + arquivoBackup + "';");
            }

            limparBackupsAntigos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao acessar o banco de dados para criar backup:\n" + e.getMessage(),
                    "Erro no Backup",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro inesperado ao criar backup:\n" + e.getMessage(),
                    "Erro no Backup",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void limparBackupsAntigos() {
        try {
            List<File> backups = Files.list(Paths.get(Config.BACKUP_DIR))
                    .map(Path::toFile)
                    .filter(f -> f.getName().startsWith("backup_") && f.getName().endsWith(".db"))
                    .sorted(Comparator.comparing(File::getName).reversed())
                    .collect(Collectors.toList());

            if (backups.size() > MAX_BACKUPS) {
                List<File> antigos = backups.subList(MAX_BACKUPS, backups.size());
                for (File arq : antigos) {
                    arq.delete();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao limpar backups antigos:\n" + e.getMessage(),
                    "Erro de Backup",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
    public static void abrirJanelaRestauracao() {
        Platform.runLater(() -> {
            try {
                File pasta = new File(Config.BACKUP_DIR);
                File[] backups = pasta.listFiles((dir, name) -> name.endsWith(".db"));

                if (backups == null || backups.length == 0) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Nenhum backup encontrado.\nUm novo banco será criado automaticamente.",
                            "Sem Backup",
                            JOptionPane.WARNING_MESSAGE
                    );
                    criarBancoNovo();
                    return;
                }

                Arrays.sort(backups, Comparator.comparing(File::getName).reversed());
                List<String> nomes = Arrays.stream(backups)
                        .map(File::getName)
                        .collect(Collectors.toList());

                String selecionado = (String) JOptionPane.showInputDialog(
                        null,
                        "Selecione um backup para restaurar:",
                        "Restaurar Banco de Dados",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        nomes.toArray(),
                        nomes.get(0)
                );

                if (selecionado != null) {
                    File escolhido = new File(Config.BACKUP_DIR + "/" + selecionado);
                    restaurarBackup(escolhido);

                    JOptionPane.showMessageDialog(
                            null,
                            "Banco restaurado com sucesso!\nReinicie o programa.",
                            "Restauração Concluída",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    Platform.exit();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Nenhum backup foi selecionado.\nO programa será encerrado.",
                            "Restauração Cancelada",
                            JOptionPane.WARNING_MESSAGE
                    );
                    Platform.exit();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro ao restaurar backup:\n" + e.getMessage(),
                        "Erro de Restauração",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private static void restaurarBackup(File arquivoBackup) {
        try {
            Path origem = arquivoBackup.toPath();
            Path destino = Paths.get(Config.DB_FULL_PATH);
            Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao restaurar backup:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static File obterBackupMaisRecente() {
        File pasta = new File(Config.BACKUP_DIR);
        File[] backups = pasta.listFiles((dir, name) -> name.endsWith(".db"));
        if (backups == null || backups.length == 0) return null;

        return Arrays.stream(backups)
                .max(Comparator.comparing(File::getName))
                .orElse(null);
    }

    private static void criarBancoNovo() {
        try {
        DBGerarBanco.GerarDB();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao criar novo banco de dados:\n" + e.getMessage(),
                    "Erro de Banco",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

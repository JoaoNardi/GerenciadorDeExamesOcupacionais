package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class MainService {

    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final ExameService exameService = new ExameService();
    private final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    private final CertificadoService certificadoService = new CertificadoService();

    public void loadInicial() {
        tipoCertificadoService.carregarTiposCertificado();
        funcionarioService.listarFuncionariosPorStatus(true);
        exameService.carregarExamesVigentes();
        certificadoService.carregarCertificadosVigentes();
    }

    public Exame getExameVencido(Funcionario funcionario) {
        for (Exame exame : exameService.listarExamesVingentes()) {
            if (Objects.equals(funcionario.getId(), exame.getFuncionario().getId())) {
                int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), exame.getDataValidade());
                if (dias <= 1) {
                    return exame;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public Certificado getCetificadoVencido(Funcionario funcionario) {
        for (Certificado certificado : certificadoService.listarCertificados()) {
            if (Objects.equals(funcionario.getId(), certificado.getFuncionario().getId())) {
                int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), certificado.getDataValidade());
                if (dias <= 1) {
                    return certificado;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public String descreveTipo(Exame f) {
        return f.getTipoExame().getNome();
    }

    public String descreveTipo(Certificado f) {
        return f.getTipoCertificado().getNome();
    }

    public ObservableList<TipoDe> carregaTiposDeVigentes() {
        Set<TipoDe> tipos = new LinkedHashSet<>();
        for (Exame exame : exameService.listarExamesVingentes()) {
            tipos.add(exame.getTipoExame());
        }
        for (Certificado certificado : certificadoService.listarCertificados()) {
            tipos.add(certificado.getTipoCertificado());
        }
        return FXCollections.observableArrayList(tipos);

    }

    public ObservableList<TableColumn<LinhaFuncionario, ?>> geraColunas() {
        ObservableList<TableColumn<LinhaFuncionario, ?>> colunas = FXCollections.observableArrayList();

        for (TipoDe tipoDe : carregaTiposDeVigentes()) {
            TableColumn<LinhaFuncionario, String> colunaTipo = new TableColumn<>(tipoDe.getNome());

            TableColumn<LinhaFuncionario, LocalDate> colEmissao = new TableColumn<>("Emissão");
            colEmissao.setCellValueFactory(cell -> {
                Tipo obj = cell.getValue().getTipoPor(tipoDe);
                if (obj == null) return new SimpleObjectProperty<>(null);

                if (obj instanceof Exame exame) {
                    return new SimpleObjectProperty<>(exame.getDataEmissao());
                }
                if (obj instanceof Certificado cert) {
                    return new SimpleObjectProperty<>(cert.getDataEmissao());
                }
                return new SimpleObjectProperty<>(null);
            });
            colEmissao.setStyle("-fx-alignment: CENTER;");

            TableColumn<LinhaFuncionario, LocalDate> colValidade = new TableColumn<>("Validade");
            colValidade.setCellValueFactory(cell -> {
                Tipo obj = cell.getValue().getTipoPor(tipoDe);
                if (obj == null) return new SimpleObjectProperty<>(null);

                if (obj instanceof Exame exame) {
                    return new SimpleObjectProperty<>(exame.getDataValidade());
                }
                if (obj instanceof Certificado cert) {
                    return new SimpleObjectProperty<>(cert.getDataValidade());
                }
                return new SimpleObjectProperty<>(null);
            });
            colValidade.setStyle("-fx-alignment: CENTER;");

            TableColumn<LinhaFuncionario, String> colTempo = new TableColumn<>("T Restante");
            colTempo.setCellValueFactory(cell -> {
                Tipo obj = cell.getValue().getTipoPor(tipoDe);
                LocalDate validade = null;
                if (obj == null) return new SimpleObjectProperty<>(null);

                if (obj instanceof Exame exame &&
                        exame.getTipoExame().equals(tipoDe)) {
                    validade = exame.getDataValidade();
                }

                if (obj instanceof Certificado cert &&
                        cert.getTipoCertificado().equals(tipoDe)) {
                    validade = cert.getDataValidade();
                }
                long dias = 0;
                if (validade == null) {
                    return new SimpleStringProperty("");
                }
                dias = ChronoUnit.DAYS.between(LocalDate.now(), validade);


                return new SimpleStringProperty(dias + " dias");
            });
            colTempo.setStyle("-fx-alignment: CENTER;");
            colunaTipo.getColumns().addAll(colEmissao, colValidade, colTempo);
            colunas.add(colunaTipo);


            TableColumn<LinhaFuncionario, String> colunaVazia = new TableColumn<>();
            colunaVazia.setPrefWidth(50);
            colunas.add(colunaVazia);
        }

        return colunas;
    }

}

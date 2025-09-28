package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.CertificadoDAO;
import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CertificadoService {
    CertificadoDAO certificadoDAO = new CertificadoDAO();
    List<Certificado> certificadosList = FXCollections.observableArrayList();

    public void carregarCertificadosVigentes(){
        certificadosList = certificadoDAO.listarCertificadosVigentes(true);
    }

    public LocalDate calcularValidade(LocalDate dataEmissao, TipoCertificado tipoCertificado){
        LocalDate dataValidade;
        if (tipoCertificado.getPeriodicidade().equals(0)) {
            return null;
        }
        dataValidade = dataEmissao.plusMonths(tipoCertificado.getPeriodicidade());
        return dataValidade;
    }

    public Certificado cadastrarCertificado(Certificado certificado) {
        Certificado certificadoCadastrado = certificadoDAO.cadastrarCertificado(certificado);
        listarCertificados();
       return certificadoCadastrado;
    }

    public ObservableList<Certificado> listarCertificados(){
        return certificadoDAO.listarCertificadosVigentes(true);
    }

    public ObservableList<Certificado> listarCertificadosPorVencimento(int diasVencimento) {
        LocalDate hoje = LocalDate.now();
        if (diasVencimento == 183) {
            return FXCollections.observableArrayList(certificadosList);
        } else {
            List<Certificado> list2 = certificadosList.stream()
                    .filter(f -> {
                        LocalDate validade = f.getDataValidade();
                        if (validade == null) {
                            return false;
                        }
                        int dias = (int) ChronoUnit.DAYS.between(hoje, validade);

                        if (diasVencimento == 0) {
                            return dias <= 0;
                        } else if (diasVencimento == 7) {
                            return dias > 0 && dias <= 7;
                        } else if (diasVencimento == 30) {
                            return dias > 7 && dias <= 30;
                        } else if (diasVencimento == 182) {
                            return dias > 30 && dias <= 182;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return FXCollections.observableArrayList(list2);
        }
    }

    public void editarCertificado(Certificado certificado) {
        certificadoDAO.alterarCertificado(certificado);
        listarCertificados();
    }

    public void deletarCertificado(Certificado certificado) {
        certificadoDAO.deletarCertificado(certificado.getId());
        listarCertificados();
    }
}

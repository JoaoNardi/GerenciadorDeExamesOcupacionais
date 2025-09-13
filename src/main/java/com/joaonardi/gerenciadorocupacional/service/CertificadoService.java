package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.CertificadoDAO;
import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;

import java.time.LocalDate;

public class CertificadoService {
    CertificadoDAO certificadoDAO = new CertificadoDAO();
    public LocalDate calcularValidade(LocalDate dataEmissao, TipoCertificado tipoCertificado){
        LocalDate dataValidade;
        if (tipoCertificado.getPeriodicidade().equals(0)) {
            return null;
        }
        dataValidade = dataEmissao.plusMonths(tipoCertificado.getPeriodicidade());
        return dataValidade;
    }

    public void cadastrarCertificado(Certificado certificado) {
        certificadoDAO.cadastrarCertificado(certificado);
    }
}

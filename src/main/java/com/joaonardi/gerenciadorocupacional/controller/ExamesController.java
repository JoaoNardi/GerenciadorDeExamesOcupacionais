package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.ExameService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ExamesController {
    public ComboBox<Funcionario> inputFuncionario;
    public ComboBox<TipoExame> inputTipoExame;
    public DatePicker inputDataEmissao;
    public DatePicker inputDataValidade;
    public Button btnSalvar;
    public Button btnCancelar;
    Janela janela = new Janela();
    FuncionarioService funcionarioService = new FuncionarioService();
    TipoExameService tipoExameService = new TipoExameService();
    ExameService exameService = new ExameService();
    Exame exame;
    @FXML
    private void initialize() throws Exception {
        ObservableList<TipoExame> exames = tipoExameService.carregarTiposExame();
        ObservableList<Funcionario> funcionarios = funcionarioService.carregarFuncionarios(true);
        inputFuncionario.setItems(funcionarios);
        inputFuncionario.setConverter(new StringConverter<Funcionario>() {
            @Override
            public String toString(Funcionario funcionario) {
                return funcionario != null ? funcionario.getNome() + " - " + SetorCache.getSetorMapeado(funcionario.getSetor()): "" ;
            }

            @Override
            public Funcionario fromString(String s) {
                for (Funcionario f : inputFuncionario.getItems()){
                    String funcionario = f.getNome() + " - " + SetorCache.getSetorMapeado(f.getSetor());
                    if (funcionario.equalsIgnoreCase(s)) {
                        return f;
                    }
                }
                return null;
            }
        });
        inputTipoExame.setItems(exames);
        inputTipoExame.setConverter(new StringConverter<TipoExame>() {
            @Override
            public String toString(TipoExame tipoExame) {
                return tipoExame != null ? tipoExame.getNome() + " - " + tipoExame.getPeriodicidade() + " Dias" : "" ;
            }

            @Override
            public TipoExame fromString(String s) {
                for (TipoExame t : inputTipoExame.getItems()){
                    String tipoExameString = t.getNome() + " - " + t.getPeriodicidade() + " Dias";
                    if (tipoExameString.equalsIgnoreCase(s)) {
                        return t;
                    }
                }
                return null;
            }
        });
        inputTipoExame.setValue(exames.getFirst());
        inputDataEmissao.setValue(LocalDate.now());
        inputDataValidade.setValue(exameService.calcularValidadeExame(inputDataEmissao.getValue(), inputTipoExame.getValue()));
    }

    public void handleSalvarExame(ActionEvent event) {
        this.exame = Exame.ExameBuilder.builder()
                .idTipoExame(inputTipoExame.getValue().getId())
                .idFuncionario(inputFuncionario.getValue().getId())
                .dataEmissao(inputDataEmissao.getValue())
                .dataValidade(inputDataValidade.getValue())
                .atualizadoPor(null)
                .build();
        exameService.lancarExame(exame);
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnCancelar);
    }

    public void validadeAlteracao(ActionEvent event) {
        inputDataValidade.setValue(exameService.calcularValidadeExame(inputDataEmissao.getValue(), inputTipoExame.getValue()));

    }
}

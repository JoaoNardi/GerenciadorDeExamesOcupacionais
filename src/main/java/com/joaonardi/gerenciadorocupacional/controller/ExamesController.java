package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
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
    Exame exame = null;

    @FXML
    private void initialize() throws Exception {
        ObservableList<TipoExame> exames = tipoExameService.listarTiposExame();
        FuncionarioCache.carregarFuncionarios(true);
        ObservableList<Funcionario> funcionarios = FuncionarioCache.todosFuncionarios;
        inputFuncionario.setItems(funcionarios);
        inputFuncionario.setConverter(new StringConverter<Funcionario>() {
            @Override
            public String toString(Funcionario funcionario) {
                return funcionario != null ? funcionario.getNome() + " - " + SetorCache.getSetorMapeado(funcionario.getIdSetor()) : "";
            }

            @Override
            public Funcionario fromString(String s) {
                for (Funcionario f : inputFuncionario.getItems()) {
                    String funcionario = f.getNome() + " - " + SetorCache.getSetorMapeado(f.getIdSetor());
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
                return tipoExame != null ? tipoExame.getNome() + " - " + tipoExame.getPeriodicidade() + " Meses" : "";
            }

            @Override
            public TipoExame fromString(String s) {
                for (TipoExame t : inputTipoExame.getItems()) {
                    String tipoExameString = t.getNome() + " - " + t.getPeriodicidade() + " Meses";
                    if (tipoExameString.equalsIgnoreCase(s)) {
                        return t;
                    }
                }
                return null;
            }
        });
        inputTipoExame.setValue(exames.getFirst());
        inputDataEmissao.setValue(LocalDate.now());
        inputDataValidade.setValue(exameService.calcularValidadeExame(inputFuncionario.getValue(), inputDataEmissao.getValue(), inputTipoExame.getValue()));
    }

    public void handleSalvarExame(ActionEvent event) {
        if (this.exame == null) {
            this.exame = Exame.ExameBuilder.builder()
                    .id(null)
                    .idTipoExame(inputTipoExame.getValue().getId())
                    .idFuncionario(inputFuncionario.getValue().getId())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            exameService.lancarExame(exame);
        }
        if (this.exame != null || this.exame.getId() != null || this.exame.getAtualizadoPor() == null) { // editar exame que nao é nao atualizado ainda
            Exame exame1 = Exame.ExameBuilder.builder()
                    .id(this.exame.getId())
                    .idTipoExame(inputTipoExame.getValue().getId())
                    .idFuncionario(inputFuncionario.getValue().getId())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            exameService.editarExame(exame1);
        }
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnCancelar);
    }

    public void validadeAlteracao(ActionEvent event) {
        inputDataValidade.setValue(exameService.calcularValidadeExame(inputFuncionario.getValue(), inputDataEmissao.getValue(),
                inputTipoExame.getValue()));

    }

    public void setExame(Exame exameSelecionado) {

        this.exame = exameSelecionado;
        if (exame != null) {
            inputFuncionario.setValue(funcionarioService.getFuncionarioMapeadoPorId(exame.getIdFuncionario()));
            inputTipoExame.setValue(tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame()));
            inputDataEmissao.setValue(exameSelecionado.getDataEmissao());
            inputDataValidade.setValue(exameSelecionado.getDataEmissao());

        }
    }
}

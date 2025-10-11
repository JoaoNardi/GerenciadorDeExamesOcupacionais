package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.RelatorioDAO;
import com.joaonardi.gerenciadorocupacional.model.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.VerticalPositionMark;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RelatorioService {
    DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private RelatorioDAO relatorioDAO = new RelatorioDAO();
    private SetorService setorService = new SetorService();
    private ObservableList<RelatorioItem> relatorioItemsLista = FXCollections.observableArrayList();
    private TipoExameService tipoExameService = new TipoExameService();
    private TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();

    public void carregarRelatorio(Funcionario funcionario, String inputData, LocalDate dataInicial, LocalDate dataFinal, TipoDe tipoDe, boolean exame,
                                  boolean certificado) {
        relatorioItemsLista = relatorioDAO.montarRelatorio(funcionario, inputData, dataInicial, dataFinal, tipoDe, exame, certificado);
    }

    public ObservableList<RelatorioItem> listarRelatorio() {
        return relatorioItemsLista;
    }


    public void gerarPDF(Stage stage, ObservableList<RelatorioItem> relatorioLista,
                         Funcionario funcionario, TableView<RelatorioItem> tableView, LocalDate dataInicial, LocalDate dataFinal, boolean inputExame,
                         boolean inputCertificado, TipoDe tipoDe) {
        setorService.carregarSetores();
        tipoCertificadoService.carregarTiposCertificado();
        tipoExameService.carregarTipoExames();

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar relatório PDF");

            String userHome = System.getProperty("user.home");
            File desktopDir = new File(userHome, "Desktop");
            File areaDeTrabalhoDir = new File(userHome, "Área de Trabalho");

            if (desktopDir.exists()) {
                fileChooser.setInitialDirectory(desktopDir);
            } else if (areaDeTrabalhoDir.exists()) {
                fileChooser.setInitialDirectory(areaDeTrabalhoDir);
            } else {
                fileChooser.setInitialDirectory(new File(userHome));
            }

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivos PDF (*.pdf)", "*.pdf")
            );
            fileChooser.setInitialFileName("relatorioPorFuncionario.pdf");

            File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Paragraph titulo = new Paragraph("Relatório por Funcionário");
            titulo.add(new Chunk(new VerticalPositionMark()));
            titulo.add("Data de geração: " + LocalDate.now().format(formatoData));
            document.add(titulo);
            document.add(new Paragraph("Nome: " + funcionario.getNome() + " - " + "Setor: " + setorService.getSetorMapeado(funcionario.getIdSetor())));
            document.add(new Paragraph(" "));
            Paragraph filtros = new Paragraph("Periodo: " + dataInicial.format(formatoData) + " á " + dataFinal.format(formatoData) + " - ");
            document.add(filtros);
            if (tipoDe == null) {
                filtros.add("Todos");
            } else {
                filtros.add(tipoDe.getNome());
            }
            if (inputExame && !inputCertificado && tipoDe == null) {
                filtros.add(" - Exames - ");
            }
            if (inputCertificado && !inputExame && tipoDe == null) {
                filtros.add(" - Certificados - ");
            }

            document.add(new Paragraph(" "));

            int colunas = 0;
            for (TableColumn<?, ?> col : tableView.getColumns()) {
                if (col.isVisible()) colunas++;
            }

            PdfPTable tabela = new PdfPTable(colunas);
            tabela.setWidthPercentage(100);

            for (TableColumn<RelatorioItem, ?> col : tableView.getColumns()) {
                if (col.isVisible()) {
                    PdfPCell cell = new PdfPCell(new Phrase(col.getText()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabela.addCell(cell);
                }
            }

            for (RelatorioItem item : relatorioLista) {
                for (TableColumn<RelatorioItem, ?> col : tableView.getColumns()) {
                    if (col.isVisible()) {
                        Object value = col.getCellObservableValue(item) != null
                                ? col.getCellObservableValue(item).getValue()
                                : null;
                        String textoCelula;
                        if (value instanceof LocalDate) {
                            textoCelula = ((LocalDate) value).format(formatoData);
                        } else if (value != null) {
                            textoCelula = value.toString();
                        } else {
                            textoCelula = "";
                        }
                        tabela.addCell(textoCelula);
                    }
                }
            }
            document.add(tabela);
            document.close();
            Notifications.create().title("Sucesso")
                    .text( " ✅ Clique aqui para abrir! " + "\n"  + " PDF salvo em: " + file.getAbsolutePath()).owner(stage).hideAfter(Duration.seconds(5)).onAction(event -> {
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).showInformation();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}

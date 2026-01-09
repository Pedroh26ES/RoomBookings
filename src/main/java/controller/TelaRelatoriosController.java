package controller;

import view.TelaRelatoriosJFrame;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Controlador específico da tela de relatórios.
 * Responsável por capturar eventos da UI (TelaRelatoriosJFrame), validar inputs do utilizador
 * e formatar os resultados obtidos do RelatorioController para exibição.
 */
public class TelaRelatoriosController {
    private TelaRelatoriosJFrame view;
    private RelatorioController relatorioController;
    
    private DateTimeFormatter uiDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter uiDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TelaRelatoriosController(TelaRelatoriosJFrame view, RelatorioController relatorioController) { 
        this.view = view;
        this.relatorioController = relatorioController; 
        
        this.view.addListenerTotalArrecadado(this::gerarRelatorioTotalArrecadado);
        this.view.addListenerSalasMaisReservadas(this::gerarRelatorioSalasMaisReservadas);
        this.view.addListenerMediaHorasPorCliente(this::gerarRelatorioMediaHorasPorCliente);
        this.view.addListenerReceitaPorTipo(this::gerarRelatorioReceitaPorTipo);
        this.view.addListenerTaxaOcupacao(this::gerarRelatorioTaxaOcupacao);
        this.view.addListenerClientesAtivos(this::gerarRelatorioClientesAtivos);
        this.view.addListenerClientesInativos(this::gerarRelatorioClientesInativos);
        this.view.addListenerResumoFinanceiro(this::gerarRelatorioResumoFinanceiro);
    }
    

    private void gerarRelatorioTotalArrecadado(ActionEvent e) {
        try {
            LocalDateTime dataInicio = obterDataInicio();
            LocalDateTime dataFim = obterDataFim();
            
            if (dataInicio == null || dataFim == null) return; 
            
            double totalArrecadado = relatorioController.calcularTotalArrecadadoPorPeriodo(dataInicio, dataFim);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== RELATÓRIO: TOTAL ARRECADADO POR PERÍODO ===\n\n");
            resultado.append("Período: ").append(dataInicio.toLocalDate().format(uiDateFormatter))
                     .append(" até ").append(dataFim.toLocalDate().format(uiDateFormatter)).append("\n");
            resultado.append("Total arrecadado: R$ ").append(String.format("%.2f", totalArrecadado)).append("\n");
            
            view.setAreaResultados(resultado.toString());
        } catch (Exception ex) {
            tratarErro(ex);
        }
    }
    

    private void tratarErro(Exception ex) {
        JOptionPane.showMessageDialog(view, "Erro ao processar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    
    private LocalDateTime obterDataInicio() {
        try {
            String dataStr = view.getDataInicio();
            if (dataStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Por favor, informe a data inicial.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (dataStr.length() > 10 && dataStr.contains(":")) { 
                 return LocalDateTime.parse(dataStr, uiDateTimeFormatter);
            } else {
                 return LocalDate.parse(dataStr, uiDateFormatter).atStartOfDay(); 
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view, "Formato de data inicial inválido. Use DD/MM/AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private LocalDateTime obterDataFim() {
        try {
            String dataStr = view.getDataFim();
            if (dataStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Por favor, informe a data final.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (dataStr.length() > 10 && dataStr.contains(":")) { 
                 return LocalDateTime.parse(dataStr, uiDateTimeFormatter);
            } else {
                 return LocalDate.parse(dataStr, uiDateFormatter).atTime(23, 59, 59); 
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view, "Formato de data final inválido. Use DD/MM/AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    

    private void gerarRelatorioSalasMaisReservadas(ActionEvent e) {
        try {
            String dataInicioStr = view.getDataInicio();
            if (dataInicioStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Informe data início para identificar o mês.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate dataInicio = LocalDate.parse(dataInicioStr, uiDateFormatter);
            Map<String, Integer> salas = relatorioController.obterSalasMaisReservadasNoMes(dataInicio.getYear(), dataInicio.getMonthValue());
            
            StringBuilder sb = new StringBuilder("=== RELATÓRIO: SALAS MAIS RESERVADAS ===\n\n");
            salas.forEach((k, v) -> sb.append(k).append(": ").append(v).append(" reservas\n"));
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { tratarErro(ex); }
    }

    private void gerarRelatorioMediaHorasPorCliente(ActionEvent e) {
        try {
            Map<String, Double> dados = relatorioController.calcularMediaHorasPorCliente();
            StringBuilder sb = new StringBuilder("=== RELATÓRIO: MÉDIA HORAS/CLIENTE ===\n\n");
            dados.forEach((k, v) -> sb.append(k).append(": ").append(String.format("%.2f", v)).append(" horas\n"));
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { tratarErro(ex); }
    }

    private void gerarRelatorioReceitaPorTipo(ActionEvent e) {
        try {
            LocalDateTime ini = obterDataInicio(); LocalDateTime fim = obterDataFim();
            if(ini==null || fim==null) return;
            Map<String, Double> dados = relatorioController.obterReceitaPorTipoSala(ini, fim);
            StringBuilder sb = new StringBuilder("=== RECEITA POR TIPO ===\n\n");
            dados.forEach((k, v) -> sb.append(k).append(": R$ ").append(String.format("%.2f", v)).append("\n"));
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { tratarErro(ex); }
    }

    private void gerarRelatorioTaxaOcupacao(ActionEvent e) {
        try {
            LocalDateTime ini = obterDataInicio(); LocalDateTime fim = obterDataFim();
            if(ini==null || fim==null) return;
            Map<String, Double> dados = relatorioController.calcularTaxaOcupacaoSalas(ini, fim);
            StringBuilder sb = new StringBuilder("=== TAXA DE OCUPAÇÃO ===\n\n");
            dados.forEach((k, v) -> sb.append(k).append(": ").append(String.format("%.2f", v)).append("%\n"));
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { tratarErro(ex); }
    }

    private void gerarRelatorioClientesAtivos(ActionEvent e) {
        try {
            Map<String, Integer> dados = relatorioController.obterClientesMaisAtivos();
            StringBuilder sb = new StringBuilder("=== CLIENTES MAIS ATIVOS ===\n\n");
            dados.forEach((k, v) -> sb.append(k).append(": ").append(v).append(" reservas\n"));
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { tratarErro(ex); }
    }

    private void gerarRelatorioClientesInativos(ActionEvent e) {
        try {
            List<String> inativos = relatorioController.obterClientesInativos();
            
            StringBuilder sb = new StringBuilder("=== RELATÓRIO: CLIENTES INATIVOS ===\n");
            sb.append("(Clientes sem reservas ativas ou concluídas)\n\n");
            
            if (inativos.isEmpty()) {
                sb.append("Nenhum cliente inativo encontrado.");
            } else {
                sb.append("Total encontrado: ").append(inativos.size()).append("\n\n");
                for (String clienteStr : inativos) {
                    sb.append(clienteStr).append("\n");
                }
            }
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { 
            tratarErro(ex); 
        }
    }

    private void gerarRelatorioResumoFinanceiro(ActionEvent e) {
        try {
            LocalDateTime ini = obterDataInicio(); LocalDateTime fim = obterDataFim();
            if(ini==null || fim==null) return;
            Map<String, Object> resumo = relatorioController.obterResumoFinanceiro(ini, fim);
            StringBuilder sb = new StringBuilder("=== RESUMO FINANCEIRO ===\n\n");
            resumo.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
            view.setAreaResultados(sb.toString());
        } catch (Exception ex) { tratarErro(ex); }
    }
}
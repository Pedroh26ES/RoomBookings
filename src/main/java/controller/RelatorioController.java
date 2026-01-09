package controller;

import dao.RelatorioDAO;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * responsável pela geração de indicadores e estatísticas do sistema.
 */
public class RelatorioController {
    private RelatorioDAO relatorioDAO;

    public RelatorioController(RelatorioDAO relatorioDAO) { 
        this.relatorioDAO = relatorioDAO;
    }

    public double calcularTotalArrecadadoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioDAO.calcularTotalArrecadadoPorPeriodo(inicio, fim);
    }

    public java.util.List<String> obterClientesInativos() {
        return relatorioDAO.obterClientesInativos();
    }
    
    /**
     * retorna um mapa contendo as salas e suas respectivas quantidades de reserva em um mês específico.
     * Útil para identificar espaços ociosos ou sobrecarregados.
     */
    public Map<String, Integer> obterSalasMaisReservadasNoMes(int ano, int mes) {
        return relatorioDAO.obterSalasMaisReservadasNoMes(ano, mes);
    }

    public Map<String, Double> calcularMediaHorasPorCliente() {
        return relatorioDAO.calcularMediaHorasPorCliente();
    }

    public Map<String, Double> obterReceitaPorTipoSala(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioDAO.obterReceitaPorTipoSala(inicio, fim);
    }

    /**
     * CALcula a porcentagem de ocupação de cada sala dentro de um período.
     */
    public Map<String, Double> calcularTaxaOcupacaoSalas(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioDAO.calcularTaxaOcupacaoSalas(inicio, fim);
    }

    public Map<String, Integer> obterClientesMaisAtivos() {
        return relatorioDAO.obterClientesMaisAtivos();
    }

    /**
     * Gera um dashboard financeiro.
     * Inclui Ticket Médio, Receita Total e Taxa de Cancelamento.
     */
    public Map<String, Object> obterResumoFinanceiro(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioDAO.obterResumoFinanceiro(inicio, fim);
    }
}
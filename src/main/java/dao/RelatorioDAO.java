package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class RelatorioDAO {

    /**
     * Relatório 1: Total arrecadado por período
     */
    public double calcularTotalArrecadadoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COALESCE(SUM(custo_calculado), 0) as total " +
                "FROM reservas " +
                "WHERE status_reserva = 'Ativa' " +
                "AND data_inicio >= ? AND data_fim <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular total arrecadado: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Relatório: Clientes Inativos (sem reservas ativas ou concluídas).
     * @return Lista de Strings formatadas "Nome (CPF: ...)"
     */
    public List<String> obterClientesInativos() {
        List<String> resultado = new ArrayList<>();
        
        // Seleciona clientes que NÃO possuem reservas ativas ou concluídas
        String sql = "SELECT c.nome, c.cpf " +
                     "FROM clientes c " +
                     "LEFT JOIN reservas r ON c.id = r.cliente_id " +
                     "AND r.status_reserva IN ('Ativa', 'Concluida') " +
                     "WHERE r.id IS NULL " +
                     "ORDER BY c.nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                // Formatação solicitada: Nome e CPF
                resultado.add(nome + " (CPF: " + cpf + ")");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter clientes inativos: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }
    
    /**
     * Relatório 2: Salas mais reservadas no mês
     */
    public Map<String, Integer> obterSalasMaisReservadasNoMes(int ano, int mes) {
        Map<String, Integer> resultado = new LinkedHashMap<>();

        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime inicioDeMes = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fimDeMes = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        String sql = "SELECT s.codigo, COUNT(r.id) as total_reservas " +
                "FROM salas s " +
                "INNER JOIN reservas r ON s.id = r.sala_id " +
                "WHERE r.data_inicio >= ? AND r.data_inicio <= ? " +
                "AND r.status_reserva IN ('Ativa', 'Concluida') " +
                "GROUP BY s.codigo " +
                "ORDER BY total_reservas DESC, s.codigo";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicioDeMes));
            stmt.setTimestamp(2, Timestamp.valueOf(fimDeMes));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String codigoSala = rs.getString("codigo");
                int totalReservas = rs.getInt("total_reservas");
                resultado.put(codigoSala, totalReservas);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter salas mais reservadas: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Relatório 3: Média de horas reservadas por cliente
     */
    public Map<String, Double> calcularMediaHorasPorCliente() {
        Map<String, Double> resultado = new LinkedHashMap<>();

        String sql = "SELECT c.nome, c.cpf, " +
                "AVG(TIMESTAMPDIFF(HOUR, r.data_inicio, r.data_fim)) as media_horas " +
                "FROM clientes c " +
                "INNER JOIN reservas r ON c.id = r.cliente_id " +
                "WHERE r.status_reserva IN ('Ativa', 'Concluida') " +
                "GROUP BY c.nome, c.cpf " +
                "ORDER BY media_horas DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                double mediaHoras = rs.getDouble("media_horas");

                String chave = nome + " (CPF: " + cpf + ")";
                resultado.put(chave, mediaHoras);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular média de horas por cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Relatório 4: Receita por tipo de sala no período
     */
    public Map<String, Double> obterReceitaPorTipoSala(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, Double> resultado = new LinkedHashMap<>();

        String sql = "SELECT ts.nome as tipo_sala, " +
                "COALESCE(SUM(r.custo_calculado), 0) as receita_total " +
                "FROM tipo_sala ts " + 
                "LEFT JOIN salas s ON ts.id = s.tipo_sala_id " +
                "LEFT JOIN reservas r ON s.id = r.sala_id " +
                "    AND r.data_inicio >= ? AND r.data_fim <= ? " +
                "    AND r.status_reserva = 'Ativa' " +
                "GROUP BY ts.nome " +
                "ORDER BY receita_total DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tipoSala = rs.getString("tipo_sala");
                double receita = rs.getDouble("receita_total");
                resultado.put(tipoSala, receita);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter receita por tipo de sala: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Relatório 5: Taxa de ocupação das salas no período
     */
    public Map<String, Double> calcularTaxaOcupacaoSalas(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, Double> resultado = new LinkedHashMap<>();

        String sql = "SELECT s.codigo, " +
                "COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.data_inicio, r.data_fim)), 0) as horas_ocupadas, " +
                "TIMESTAMPDIFF(HOUR, ?, ?) as horas_periodo " +
                "FROM salas s " +
                "LEFT JOIN reservas r ON s.id = r.sala_id " +
                "    AND r.data_inicio >= ? AND r.data_fim <= ? " +
                "    AND r.status_reserva IN ('Ativa', 'Concluida') " +
                "GROUP BY s.codigo " +
                "ORDER BY s.codigo";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));
            stmt.setTimestamp(3, Timestamp.valueOf(inicio));
            stmt.setTimestamp(4, Timestamp.valueOf(fim));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String codigo = rs.getString("codigo");
                int horasOcupadas = rs.getInt("horas_ocupadas");
                int horasPeriodo = rs.getInt("horas_periodo");

                double taxaOcupacao = horasPeriodo > 0 ? (double) horasOcupadas / horasPeriodo * 100 : 0.0;

                resultado.put(codigo, taxaOcupacao);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular taxa de ocupação: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Relatório 6: Clientes mais ativos (por número de reservas)
     */
    public Map<String, Integer> obterClientesMaisAtivos() {
        Map<String, Integer> resultado = new LinkedHashMap<>();

        String sql = "SELECT c.nome, c.cpf, COUNT(r.id) as total_reservas " +
                "FROM clientes c " +
                "INNER JOIN reservas r ON c.id = r.cliente_id " +
                "WHERE r.status_reserva IN ('Ativa', 'Concluida') " +
                "GROUP BY c.nome, c.cpf " +
                "ORDER BY total_reservas DESC, c.nome";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                int totalReservas = rs.getInt("total_reservas");

                String chave = nome + " (CPF: " + cpf + ")";
                resultado.put(chave, totalReservas);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter clientes mais ativos: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Relatório 7: Resumo financeiro do período
     */
    public Map<String, Object> obterResumoFinanceiro(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, Object> resumo = new HashMap<>();

        String sql = "SELECT " +
                "COUNT(r.id) as total_reservas, " +
                "COALESCE(SUM(r.custo_calculado), 0) as receita_total, " +
                "COALESCE(AVG(r.custo_calculado), 0) as ticket_medio, " +
                "COUNT(CASE WHEN r.status_reserva = 'Cancelada' THEN 1 END) as reservas_canceladas " +
                "FROM reservas r " +
                "WHERE r.data_inicio >= ? AND r.data_fim <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                resumo.put("totalReservas", rs.getInt("total_reservas"));
                resumo.put("receitaTotal", rs.getDouble("receita_total"));
                resumo.put("ticketMedio", rs.getDouble("ticket_medio"));
                resumo.put("reservasCanceladas", rs.getInt("reservas_canceladas"));

                int totalReservas = rs.getInt("total_reservas");
                int canceladas = rs.getInt("reservas_canceladas");
                double taxaCancelamento = totalReservas > 0 ? (double) canceladas / totalReservas * 100 : 0.0;
                resumo.put("taxaCancelamento", taxaCancelamento);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter resumo financeiro: " + e.getMessage());
            e.printStackTrace();
        }

        return resumo;
    }
}
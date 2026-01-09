package dao;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável pela persistência e recuperação de reservas.
 */
public class ReservaDAO {

    /**
     * Carrega todas as reservas ativas ou concluídas.
     */
    public List<Reserva> carregarReservas(List<Sala> salas, List<Cliente> clientes) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.data_inicio, r.data_fim, r.custo_calculado, " +
                "s.id as sala_id, s.codigo as sala_codigo, ts.nome as sala_tipo, s.capacidade as sala_capacidade, " +
                "c.nome as cliente_nome, c.cpf as cliente_cpf, c.corporativo as cliente_corporativo " +
                "FROM reservas r " +
                "INNER JOIN salas s ON r.sala_id = s.id " +
                "INNER JOIN tipo_sala ts ON s.tipo_sala_id = ts.id " + 
                "INNER JOIN clientes c ON r.cliente_id = c.id " +
                "WHERE r.status_reserva IN ('Ativa', 'Concluida') " +
                "ORDER BY r.data_inicio";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDateTime inicio = rs.getTimestamp("data_inicio").toLocalDateTime();
                LocalDateTime fim = rs.getTimestamp("data_fim").toLocalDateTime();

                int salaId = rs.getInt("sala_id");
                String salaCodigo = rs.getString("sala_codigo");
                String salaTipo = rs.getString("sala_tipo");
                int salaCapacidade = rs.getInt("sala_capacidade");

                boolean quadroBranco = false;
                if ("Standard".equals(salaTipo)) {
                    quadroBranco = temQuadroBranco(conn, salaId);
                }
                Sala sala = criarSala(salaTipo, salaCodigo, salaCapacidade, quadroBranco);

                String clienteNome = rs.getString("cliente_nome");
                String clienteCpf = rs.getString("cliente_cpf");
                boolean clienteCorporativo = rs.getBoolean("cliente_corporativo");
                Cliente cliente = new Cliente(clienteNome, clienteCpf, clienteCorporativo);

                if (sala != null && cliente != null) {
                    reservas.add(new Reserva(sala, cliente, inicio, fim));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar reservas: " + e.getMessage());
            e.printStackTrace();
        }

        return reservas;
    }

    public List<Reserva> buscarReservasPorCliente(String cpfCliente) {
        List<Reserva> reservas = new ArrayList<>();

        String sql = "SELECT r.data_inicio, r.data_fim, r.custo_calculado, " +
                "s.id as sala_id, s.codigo as sala_codigo, ts.nome as sala_tipo, s.capacidade as sala_capacidade, " +
                "c.nome as cliente_nome, c.cpf as cliente_cpf, c.corporativo as cliente_corporativo " +
                "FROM reservas r " +
                "INNER JOIN salas s ON r.sala_id = s.id " +
                "INNER JOIN tipo_sala ts ON s.tipo_sala_id = ts.id " + 
                "INNER JOIN clientes c ON r.cliente_id = c.id " +
                "WHERE c.cpf = ? AND r.status_reserva IN ('Ativa', 'Concluida') " +
                "ORDER BY r.data_inicio";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime inicioReserva = rs.getTimestamp("data_inicio").toLocalDateTime();
                    LocalDateTime fimReserva = rs.getTimestamp("data_fim").toLocalDateTime();

                    int salaId = rs.getInt("sala_id");
                    String salaCodigo = rs.getString("sala_codigo");
                    String salaTipo = rs.getString("sala_tipo");
                    int salaCapacidade = rs.getInt("sala_capacidade");

                    boolean quadroBranco = false;
                    if ("Standard".equals(salaTipo)) {
                        quadroBranco = temQuadroBranco(conn, salaId);
                    }
                    Sala sala = criarSala(salaTipo, salaCodigo, salaCapacidade, quadroBranco);

                    String clienteNome = rs.getString("cliente_nome");
                    String clienteCpf = rs.getString("cliente_cpf");
                    boolean clienteCorporativo = rs.getBoolean("cliente_corporativo");
                    Cliente cliente = new Cliente(clienteNome, clienteCpf, clienteCorporativo);

                    if (sala != null && cliente != null) {
                        reservas.add(new Reserva(sala, cliente, inicioReserva, fimReserva));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar reservas por cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return reservas;
    }

    public List<Reserva> buscarReservasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.data_inicio, r.data_fim, r.custo_calculado, " +
                "s.id as sala_id, s.codigo as sala_codigo, ts.nome as sala_tipo, s.capacidade as sala_capacidade, " +
                "c.nome as cliente_nome, c.cpf as cliente_cpf, c.corporativo as cliente_corporativo " +
                "FROM reservas r " +
                "INNER JOIN salas s ON r.sala_id = s.id " +
                "INNER JOIN tipo_sala ts ON s.tipo_sala_id = ts.id " + 
                "INNER JOIN clientes c ON r.cliente_id = c.id " +
                "WHERE r.data_inicio >= ? AND r.data_fim <= ? " +
                "AND r.status_reserva IN ('Ativa', 'Concluida') " +
                "ORDER BY r.data_inicio";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime inicioReserva = rs.getTimestamp("data_inicio").toLocalDateTime();
                    LocalDateTime fimReserva = rs.getTimestamp("data_fim").toLocalDateTime();

                    int salaId = rs.getInt("sala_id");
                    String salaCodigo = rs.getString("sala_codigo");
                    String salaTipo = rs.getString("sala_tipo");
                    int salaCapacidade = rs.getInt("sala_capacidade");

                    boolean quadroBranco = false;
                    if ("Standard".equals(salaTipo)) {
                        quadroBranco = temQuadroBranco(conn, salaId);
                    }
                    Sala sala = criarSala(salaTipo, salaCodigo, salaCapacidade, quadroBranco);

                    String clienteNome = rs.getString("cliente_nome");
                    String clienteCpf = rs.getString("cliente_cpf");
                    boolean clienteCorporativo = rs.getBoolean("cliente_corporativo");
                    Cliente cliente = new Cliente(clienteNome, clienteCpf, clienteCorporativo);

                    if (sala != null && cliente != null) {
                        reservas.add(new Reserva(sala, cliente, inicioReserva, fimReserva));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar reservas por período: " + e.getMessage());
            e.printStackTrace();
        }

        return reservas;
    }

    private boolean temQuadroBranco(Connection conn, int salaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sala_recursos sr " +
                "INNER JOIN recursos r ON sr.recurso_id = r.id " +
                "WHERE sr.sala_id = ? AND r.nome = 'Quadro Branco'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Sala criarSala(String tipo, String codigo, int capacidade, boolean quadroBranco) {
        switch (tipo) {
            case "Standard":
                return new SalaStandard(codigo, capacidade, quadroBranco);
            case "Premium":
                return new SalaPremium(codigo, capacidade);
            case "VIP":
                return new SalaVIP(codigo, capacidade);
            default:
                return null;
        }
    }

    public void salvarReservas(List<Reserva> reservas) {
        for (Reserva reserva : reservas) {
            inserirReserva(reserva);
        }
    }

    /**
     * Insere uma nova reserva.
     * Utiliza sub-queries (SELECT ID FROM ...) para resolver as chaves estrangeiras
     * de Sala e Cliente baseado no Código e CPF.
     */
    public boolean inserirReserva(Reserva reserva) {
        String sql = "INSERT INTO reservas (sala_id, cliente_id, data_inicio, data_fim, custo_calculado, status_reserva) "
                +
                "SELECT s.id, c.id, ?, ?, ?, 'Ativa' " +
                "FROM salas s " +
                "CROSS JOIN clientes c " +
                "WHERE s.codigo = ? AND c.cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(reserva.getInicio()));
            stmt.setTimestamp(2, Timestamp.valueOf(reserva.getFim()));
            stmt.setDouble(3, reserva.calcularCusto());
            stmt.setString(4, reserva.getSala().getCodigo());
            stmt.setString(5, reserva.getCliente().getCpf());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao inserir reserva: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Realiza o cancelamento lógico da reserva e registra o valor do reembolso.
     */
    public boolean removerReserva(Reserva reserva) {
        double valorReembolso = reserva.calcularReembolso();

        String sql = "UPDATE reservas r " +
                "INNER JOIN salas s ON r.sala_id = s.id " +
                "INNER JOIN clientes c ON r.cliente_id = c.id " +
                "SET r.status_reserva = 'Cancelada', " +
                "    r.data_cancelamento = CURRENT_TIMESTAMP, " +
                "    r.valor_reembolso = ? " +
                "WHERE s.codigo = ? " +
                "AND c.cpf = ? " +
                "AND r.data_inicio = ? " +
                "AND r.data_fim = ? " +
                "AND r.status_reserva = 'Ativa'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, valorReembolso);
            stmt.setString(2, reserva.getSala().getCodigo());
            stmt.setString(3, reserva.getCliente().getCpf());
            stmt.setTimestamp(4, Timestamp.valueOf(reserva.getInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(reserva.getFim()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao remover reserva: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica conflito de horário no banco de dados.
     * Checa se existe alguma reserva ativa que se sobreponha ao período solicitado.
     */
    public boolean existeConflito(String codigoSala, LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) " +
                "FROM reservas r " +
                "INNER JOIN salas s ON r.sala_id = s.id " +
                "WHERE s.codigo = ? " +
                "AND r.status_reserva = 'Ativa' " +
                "AND NOT (r.data_fim <= ? OR r.data_inicio >= ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoSala);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar conflito: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
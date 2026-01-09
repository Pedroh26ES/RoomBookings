package dao;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável pela persistência das salas e seus recursos.
 * Lida com a hierarquia de classes (Standard, Premium, VIP)
 */
public class SalaDAO {

    /**
     * Carrega todas as salas, identificando seu tipo e recursos associados
     */
    public List<Sala> carregarSalas() {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT s.id, s.codigo, ts.nome as tipo, s.capacidade " +
                "FROM salas s " +
                "INNER JOIN tipo_sala ts ON s.tipo_sala_id = ts.id " +
                "ORDER BY s.codigo";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String codigo = rs.getString("codigo");
                String tipo = rs.getString("tipo");
                int capacidade = rs.getInt("capacidade");

                boolean quadroBranco = false;
                if ("Standard".equals(tipo)) {
                    List<String> recursos = carregarRecursosDaSala(id);
                    quadroBranco = recursos.contains("Quadro Branco");
                }

                Sala sala = criarSala(tipo, codigo, capacidade, quadroBranco);
                if (sala != null) {
                    salas.add(sala);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar salas: " + e.getMessage());
            e.printStackTrace();
        }

        return salas;
    }

    /**
     * Insere uma sala e vincula automaticamente os recursos padrão do seu tipo.
     * Utiliza RETURN_GENERATED_KEYS para obter o ID gerado pelo banco.
     */
    public boolean inserirSala(Sala sala) {
        String sql = "INSERT INTO salas (codigo, tipo_sala_id, capacidade) " +
                "SELECT ?, ts.id, ? FROM tipo_sala ts WHERE ts.nome = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, sala.getCodigo());
                stmt.setInt(2, sala.getCapacidade());
                stmt.setString(3, obterTipoSala(sala));

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int salaId = generatedKeys.getInt(1);
                            associarRecursosPorTipo(conn, salaId, sala);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir sala: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private void associarRecursosPorTipo(Connection conn, int salaId, Sala sala) throws SQLException {
        if (sala instanceof SalaStandard) {
            SalaStandard standard = (SalaStandard) sala;
            if (standard.hasQuadroBranco()) {
                associarRecursoASala(conn, salaId, "Quadro Branco");
            }
            associarRecursoASala(conn, salaId, "Wi-Fi Premium");
        } else if (sala instanceof SalaPremium) {
            associarRecursoASala(conn, salaId, "Projetor");
            associarRecursoASala(conn, salaId, "Quadro Branco");
            associarRecursoASala(conn, salaId, "Ar-condicionado");
            associarRecursoASala(conn, salaId, "Wi-Fi Premium");
        } else if (sala instanceof SalaVIP) {
            String[] recursosVIP = { "Projetor", "Videoconferência", "Quadro Branco",
                    "Ar-condicionado", "Televisão", "Sistema de Som",
                    "Wi-Fi Premium", "Café e Água" };
            for (String recurso : recursosVIP) {
                associarRecursoASala(conn, salaId, recurso);
            }
        }
    }

    private void associarRecursoASala(Connection conn, int salaId, String nomeRecurso) throws SQLException {
        String sql = "INSERT INTO sala_recursos (sala_id, recurso_id) " +
                "SELECT ?, r.id FROM recursos r WHERE r.nome = ? " +
                "AND NOT EXISTS (SELECT 1 FROM sala_recursos sr WHERE sr.sala_id = ? AND sr.recurso_id = r.id)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaId);
            stmt.setString(2, nomeRecurso);
            stmt.setInt(3, salaId);
            stmt.executeUpdate();
        }
    }

    public List<String> carregarRecursosDaSala(int salaId) {
        List<String> recursos = new ArrayList<>();
        String sql = "SELECT r.nome FROM recursos r " +
                "JOIN sala_recursos sr ON r.id = sr.recurso_id " +
                "WHERE sr.sala_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                recursos.add(rs.getString("nome"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar recursos da sala: " + e.getMessage());
        }

        return recursos;
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

    private String obterTipoSala(Sala sala) {
        if (sala instanceof SalaStandard)
            return "Standard";
        if (sala instanceof SalaPremium)
            return "Premium";
        if (sala instanceof SalaVIP)
            return "VIP";
        return "Standard";
    }

    public boolean existeSala(String codigo) {
        String sql = "SELECT COUNT(*) FROM salas WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência da sala: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Verifica se há reservas vinculadas à sala, impedindo exclusão sem querer.
     */
       public boolean temReservasAtivas(String codigo) { 
        String sql = "SELECT COUNT(*) FROM reservas r " +
                "INNER JOIN salas s ON r.sala_id = s.id " +
                "WHERE s.codigo = ?"; 

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar reservas da sala: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove fisicamente as reservas de uma sala.
     * Necessário para limpeza antes de remover a sala do cadastro.
     */
    public boolean cancelarReservasDaSala(String codigo) {
        String sql = "DELETE FROM reservas WHERE sala_id = (SELECT id FROM salas WHERE codigo = ?)"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            int rowsAffected = stmt.executeUpdate();
            return true; 
        } catch (SQLException e) {
            System.err.println("Erro ao deletar reservas da sala: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove uma sala e suas associações de recursos via Transação.
     * Garante que ou tudo é removido, ou nada é alterado.
     */
    public boolean removerSala(String codigo) {
        String sqlRecursos = "DELETE FROM sala_recursos WHERE sala_id = (SELECT id FROM salas WHERE codigo = ?)";
        String sqlSala = "DELETE FROM salas WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseConnection.executeTransaction(conn, () -> {
                try (PreparedStatement stmtRecursos = conn.prepareStatement(sqlRecursos)) {
                    stmtRecursos.setString(1, codigo);
                    stmtRecursos.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException("Erro ao remover recursos da sala: " + e.getMessage(), e);
                }

                try (PreparedStatement stmtSala = conn.prepareStatement(sqlSala)) {
                    stmtSala.setString(1, codigo);
                    int rowsAffected = stmtSala.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new RuntimeException("Sala não encontrada para remoção: " + codigo);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Erro ao remover a sala: " + e.getMessage(), e);
                }
            });
            return true;
        } catch (SQLException | RuntimeException e) {
            System.err.println("Erro ao remover sala (transação): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
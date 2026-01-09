package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável pelo gerenciamento dos recursos disponíveis no sistema.
 * Permite listar os recursos e associá-los a salas específicas.
 */
public class RecursoDAO {
    
    /**
     * Lista todos os recursos cadastrados no banco.
     * @return Lista de nomes dos recursos.
     */
    public List<String> listarTodosRecursos() {
        List<String> recursos = new ArrayList<>();
        String sql = "SELECT nome FROM recursos ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                recursos.add(rs.getString("nome"));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar recursos: " + e.getMessage());
        }
        
        return recursos;
    }
    
    /**
     * Vincula um recurso existente a uma sala específica na tabela associativa.
     */
    public boolean associarRecursoASala(String codigoSala, String nomeRecurso) {
        String sql = "INSERT INTO sala_recursos (sala_id, recurso_id) " +
                    "SELECT s.id, r.id FROM salas s, recursos r " +
                    "WHERE s.codigo = ? AND r.nome = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigoSala);
            stmt.setString(2, nomeRecurso);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao associar recurso à sala: " + e.getMessage());
            return false;
        }
    }
}
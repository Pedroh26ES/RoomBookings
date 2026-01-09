package dao;

import model.Cliente;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável pelos dados de Clientes.
 * Gerencia operações de inclusão, consulta e remoção.
 */
public class ClienteDAO {

    /**
     * Recupera todos os clientes cadastrados.
     * @return Lista de objetos Cliente.
     */
    public List<Cliente> carregarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT nome, cpf, corporativo FROM clientes ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                boolean corporativo = rs.getBoolean("corporativo");
                clientes.add(new Cliente(nome, cpf, corporativo));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return clientes;
    }

    public void salvarClientes(List<Cliente> clientes) {
        for (Cliente cliente : clientes) {
            if (!existeCliente(cliente.getCpf())) {
                inserirCliente(cliente);
            }
        }
    }

    /**
     * Insere um novo cliente no banco.
     * @return true se a inserção foi feita bem sucedida.
     */
    public boolean inserirCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, cpf, corporativo) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setBoolean(3, cliente.isCorporativo());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeCliente(String cpf) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência do cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Remove um cliente pelo CPF.
     * Verifica previamente se existem dependências (reservas) ativas.
     */
    public boolean removerCliente(String cpf) {
        if (temReservasAtivas(cpf)) {
            return false; 
        }

        String sql = "DELETE FROM clientes WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao remover cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Cliente buscarPorCpf(String cpf) {
        String sql = "SELECT nome, cpf, corporativo FROM clientes WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                boolean corporativo = rs.getBoolean("corporativo");
                return new Cliente(nome, cpf, corporativo);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica se o cliente possui reservas futuras ou em andamento.
     * Utiliza INNER JOIN para cruzar CPF com a tabela de reservas.
     */
    public boolean temReservasAtivas(String cpfCliente) {
        String sql = "SELECT COUNT(*) FROM reservas r " +
                     "INNER JOIN clientes c ON r.cliente_id = c.id " +
                     "WHERE c.cpf = ? AND r.status_reserva = 'Ativa' AND r.data_fim > NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpfCliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar reservas ativas para o cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cancela logicamente (status='Cancelada') todas as reservas ativas de um cliente.
     * Aplica uma taxa de reembolso padrão (50%) para cancelamentos em massa.
     */
    public boolean cancelarReservasDoCliente(String cpfCliente) {
        String sql = "UPDATE reservas r " +
                     "INNER JOIN clientes c ON r.cliente_id = c.id " +
                     "SET r.status_reserva = 'Cancelada', " +
                     "    r.data_cancelamento = CURRENT_TIMESTAMP, " +
                     "    r.valor_reembolso = r.custo_calculado * 0.5 " + 
                     "WHERE c.cpf = ? AND r.status_reserva = 'Ativa'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpfCliente);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Canceladas " + rowsAffected + " reservas do cliente " + cpfCliente);
            return true; 
        } catch (SQLException e) {
            System.err.println("Erro ao cancelar reservas do cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
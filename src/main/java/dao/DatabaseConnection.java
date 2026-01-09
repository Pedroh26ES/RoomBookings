package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a conexão com o banco de dados MySQL.
 * Implementa o padrão Singleton para centralizar
 * as configurações de acesso e o fornecimento de conexões para os DAOs.
 */
public class DatabaseConnection {
    private static final String SERVER = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "roomTESTE";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234"; 

    private static final String CONNECTION_STRING = "jdbc:mysql://" + SERVER + ":" + PORT + "/" + DATABASE +
            "?useSSL=false" +
            "&serverTimezone=UTC" +
            "&allowPublicKeyRetrieval=true";

    /**
     * Obtém uma nova conexão com o banco de dados.
     * @return Objeto Connection pronto para uso.
     * @throws SQLException Se houver falha na autenticação ou driver não encontrado.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado", e);
        }
    }

    /**
     * Fecha a conexão de forma segura, verificando se não é nula.
     * @param conn Conexão a ser fechada.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param conn Conexão ativa.
     * @param transaction Lógica a ser ativa.
     * @throws SQLException Se ocorrer erro durante a transação.
     */
    public static void executeTransaction(Connection conn, Runnable transaction) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            transaction.run();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw new SQLException("Erro na transação", e);
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}
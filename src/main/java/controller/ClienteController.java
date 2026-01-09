package controller;

import model.Cliente;
import dao.ClienteDAO;
import java.util.List;

/**
 * responsável pelas operações relacionadas a Clientes.
 */
public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    /**
     * Adiciona um novo cliente ao sistema.
     * Verifica se o CPF já existe antes de prosseguir.
     *
     * @param cliente Objeto Cliente preenchido.
     * @return true se o cadastro foi realizado com sucesso.
     */
    public boolean adicionarCliente(Cliente cliente) {
        if (clienteDAO.existeCliente(cliente.getCpf())) {
            System.out.println("Cliente com esse CPF já existe.");
            return false;
        }
        
        boolean sucesso = clienteDAO.inserirCliente(cliente);
        if (sucesso) {
            System.out.println("Cliente adicionado: " + cliente.getNome());
        }
        return sucesso;
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteDAO.buscarPorCpf(cpf);
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.carregarClientes();
    }

    /**
     * Tenta remover um cliente do sistema.
     * A operação é bloqueada se o cliente possuir reservas ativas.
     *
     * @param cpf CPF do cliente a ser removido.
     * @return Código de status da operação:
     * "SUCESSO", "CLIENTE_COM_RESERVAS" ou "ERRO_DESCONHECIDO".
     */
    public String removerCliente(String cpf) {
        if (clienteDAO.temReservasAtivas(cpf)) {
            return "CLIENTE_COM_RESERVAS"; 
        }
        
        boolean sucesso = clienteDAO.removerCliente(cpf);
        if (sucesso) {
            System.out.println("Cliente removido com CPF: " + cpf);
            return "SUCESSO";
        } else {
            return "ERRO_DESCONHECIDO"; 
        }
    }

    /**
     * Remove um cliente forçando o cancelamento de suas reservas.
     * Método destrutivo: cancela todas as reservas ativas do cliente antes de excluí-lo.
     *
     * @param cpf CPF do cliente.
     * @return true se a operação completa (cancelamento + exclusão) foi bem-sucedida.
     */
    public boolean removerClienteComCancelamento(String cpf) {
        boolean reservasCanceladas = clienteDAO.cancelarReservasDoCliente(cpf);
        if (!reservasCanceladas) {
            System.err.println("Erro ao cancelar as reservas do cliente " + cpf);
            return false;
        }
        
        return clienteDAO.removerCliente(cpf); 
    }
}
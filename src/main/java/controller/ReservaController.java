package controller;

import dao.ReservaDAO;
import model.Reserva;
import model.Sala;
import model.Cliente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; 

/**
 * Controlador central do sistema, gerenciando o fluxo de reservas.
 * Responsável por garantir que não existam conflitos de horário
 */
public class ReservaController {
    private ReservaDAO reservaDAO;
    private SalaController salaController; 
    private ClienteController clienteController; 

    public ReservaController(ReservaDAO reservaDAO, SalaController salaController, ClienteController clienteController) {
        this.reservaDAO = reservaDAO;
        this.salaController = salaController;
        this.clienteController = clienteController;
    }

    /**
     * Cria uma nova reserva validando datas e disponibilidade.
     *
     * @param sala A sala desejada.
     * @param cliente O cliente solicitante.
     * @param inicio Data/Hora de início.
     * @param fim Data/Hora de término.
     * @return true se a reserva foi criada e persistida com sucesso.
     */
    public boolean criarReserva(Sala sala, Cliente cliente, LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim) || inicio.isEqual(fim)) {
            System.out.println("Horário inválido.");
            return false;
        }

        Reserva nova = new Reserva(sala, cliente, inicio, fim);

        if (temConflito(nova)) {
            System.out.println("Conflito com outra reserva.");
            return false;
        }

        boolean sucesso = reservaDAO.inserirReserva(nova);
        if (sucesso) {
            System.out.println("Reserva criada com sucesso.");
        }
        return sucesso;
    }

    public boolean adicionarReserva(Reserva reserva) {
        if (temConflito(reserva)) {
            return false;
        }
        return reservaDAO.inserirReserva(reserva);
    }

    /**
     * Verifica se a nova reserva conflita com alguma já existente no banco.
     */
    private boolean temConflito(Reserva novaReserva) {
        List<Reserva> reservasExistentes = reservaDAO.carregarReservas(
                salaController.listarSalas(),
                clienteController.listarClientes());

        return reservasExistentes.stream()
                .filter(r -> r.getSala().getCodigo().equals(novaReserva.getSala().getCodigo()))
                .anyMatch(r -> r.conflitaCom(novaReserva.getInicio(), novaReserva.getFim()));
    }

    /**
     * Processa o cancelamento de uma reserva.
     * Calcula e exibe o valor do reembolso conforme a política da sala.
     */
    public boolean cancelarReserva(Reserva reserva) {
        double reembolso = reserva.calcularReembolso(); 
        boolean sucesso = reservaDAO.removerReserva(reserva); 
        if (sucesso) {
            System.out.println("Reserva cancelada. Reembolso: R$" + reembolso);
        }
        return sucesso;
    }

    public List<Reserva> listarReservas() {
        return reservaDAO.carregarReservas(
                salaController.listarSalas(),
                clienteController.listarClientes());
    }

    /**
     * Filtra as reservas por CPF do cliente.
     */
    public List<Reserva> buscarPorCliente(String cpf) {
        List<Reserva> todas = listarReservas();
        return todas.stream()
                .filter(r -> r.getCliente().getCpf().equals(cpf))
                .collect(Collectors.toList());
    }

    /**
     * Filtra as reservas que ocorrem dentro de um período específico.
     */
    public List<Reserva> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Reserva> todas = listarReservas();
        return todas.stream()
                .filter(r -> !r.getFim().isBefore(inicio) && !r.getInicio().isAfter(fim))
                .collect(Collectors.toList());
    }

    public double totalArrecadadoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return buscarPorPeriodo(inicio, fim).stream()
                .mapToDouble(Reserva::calcularCusto)
                .sum();
    }

    public List<Reserva> buscarPorSala(String codigoSala) {
        List<Reserva> todas = listarReservas();
        return todas.stream()
                .filter(r -> r.getSala().getCodigo().equals(codigoSala))
                .collect(Collectors.toList());
    }

    /**
     * Verifica se uma sala possui reservas ativas futuras.
     * Utilizado para impedir a remoção de salas que ainda serão usadas.
     */
    public boolean temReservasAtivas(String codigoSala) {
        List<Reserva> reservasSala = buscarPorSala(codigoSala);
        LocalDateTime agora = LocalDateTime.now();

        return reservasSala.stream()
                .anyMatch(r -> r.getFim().isAfter(agora));
    }
}
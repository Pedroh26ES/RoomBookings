package controller;

import dao.SalaDAO;
import model.Sala;
import java.util.List;

/**
 * responsável pela gestão das Salas de reunião.
 */
public class SalaController {
    private SalaDAO salaDAO;

    public SalaController(SalaDAO salaDAO) {
        this.salaDAO = salaDAO;
    }

    /**
     * Adiciona uma nova sala, garantindo que o código identificador seja único.
     * @return true se cadastrada com sucesso.
     */
    public boolean adicionarSala(Sala sala) {
        if (salaDAO.existeSala(sala.getCodigo())) {
            System.out.println("Sala com esse código já existe.");
            return false;
        }
        
        boolean sucesso = salaDAO.inserirSala(sala);
        if (sucesso) {
            System.out.println("Sala adicionada: " + sala.getCodigo());
        }
        return sucesso;
    }

    public Sala buscarPorCodigo(String codigo) {
        List<Sala> salas = salaDAO.carregarSalas();
        return salas.stream()
                .filter(sala -> sala.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);
    }

    public List<Sala> listarSalas() {
        return salaDAO.carregarSalas();
    }

    /**
     * Tenta remover uma sala.
     * Retorna um código de erro específico se houver reservas associadas, permitindo
     * que a View solicite confirmação para remoção forçada.
     *
     * @param codigo Código da sala.
     * @return Status: "SUCESSO", "SALA_NAO_ENCONTRADA" ou "SALA_COM_RESERVAS_ATIVAS".
     */
    public String removerSala(String codigo) {
        if (!salaDAO.existeSala(codigo)) {
            return "SALA_NAO_ENCONTRADA";
        }

        if (salaDAO.temReservasAtivas(codigo)) {
            return "SALA_COM_RESERVAS_ATIVAS"; 
        }

        boolean sucesso = salaDAO.removerSala(codigo);
        if (sucesso) {
            System.out.println("Sala removida: " + codigo);
            return "SUCESSO";
        } else {
            return "ERRO_DESCONHECIDO"; 
        }
    }

    /**
     * Remove uma sala e deleta todas as reservas associadas a ela .
     * ATENÇÃO!! lembre-se de usar com cautela, geralmente após confirmação explícita do usuário.
     */
    public boolean removerSalaComCancelamento(String codigo) {
        if (!salaDAO.existeSala(codigo)) {
            System.out.println("Sala não encontrada: " + codigo);
            return false;
        }

        boolean reservasDeletadas = salaDAO.cancelarReservasDaSala(codigo);
        if (!reservasDeletadas) {
            System.out.println("Erro ao deletar as reservas da sala " + codigo);
            return false;
        }

        return salaDAO.removerSala(codigo);
    }
}
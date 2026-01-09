package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import dao.ClienteDAO;
import dao.ReservaDAO;
import dao.SalaDAO;
import dao.RelatorioDAO; 

import controller.ClienteController;
import controller.SalaController;
import controller.ReservaController;
import controller.RelatorioController; 
import controller.TelaRelatoriosController; 

/**
 * Tela Principal (Dashboard) da aplicação.
 * * Atua como o container principal e centralizador de dependências.
 * instanciados todos os DAOs e Controllers, que são injetados
 * nas telas subsequentes. 
 */
public class MainView extends JFrame {

    private Color corFundo = new Color(245, 245, 245);
    private Color corPrimaria = new Color(52, 152, 219);
    private Color corTexto = Color.WHITE;
    private Font fonteBotao = new Font("Segoe UI", Font.BOLD, 16);

    // Instâncias dos DAOs 
    private ClienteDAO clienteDAO;
    private SalaDAO salaDAO;
    private ReservaDAO reservaDAO;
    private RelatorioDAO relatorioDAO; 

    // Instâncias dos Controllers 
    private ClienteController clienteController;
    private SalaController salaController;
    private ReservaController reservaController;
    private RelatorioController relatorioController; 
    private TelaRelatoriosController telaRelatoriosController; 

    public MainView() {
        setTitle("RoomBookings - Sistema de Gestão de Salas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);

        clienteDAO = new ClienteDAO();
        salaDAO = new SalaDAO();
        reservaDAO = new ReservaDAO();
        relatorioDAO = new RelatorioDAO(); 

        clienteController = new ClienteController(clienteDAO);
        salaController = new SalaController(salaDAO);
        reservaController = new ReservaController(reservaDAO, salaController, clienteController);
        relatorioController = new RelatorioController(relatorioDAO); 

        JPanel painel = new JPanel();
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        painel.setLayout(new GridLayout(6, 1, 15, 15));

        JButton btnCadastroCliente = criarBotao("Cadastrar Cliente");
        JButton btnCadastroSala = criarBotao("Cadastrar Sala");
        JButton btnListarSalas = criarBotao("Listar Salas");
        JButton btnReserva = criarBotao("Reservar Sala");
        JButton btnListagem = criarBotao("Listar Reservas");
        JButton btnRelatorios = criarBotao("Relatórios");

        painel.add(btnCadastroCliente);
        painel.add(btnCadastroSala);
        painel.add(btnListarSalas);
        painel.add(btnReserva);
        painel.add(btnListagem);
        painel.add(btnRelatorios);

        add(painel);

        // injeção de Dependência
        
        btnCadastroCliente.addActionListener((ActionEvent e) -> new TelaCadastroCliente(clienteController).setVisible(true));
        
        btnCadastroSala.addActionListener((ActionEvent e) -> new TelaCadastroSala(salaController).setVisible(true));
        
        btnListarSalas.addActionListener((ActionEvent e) -> new TelaListagemSalas(salaController).setVisible(true));
        
        btnReserva.addActionListener((ActionEvent e) -> new TelaReservaSala(salaController, clienteController, reservaController).setVisible(true));
        
        btnListagem.addActionListener((ActionEvent e) -> new TelaListagemReservas(reservaController).setVisible(true));

        btnRelatorios.addActionListener((ActionEvent e) -> {
            TelaRelatoriosJFrame telaRelatorios = new TelaRelatoriosJFrame(relatorioController); 
            telaRelatorios.setVisible(true);
        });
    }

    /**
     * Factory Method 
     */
    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setFont(fonteBotao);
        botao.setBackground(corPrimaria);
        botao.setForeground(corTexto);
        botao.setBorder(BorderFactory.createLineBorder(corPrimaria.darker()));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(corPrimaria.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(corPrimaria);
            }
        });

        return botao;
    }
}
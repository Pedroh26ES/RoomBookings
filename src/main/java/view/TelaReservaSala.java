package view;
import controller.ClienteController;
import controller.ReservaController;
import controller.SalaController;
import model.Cliente;
import model.Reserva;
import model.Sala;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List; 

/**
 * Interface gráfica para criação e cancelamento de Reservas.
 * Apresenta formulário para seleção de Cliente e Sala 
 * e listagem das reservas ativas para consulta.
 */
public class TelaReservaSala extends JFrame {
    private SalaController salaController;
    private ClienteController clienteController;
    private ReservaController reservaController;

    private JComboBox<Cliente> comboClientes;
    private JComboBox<Sala> comboSalas;

    private JTextField txtInicio;
    private JTextField txtFim;

    private DefaultListModel<Reserva> reservasListModel;
    private JList<Reserva> listaReservas;

    private JButton btnReservar;
    private JButton btnCancelarReserva;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TelaReservaSala(SalaController salaController, ClienteController clienteController, ReservaController reservaController) {
        this.salaController = salaController;
        this.clienteController = clienteController;
        this.reservaController = reservaController;

        setTitle("Reserva de Salas");
        setSize(720, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(new Color(0xE3F2FD));
        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x64B5F6), 2, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        painelFormulario.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 15, 15, 15);
        c.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
        Color labelColor = new Color(0x0D47A1);

        c.gridx = 0;
        c.gridy = 0;
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(labelFont);
        lblCliente.setForeground(labelColor);
        painelFormulario.add(lblCliente, c);

        c.gridx = 1;
        comboClientes = new JComboBox<>();
        comboClientes.setPreferredSize(new Dimension(260, 32));
        comboClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClientes.setBackground(Color.WHITE);
        painelFormulario.add(comboClientes, c);

        c.gridx = 0;
        c.gridy = 1;
        JLabel lblSala = new JLabel("Sala:");
        lblSala.setFont(labelFont);
        lblSala.setForeground(labelColor);
        painelFormulario.add(lblSala, c);

        c.gridx = 1;
        comboSalas = new JComboBox<>();
        comboSalas.setPreferredSize(new Dimension(260, 32));
        comboSalas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboSalas.setBackground(Color.WHITE);
        painelFormulario.add(comboSalas, c);

        c.gridx = 0;
        c.gridy = 2;
        JLabel lblInicio = new JLabel("Início (dd/MM/yyyy HH:mm):");
        lblInicio.setFont(labelFont);
        lblInicio.setForeground(labelColor);
        painelFormulario.add(lblInicio, c);

        c.gridx = 1;
        txtInicio = new JTextField();
        txtInicio.setPreferredSize(new Dimension(260, 32));
        txtInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelFormulario.add(txtInicio, c);

        c.gridx = 0;
        c.gridy = 3;
        JLabel lblFim = new JLabel("Fim (dd/MM/yyyy HH:mm):");
        lblFim.setFont(labelFont);
        lblFim.setForeground(labelColor);
        painelFormulario.add(lblFim, c);

        c.gridx = 1;
        txtFim = new JTextField();
        txtFim.setPreferredSize(new Dimension(260, 32));
        txtFim.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelFormulario.add(txtFim, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;

        btnReservar = new JButton("Reservar Sala");
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnReservar.setBackground(new Color(0x64B5F6)); // azul suave
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setFocusPainted(false);
        btnReservar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReservar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnReservar.setOpaque(true);
        btnReservar.setBorderPainted(false);
        btnReservar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnReservar.setBackground(new Color(0x42A5F5));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnReservar.setBackground(new Color(0x64B5F6));
            }
        });

        painelFormulario.add(btnReservar, c);

        add(painelFormulario, BorderLayout.NORTH);

        JPanel painelReservas = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                g2d.dispose();
            }
        };
        painelReservas.setBackground(Color.WHITE);
        painelReservas.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x90CAF9), 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        painelReservas.setOpaque(true);

        reservasListModel = new DefaultListModel<>();
        listaReservas = new JList<>(reservasListModel);
        listaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaReservas.setVisibleRowCount(10);
        listaReservas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaReservas.setFixedCellHeight(34);
        listaReservas.setSelectionBackground(new Color(0xBBDEFB));
        listaReservas.setSelectionForeground(new Color(0x0D47A1));
        listaReservas.setBorder(BorderFactory.createLineBorder(new Color(0x90CAF9), 1));
        listaReservas.setBackground(new Color(0xE3F2FD));

        JScrollPane scroll = new JScrollPane(listaReservas);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        painelReservas.add(scroll, BorderLayout.CENTER);

        btnCancelarReserva = new JButton("Cancelar Reserva Selecionada");
        btnCancelarReserva.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancelarReserva.setBackground(new Color(0xBBDEFB));
        btnCancelarReserva.setForeground(new Color(0x0D47A1));
        btnCancelarReserva.setFocusPainted(false);
        btnCancelarReserva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelarReserva.setBorder(BorderFactory.createLineBorder(new Color(0x64B5F6), 2, true));
        btnCancelarReserva.setOpaque(true);
        btnCancelarReserva.setBorderPainted(true);
        btnCancelarReserva.setPreferredSize(new Dimension(280, 40));
        btnCancelarReserva.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancelarReserva.setBackground(new Color(0x90CAF9));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCancelarReserva.setBackground(new Color(0xBBDEFB));
            }
        });

        painelReservas.add(btnCancelarReserva, BorderLayout.SOUTH);

        add(painelReservas, BorderLayout.CENTER);

        btnReservar.addActionListener(e -> reservarSala());
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
    }


    private void carregarDados() {
        comboClientes.removeAllItems();
        for (Cliente c : clienteController.listarClientes()) {
            comboClientes.addItem(c);
        }

        comboSalas.removeAllItems();
        for (Sala s : salaController.listarSalas()) {
            comboSalas.addItem(s);
        }

        atualizarListaReservas();
    }

    private void atualizarListaReservas() {
        reservasListModel.clear();
        for (Reserva r : reservaController.listarReservas()) {
            reservasListModel.addElement(r);
        }
    }

    /**
     * tenta realizar uma reserva.
     * valida os campos, converte datas e chama o controller para persistência.
     */
    private void reservarSala() {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        Sala sala = (Sala) comboSalas.getSelectedItem();

        if (cliente == null || sala == null) {
            JOptionPane.showMessageDialog(this, "Selecione cliente e sala.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDateTime inicio = LocalDateTime.parse(txtInicio.getText().trim(), formatter);
            LocalDateTime fim = LocalDateTime.parse(txtFim.getText().trim(), formatter);

            if (!fim.isAfter(inicio)) {
                JOptionPane.showMessageDialog(this, "Hora fim deve ser depois da hora início.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reserva novaReserva = new Reserva(sala, cliente, inicio, fim);

            if (reservaController.adicionarReserva(novaReserva)) {
                JOptionPane.showMessageDialog(this, "Reserva realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                atualizarListaReservas();
            } else {
                JOptionPane.showMessageDialog(this, "Conflito de horário para esta sala.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de data/hora inválido. Use dd/MM/yyyy HH:mm", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        Reserva selecionada = listaReservas.getSelectedValue();

        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirmar cancelamento da reserva da sala " + selecionada.getSala().getCodigo() + "?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (reservaController.cancelarReserva(selecionada)) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                atualizarListaReservas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar reserva.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
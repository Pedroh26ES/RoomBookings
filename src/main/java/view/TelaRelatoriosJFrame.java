package view;

import controller.TelaRelatoriosController;
import controller.RelatorioController; 

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Janela principal de Relatórios e Dashboard.
 */
public class TelaRelatoriosJFrame extends JFrame {
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    
    private JButton btnTotalArrecadado;
    private JButton btnSalasMaisReservadas;
    private JButton btnMediaHorasPorCliente;
    private JButton btnReceitaPorTipo;
    private JButton btnTaxaOcupacao;
    private JButton btnClientesAtivos;
    private JButton btnClientesInativos; 
    private JButton btnResumoFinanceiro;
    
    private JTextArea areaResultados;
    private TelaRelatoriosController controller; 

    public TelaRelatoriosJFrame(RelatorioController relatorioController) {
        inicializarComponentes();
        this.controller = new TelaRelatoriosController(this, relatorioController);
    }

    private void inicializarComponentes() {
        setTitle("Relatórios de Gestão - RoomBookings");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelPrincipal.setBackground(new Color(250, 252, 255));
        setContentPane(painelPrincipal);

        JLabel titulo = new JLabel("Relatórios de Gestão");
        titulo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        titulo.setForeground(new Color(45, 65, 90));
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        painelPrincipal.add(criarPainelFiltros(), BorderLayout.NORTH); 

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(new Color(250, 252, 255));
        contentPanel.add(criarPainelBotoes(), BorderLayout.WEST);
        contentPanel.add(criarPainelResultados(), BorderLayout.CENTER);
        painelPrincipal.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel criarPainelFiltros() {
        JPanel painelFiltros = new JPanel(new GridBagLayout());
        painelFiltros.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 2), 
            "Filtrar por Período (DD/MM/AAAA ou DD/MM/AAAA HH:mm)",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(45, 65, 90)
        ));
        painelFiltros.setBackground(new Color(245, 248, 255));
        painelFiltros.setPreferredSize(new Dimension(800, 150)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblDataInicio = new JLabel("Data Início:");
        lblDataInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelFiltros.add(lblDataInicio, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtDataInicio = criarCampoData("DD/MM/AAAA");
        painelFiltros.add(txtDataInicio, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton btnHojeInicio = criarBotaoSecundario("Hoje", e -> {
            txtDataInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            txtDataInicio.setForeground(new Color(45, 65, 90));
        });
        painelFiltros.add(btnHojeInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDataFim = new JLabel("Data Fim:");
        lblDataFim.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelFiltros.add(lblDataFim, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtDataFim = criarCampoData("DD/MM/AAAA");
        painelFiltros.add(txtDataFim, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton btnHojeFim = criarBotaoSecundario("Hoje", e -> {
            txtDataFim.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            txtDataFim.setForeground(new Color(45, 65, 90));
        });
        painelFiltros.add(btnHojeFim, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel labelAjuda = new JLabel("<html><i>Nota: Para relatórios específicos de um mês, preencha apenas a Data Início. Formato: DD/MM/AAAA ou DD/MM/AAAA HH:mm.</i></html>"); 
        labelAjuda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        labelAjuda.setForeground(new Color(100, 100, 120));
        painelFiltros.add(labelAjuda, gbc);

        return painelFiltros;
    }

    private JTextField criarCampoData(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(150, 28));
        campo.setForeground(Color.GRAY);
        campo.setText(placeholder);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(new Color(45, 65, 90));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });

        return campo;
    }

    private JButton criarBotaoSecundario(String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(230, 235, 245));
        botao.setForeground(new Color(65, 80, 120));
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        botao.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true));
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.addActionListener(acao);
        botao.setPreferredSize(new Dimension(60, 28));

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(new Color(200, 210, 230));
            }
            public void mouseExited(MouseEvent evt) {
                botao.setBackground(new Color(230, 235, 245));
            }
        });

        return botao;
    }

    private JPanel criarPainelResultados() {
        JPanel painelResultados = new JPanel(new BorderLayout());
        painelResultados.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 2, true), 
            "Resultados",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(45, 65, 90)
        ));
        painelResultados.setBackground(Color.WHITE);

        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        areaResultados.setBackground(Color.WHITE);
        areaResultados.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 200, 230), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane scrollPane = new JScrollPane(areaResultados);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        painelResultados.add(scrollPane, BorderLayout.CENTER);

        return painelResultados;
    }

    private JPanel criarPainelBotoes() {
        JPanel painelBotoes = new JPanel(new GridLayout(8, 1, 10, 10)); // Aumentado para 8
        painelBotoes.setBackground(new Color(250, 252, 255));
        painelBotoes.setBorder(new EmptyBorder(10, 10, 10, 0)); 

        btnTotalArrecadado = criarBotaoPrincipal("Total Arrecadado", "Gerar relatório de total arrecadado no período");
        btnSalasMaisReservadas = criarBotaoPrincipal("Salas Mais Reservadas", "Gerar relatório das salas mais reservadas no mês");
        btnMediaHorasPorCliente = criarBotaoPrincipal("Média Horas/Cliente", "Gerar relatório da média de horas por cliente");
        btnReceitaPorTipo = criarBotaoPrincipal("Receita por Tipo", "Gerar relatório de receita por tipo de sala no período"); 
        btnTaxaOcupacao = criarBotaoPrincipal("Taxa de Ocupação", "Gerar relatório da taxa de ocupação das salas no período"); 
        btnClientesAtivos = criarBotaoPrincipal("Clientes Mais Ativos", "Gerar relatório dos clientes com mais reservas");
        btnClientesInativos = criarBotaoPrincipal("Clientes Inativos", "Listar clientes sem reservas ativas"); 
        btnResumoFinanceiro = criarBotaoPrincipal("Resumo Financeiro", "Gerar resumo financeiro detalhado do período"); 

        painelBotoes.add(btnTotalArrecadado);
        painelBotoes.add(btnSalasMaisReservadas);
        painelBotoes.add(btnMediaHorasPorCliente);
        painelBotoes.add(btnReceitaPorTipo);
        painelBotoes.add(btnTaxaOcupacao);
        painelBotoes.add(btnClientesAtivos);
        painelBotoes.add(btnClientesInativos);
        painelBotoes.add(btnResumoFinanceiro);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(new Color(250, 252, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.weighty = 1.0; 
        wrapperPanel.add(painelBotoes, gbc);

        return wrapperPanel; 
    }

    private JButton criarBotaoPrincipal(String texto, String tooltip) {
        JButton botao = new JButton(texto);
        botao.setToolTipText(tooltip);
        botao.setPreferredSize(new Dimension(200, 40)); 
        botao.setBackground(new Color(45, 85, 140));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14)); 
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(25, 55, 100), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12) 
        ));

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(new Color(65, 105, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(new Color(45, 85, 140));
            }
        });

        return botao;
    }

    
    public String getDataInicio() {
        String texto = txtDataInicio.getText();
        if ("DD/MM/AAAA".equals(texto)) return "";
        return texto;
    }

    public String getDataFim() {
        String texto = txtDataFim.getText();
        if ("DD/MM/AAAA".equals(texto)) return "";
        return texto;
    }

    public void setAreaResultados(String texto) {
        areaResultados.setText(texto);
        areaResultados.setCaretPosition(0); 
    }

    public void addListenerTotalArrecadado(ActionListener listener) {
        btnTotalArrecadado.addActionListener(listener);
    }

    public void addListenerSalasMaisReservadas(ActionListener listener) {
        btnSalasMaisReservadas.addActionListener(listener);
    }

    public void addListenerMediaHorasPorCliente(ActionListener listener) {
        btnMediaHorasPorCliente.addActionListener(listener);
    }

    public void addListenerReceitaPorTipo(ActionListener listener) {
        btnReceitaPorTipo.addActionListener(listener);
    }

    public void addListenerTaxaOcupacao(ActionListener listener) {
        btnTaxaOcupacao.addActionListener(listener);
    }

    public void addListenerClientesAtivos(ActionListener listener) {
        btnClientesAtivos.addActionListener(listener);
    }

    public void addListenerClientesInativos(ActionListener listener) {
        btnClientesInativos.addActionListener(listener);
    }

    public void addListenerResumoFinanceiro(ActionListener listener) {
        btnResumoFinanceiro.addActionListener(listener);
    }
}
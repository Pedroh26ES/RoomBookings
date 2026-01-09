package view;
import controller.ReservaController;
import model.Reserva;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tela de visualização e filtragem de reservas.
 * Implementa uma JTable customizada para exibir dados e filtros
 * de busca por CPF ou período.
 */
public class TelaListagemReservas extends JFrame {
    private ReservaController reservaController;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JTextField txtCpfFiltro;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Color backgroundColor = new Color(245, 248, 252);
    private final Color panelBackground = new Color(255, 255, 255);
    private final Color primaryColor = new Color(33, 150, 243);
    private final Color primaryLight = new Color(227, 242, 253);
    private final Color accentColor = new Color(255, 193, 7);
    private final Color borderColor = new Color(200, 210, 220);
    private final Color textColor = new Color(45, 45, 45);
    private final Color tableHeaderColor = new Color(33, 150, 243);
    private final Color tableRowAltColor = new Color(245, 248, 252);
    private final Font fontRegular = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fontBold = new Font("Segoe UI", Font.BOLD, 14);

    public TelaListagemReservas(ReservaController reservaController) {
        this.reservaController = reservaController;

        setTitle("Listagem de Reservas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1250, 450);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 400));
        setBackground(backgroundColor);

        initComponents();
        atualizarTabela();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        setContentPane(mainPanel);

        JPanel painelFiltro = new JPanel();
        painelFiltro.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelFiltro.setBackground(panelBackground);
        painelFiltro.setBorder(new RoundedBorder(borderColor, 1, 15));
        
        painelFiltro.add(criarLabelComIcone("CPF Cliente:", MetalIconFactory.getFileChooserHomeFolderIcon()));
        txtCpfFiltro = criarTextFieldComPlaceholder("Digite CPF...");
        painelFiltro.add(txtCpfFiltro);

        painelFiltro.add(criarLabelComIcone("Início (dd/MM/yyyy HH:mm):", MetalIconFactory.getFileChooserDetailViewIcon()));
        txtDataInicio = criarTextFieldComPlaceholder("Ex: 01/01/2025 14:00");
        txtDataInicio.setPreferredSize(new Dimension(150, 30));
        painelFiltro.add(txtDataInicio);

        painelFiltro.add(criarLabelComIcone("Fim (dd/MM/yyyy HH:mm):", MetalIconFactory.getFileChooserListViewIcon()));
        txtDataFim = criarTextFieldComPlaceholder("Ex: 02/01/2025 18:00");
        txtDataFim.setPreferredSize(new Dimension(150, 30));
        painelFiltro.add(txtDataFim);

        JButton btnFiltrar = criarBotaoComIcone("Filtrar", MetalIconFactory.getTreeControlIcon(true), primaryColor, primaryColor.darker());
        painelFiltro.add(btnFiltrar);

        mainPanel.add(painelFiltro, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new String[]{"Sala", "Cliente", "Início", "Fim", "Custo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(fontRegular);
        tabela.setRowHeight(30);
        tabela.setSelectionBackground(accentColor);
        tabela.setSelectionForeground(Color.BLACK);
        tabela.setGridColor(borderColor);
        tabela.setShowGrid(true);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setFont(fontBold);
        header.setReorderingAllowed(false);
        header.setBorder(new RoundedBorder(tableHeaderColor.darker(), 1, 15));

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableRowAltColor);
                    c.setForeground(textColor);
                } else {
                    c.setBackground(accentColor);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(new RoundedBorder(borderColor, 1, 15));
        scroll.getViewport().setBackground(Color.WHITE);

        mainPanel.add(scroll, BorderLayout.CENTER);

        btnFiltrar.addActionListener(e -> atualizarTabela());
    }

    private JLabel criarLabelComIcone(String texto, Icon icone) {
        JLabel label = new JLabel(texto);
        label.setFont(fontBold);
        label.setForeground(textColor);
        label.setIcon(icone);
        label.setIconTextGap(6);
        return label;
    }

    private JTextField criarTextFieldComPlaceholder(String placeholder) {
        JTextField field = new JTextField(12);
        field.setFont(fontRegular);
        field.setForeground(new Color(110, 110, 110));
        field.setBorder(new RoundedBorder(borderColor, 1, 12));
        field.setPreferredSize(new Dimension(130, 28));
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            boolean showingPlaceholder = true;

            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    field.setText("");
                    field.setForeground(textColor);
                    showingPlaceholder = false;
                }
                field.setBorder(new RoundedBorder(primaryColor, 2, 12));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(110, 110, 110));
                    showingPlaceholder = true;
                }
                field.setBorder(new RoundedBorder(borderColor, 1, 12));
            }
        });

        return field;
    }

    private JButton criarBotaoComIcone(String texto, Icon icone, Color bgColor, Color hoverColor) {
        JButton botao = new JButton(texto, icone);
        botao.setFont(fontBold);
        botao.setForeground(Color.WHITE);
        botao.setBackground(bgColor);
        botao.setBorder(new RoundedBorder(bgColor.darker(), 1, 15));
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(110, 36));
        botao.setIconTextGap(8);

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(hoverColor);
                botao.setBorder(new RoundedBorder(hoverColor.darker(), 2, 15));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(bgColor);
                botao.setBorder(new RoundedBorder(bgColor.darker(), 1, 15));
            }
        });

        return botao;
    }

    /**
     * Atualiza os dados da tabela com base nos filtros selecionados.
     * Chama o controller para buscar os dados filtrados.
     */
    private void atualizarTabela() {
        modeloTabela.setRowCount(0);

        String cpfFiltro = txtCpfFiltro.getText().trim();
        String dataInicioStr = txtDataInicio.getText().trim();
        String dataFimStr = txtDataFim.getText().trim();

        LocalDateTime dataInicio = null;
        LocalDateTime dataFim = null;

        try {
            if (!dataInicioStr.isEmpty() && !dataInicioStr.equals("Ex: 01/01/2025 14:00")) {
                dataInicio = LocalDateTime.parse(dataInicioStr, formatter);
            }
            if (!dataFimStr.isEmpty() && !dataFimStr.equals("Ex: 02/01/2025 18:00")) {
                dataFim = LocalDateTime.parse(dataFimStr, formatter);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy HH:mm", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Reserva> reservasFiltradas;

        if (!cpfFiltro.isEmpty() && !cpfFiltro.equals("Digite CPF...")) {
            reservasFiltradas = reservaController.buscarPorCliente(cpfFiltro);
        } else if (dataInicio != null && dataFim != null) {
            reservasFiltradas = reservaController.buscarPorPeriodo(dataInicio, dataFim);
        } else {
            reservasFiltradas = reservaController.listarReservas();
        }

        for (Reserva r : reservasFiltradas) {
            modeloTabela.addRow(new Object[]{
                    r.getSala().getCodigo(),
                    r.getCliente().getNome(),
                    r.getInicio().format(formatter),
                    r.getFim().format(formatter),
                    String.format("R$ %.2f", r.calcularCusto())
            });
        }
    }

    static class RoundedBorder extends LineBorder {
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            super(color, thickness);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getLineColor());
            g2.setStroke(new BasicStroke(getThickness()));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
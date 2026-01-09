package view;
import controller.SalaController;
import model.Sala;
import model.SalaStandard;
import model.SalaPremium;
import model.SalaVIP;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tela de listagem e gerenciamento de Salas.
 */
public class TelaListagemSalas extends JFrame {
    private SalaController salaController;
    private JTable tabelaSalas;
    private DefaultTableModel modeloTabela;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color primaryColor = new Color(74, 144, 226);
    private final Color primaryDarkColor = new Color(50, 110, 190);
    private final Color accentColor = new Color(255, 180, 0);
    private final Color borderColor = new Color(200, 210, 230);
    private final Color selectedRowColor = new Color(200, 220, 255);

    public TelaListagemSalas(SalaController salaController) {
        this.salaController = salaController;

        setTitle("Listagem de Salas");
        setSize(700, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(backgroundColor);

        initComponents();
        atualizarTabela();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        modeloTabela = new DefaultTableModel(
            new String[]{"Código", "Tipo", "Capacidade", "Características", "Valor/Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaSalas = new JTable(modeloTabela);
        tabelaSalas.setFillsViewportHeight(true);
        tabelaSalas.setRowHeight(28);
        tabelaSalas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaSalas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabelaSalas.getTableHeader().setBackground(primaryColor);
        tabelaSalas.getTableHeader().setForeground(Color.WHITE);
        tabelaSalas.setSelectionBackground(selectedRowColor);
        tabelaSalas.setSelectionForeground(primaryDarkColor);
        tabelaSalas.setShowGrid(false);
        tabelaSalas.setIntercellSpacing(new Dimension(0, 0));
        tabelaSalas.setGridColor(borderColor);
        tabelaSalas.setBorder(new RoundedCornerBorder(borderColor, 1));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabelaSalas.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tabelaSalas.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tabelaSalas);
        scrollPane.setBorder(new RoundedCornerBorder(borderColor, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.setBackground(backgroundColor);

        JButton btnAtualizar = criarBotao("Atualizar Lista", primaryColor, "↻");
        JButton btnExcluir = criarBotao("Excluir Sala", accentColor, "✖");

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);

        mainPanel.add(painelBotoes, BorderLayout.SOUTH);

        add(mainPanel);

        btnAtualizar.addActionListener(e -> atualizarTabela());
        btnExcluir.addActionListener(e -> excluirSala());
    }

    private JButton criarBotao(String texto, Color corFundo, String iconeSimples) {
        JButton botao = new JButton(iconeSimples + " " + texto);
        botao.setFont(new Font("Dialog", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setFocusPainted(false);
        botao.setBorder(new RoundedCornerBorder(corFundo.darker(), 1));
        botao.setPreferredSize(new Dimension(140, 38));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color corOriginal = corFundo;
            final Color corHover = corFundo.brighter();

            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(corHover);
                botao.setBorder(new RoundedCornerBorder(corHover.darker(), 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(corOriginal);
                botao.setBorder(new RoundedCornerBorder(corOriginal.darker(), 1));
            }
        });

        return botao;
    }

    /**
     * Recarrega os dados da tabela buscando a lista atualizada do Controller.
     * Identifica  o tipo de sala para exibir as características certas.
     */
    private void atualizarTabela() {
        modeloTabela.setRowCount(0);

        List<Sala> salas = salaController.listarSalas();

        for (Sala sala : salas) {
            String tipo;
            String caracteristicas;

            if (sala instanceof SalaStandard) {
                tipo = "Standard";
                caracteristicas = "Quadro Branco: " + (((SalaStandard) sala).hasQuadroBranco() ? "Sim" : "Não");
            } else if (sala instanceof SalaPremium) {
                tipo = "Premium";
                caracteristicas = "Projetor e Ar-Condicionado";
            } else if (sala instanceof SalaVIP) {
                tipo = "VIP";
                caracteristicas = "Todos os recursos inclusos";
            } else {
                tipo = "Desconhecido";
                caracteristicas = "-";
            }

            modeloTabela.addRow(new Object[]{
                    sala.getCodigo(),
                    tipo,
                    sala.getCapacidade(),
                    caracteristicas,
                    String.format("R$ %.2f", sala.calcularCustoHora())
            });
        }
    }

    /**
     * Gerencia o fluxo de exclusão de uma sala.
     */
    private void excluirSala() {
        int linhaSelecionada = tabelaSalas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma sala para excluir!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloTabela.getValueAt(linhaSelecionada, 0);

        String statusRemocao = salaController.removerSala(codigo);

        if ("SUCESSO".equals(statusRemocao)) {
            JOptionPane.showMessageDialog(this,
                    "Sala excluída com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();
        } else if ("SALA_COM_RESERVAS_ATIVAS".equals(statusRemocao)) {
            int confirmacao = JOptionPane.showConfirmDialog(this,
                    "A sala " + codigo + " possui reservas associadas.\n" + 
                    "Deseja DELETAR todas as reservas associadas e prosseguir com a exclusão da sala?",
                    "Reservas Associadas Encontradas", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmacao == JOptionPane.YES_OPTION) {
                if (salaController.removerSalaComCancelamento(codigo)) {
                    JOptionPane.showMessageDialog(this,
                            "Reservas deletadas e sala excluída com sucesso!", 
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    atualizarTabela();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao deletar reservas e excluir sala. Tente novamente.", 
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if ("SALA_NAO_ENCONTRADA".equals(statusRemocao)) {
            JOptionPane.showMessageDialog(this,
                    "Sala não encontrada no sistema!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ocorreu um erro inesperado ao excluir a sala. Detalhes: " + statusRemocao, 
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class RoundedCornerBorder extends LineBorder {
        private final int radius = 12;

        public RoundedCornerBorder(Color color, int thickness) {
            super(color, thickness, true);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getLineColor());
            g2d.setStroke(new BasicStroke(getThickness()));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }
}
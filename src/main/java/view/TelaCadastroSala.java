package view;
import controller.SalaController;
import model.SalaStandard;
import model.SalaPremium;
import model.SalaVIP;
import model.Sala; 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Interface gráfica para cadastro de novas salas.
 * Gerencia a lógica de UI para diferentes tipos de sala (Standard, Premium, VIP),
 * habilitando ou desabilitando campos (como Quadro Branco) 
 */
public class TelaCadastroSala extends JFrame {
    private SalaController salaController;

    private JTextField codigoField;
    private JTextField capacidadeField;
    private JComboBox<String> tipoSalaCombo;
    private JCheckBox quadroBrancoCheck;
    private JButton salvarButton;
    private JButton limparButton;

    private final Color primaryColor = new Color(74, 144, 226);
    private final Color primaryDarkColor = new Color(50, 110, 190);
    private final Color accentColor = new Color(255, 180, 0);
    private final Color backgroundColor = new Color(240, 245, 250);
    private final Color borderColor = new Color(170, 190, 220);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

    // Construtor com Injeção do SalaController
    public TelaCadastroSala(SalaController salaController) {
        this.salaController = salaController;

        setTitle("Cadastro de Sala");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel codigoLabel = new JLabel("Código da Sala:");
        codigoLabel.setFont(labelFont);
        codigoLabel.setForeground(primaryDarkColor);
        panel.add(codigoLabel, gbc);

        gbc.gridx = 1;
        codigoField = criarCampoTexto();
        codigoField.setColumns(15);
        panel.add(codigoField, gbc);
        

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel capacidadeLabel = new JLabel("Capacidade:");
        capacidadeLabel.setFont(labelFont);
        capacidadeLabel.setForeground(primaryDarkColor);
        panel.add(capacidadeLabel, gbc);

        gbc.gridx = 1;
        capacidadeField = criarCampoTexto();
        capacidadeField.setColumns(15);
        panel.add(capacidadeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel tipoSalaLabel = new JLabel("Tipo de Sala:");
        tipoSalaLabel.setFont(labelFont);
        tipoSalaLabel.setForeground(primaryDarkColor);
        panel.add(tipoSalaLabel, gbc);

        gbc.gridx = 1;
        tipoSalaCombo = criarComboBox(new String[]{"Standard", "Premium", "VIP"});
        panel.add(tipoSalaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel quadroLabel = new JLabel("Quadro Branco:");
        quadroLabel.setFont(labelFont);
        quadroLabel.setForeground(primaryDarkColor);
        panel.add(quadroLabel, gbc);

        gbc.gridx = 1;
        quadroBrancoCheck = criarCheckBox();
        panel.add(quadroBrancoCheck, gbc);

        // Listener para UX Dinâmica:
        // Só permite selecionar "Quadro Branco" se a sala for Standard.
        // Salas Premium/VIP já possuem recursos fixos na regra de negócio.
        tipoSalaCombo.addActionListener(e -> {
            boolean isStandard = "Standard".equals(tipoSalaCombo.getSelectedItem());
            quadroBrancoCheck.setEnabled(isStandard);
            if (!isStandard) quadroBrancoCheck.setSelected(false);
        });

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(backgroundColor);

        salvarButton = criarBotao("Salvar", primaryColor);
        limparButton = criarBotao("Limpar", accentColor);

        buttonPanel.add(salvarButton);
        buttonPanel.add(limparButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        add(panel);

        salvarButton.addActionListener(e -> salvarSala());
        limparButton.addActionListener(e -> limparCampos());

        quadroBrancoCheck.setEnabled(true);
    }
    
    private JTextField criarCampoTexto() {
        JTextField field = new JTextField();
        field.setFont(fieldFont);
        field.setBorder(new RoundedCornerBorder(borderColor, 1));
        field.setPreferredSize(new Dimension(220, 36));
        field.setForeground(primaryDarkColor);
        field.setCaretColor(primaryDarkColor);
        field.setBackground(Color.WHITE);
        field.setMargin(new Insets(5, 10, 5, 10));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedCornerBorder(primaryColor, 2));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedCornerBorder(borderColor, 1));
            }
        });
        return field;
    }

    private JComboBox<String> criarComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(fieldFont);
        combo.setBackground(Color.WHITE);
        combo.setForeground(primaryDarkColor);
        combo.setBorder(new RoundedCornerBorder(borderColor, 1));
        combo.setPreferredSize(new Dimension(220, 36));
        combo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                combo.setBorder(new RoundedCornerBorder(primaryColor, 2));
            }
            public void focusLost(FocusEvent e) {
                combo.setBorder(new RoundedCornerBorder(borderColor, 1));
            }
        });
        return combo;
    }

    private JCheckBox criarCheckBox() {
        JCheckBox check = new JCheckBox();
        check.setBackground(backgroundColor);
        check.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return check;
    }

    private JButton criarBotao(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setFont(fieldFont);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setFocusPainted(false);
        botao.setBorder(new RoundedCornerBorder(corFundo.darker(), 1));
        botao.setPreferredSize(new Dimension(110, 40));

        botao.addMouseListener(new MouseAdapter() {
            private final Color corOriginal = corFundo;
            private final Color corHover = corFundo.brighter();

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

    private void salvarSala() {
        String codigo = codigoField.getText().trim();
        String capacidadeStr = capacidadeField.getText().trim();
        String tipoSala = (String) tipoSalaCombo.getSelectedItem();
        boolean temQuadroBranco = quadroBrancoCheck.isSelected();

        if (codigo.isEmpty() || capacidadeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int capacidade;
        try {
            capacidade = Integer.parseInt(capacidadeStr);
            if (capacidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Capacidade deve ser um número inteiro positivo!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (salaController.buscarPorCodigo(codigo) != null) {
            JOptionPane.showMessageDialog(this,
                    "Já existe uma sala com este código!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean sucesso = false;
        switch (tipoSala) {
            case "Standard":
                sucesso = salaController.adicionarSala(new SalaStandard(codigo, capacidade, temQuadroBranco));
                break;
            case "Premium":
                sucesso = salaController.adicionarSala(new SalaPremium(codigo, capacidade));
                break;
            case "VIP":
                sucesso = salaController.adicionarSala(new SalaVIP(codigo, capacidade));
                break;
        }

        if (sucesso) {
            JOptionPane.showMessageDialog(this,
                    "Sala cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar a sala. Verifique os dados e tente novamente.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        codigoField.setText("");
        capacidadeField.setText("");
        tipoSalaCombo.setSelectedIndex(0);
        quadroBrancoCheck.setSelected(false);
    }


    static class RoundedCornerBorder extends LineBorder {
        private final int radius;

        public RoundedCornerBorder(Color color, int thickness) {
            super(color, thickness);
            this.radius = 12;
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
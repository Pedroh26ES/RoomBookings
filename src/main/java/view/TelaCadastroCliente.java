package view;

import controller.ClienteController;
import model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;

/**
 * Interface gráfica para registro de novos clientes.
 */
public class TelaCadastroCliente extends JFrame {
    private ClienteController clienteController;

    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JCheckBox corporativoCheck;
    private JButton salvarButton;
    private JButton limparButton;

    private final Color primaryColor = new Color(74, 144, 226);
    private final Color primaryDarkColor = new Color(50, 110, 190);
    private final Color accentColor = new Color(255, 180, 0);
    private final Color backgroundColor = new Color(230, 240, 250);
    private final Color borderColor = new Color(150, 180, 200);
    private final Color hintColor = new Color(150, 150, 150);

    private final Font defaultFont = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);

    public TelaCadastroCliente(ClienteController clienteController) {
        this.clienteController = clienteController;
        setTitle("Cadastro de Cliente");
        setSize(550, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createCompoundBorder(
                        new RoundedCornerBorder(new Color(200, 200, 200), 1, 15),
                        new EmptyBorder(20, 20, 20, 20))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Novo Cliente");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryDarkColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setFont(labelFont);
        mainPanel.add(nomeLabel, gbc);

        nomeField = criarCampoTexto(" Nome do Cliente");
        gbc.gridx = 1;
        gbc.weightx = 2.0;
        mainPanel.add(nomeField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        JLabel cpfLabel = new JLabel("CPF:");
        cpfLabel.setFont(labelFont);
        mainPanel.add(cpfLabel, gbc);

        cpfField = criarCampoCpf();
        gbc.gridx = 1;
        gbc.weightx = 2.0;
        mainPanel.add(cpfField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        JLabel corporativoLabel = new JLabel("Cliente Corporativo:");
        corporativoLabel.setFont(labelFont);
        mainPanel.add(corporativoLabel, gbc);

        corporativoCheck = new JCheckBox();
        corporativoCheck.setBackground(backgroundColor);
        corporativoCheck.setForeground(primaryDarkColor);
        gbc.gridx = 1;
        gbc.weightx = 2.0;
        mainPanel.add(corporativoCheck, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(backgroundColor);

        salvarButton = criarBotao("Salvar", primaryColor);
        limparButton = criarBotao("Limpar", accentColor);

        buttonPanel.add(salvarButton);
        buttonPanel.add(limparButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        salvarButton.addActionListener(this::salvarCliente);
        limparButton.addActionListener(e -> limparCampos());
    }

    // Método específico para criar o campo de CPF com máscara
    private JFormattedTextField criarCampoCpf() {
        MaskFormatter mask = null;
        try {
            // Define a máscara: # aceita apenas números
            // Formato: 3 números, ponto, 3 números, ponto, 3 números, traço, 2 números
            mask = new MaskFormatter("###.###.###-##");
            mask.setPlaceholderCharacter('_'); // Caractere para mostrar espaços vazios
            mask.setValidCharacters("0123456789"); // Garante que só números sejam digitados
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JFormattedTextField field = new JFormattedTextField(mask);
        field.setFont(defaultFont);
        field.setBorder(new RoundedCornerBorder(borderColor, 1, 10));
        field.setPreferredSize(new Dimension(300, 35));
        field.setMargin(new Insets(5, 10, 5, 10));
        field.setForeground(primaryDarkColor);

        field.setFocusLostBehavior(JFormattedTextField.PERSIST);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                ((RoundedCornerBorder) field.getBorder()).setLineColor(primaryDarkColor);
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                ((RoundedCornerBorder) field.getBorder()).setLineColor(borderColor);
                field.repaint();
            }
        });

        return field;
    }

    private JTextField criarCampoTexto(String hint) {
        JTextField field = new JTextField();
        field.setFont(defaultFont);
        field.setBorder(new RoundedCornerBorder(borderColor, 1, 10));
        field.setPreferredSize(new Dimension(300, 35));
        field.setMargin(new Insets(5, 10, 5, 10));
        field.setForeground(hintColor);
        field.setText(hint);
        field.setCaretColor(primaryDarkColor);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(primaryDarkColor);
                }
                ((RoundedCornerBorder) field.getBorder()).setLineColor(primaryDarkColor);
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(hintColor);
                }
                ((RoundedCornerBorder) field.getBorder()).setLineColor(borderColor);
                field.repaint();
            }
        });

        return field;
    }

    private JButton criarBotao(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setFont(defaultFont);
        botao.setFocusPainted(false);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.setBorder(BorderFactory.createCompoundBorder(
                new RoundedCornerBorder(corFundo.darker(), 1, 15),
                new EmptyBorder(10, 20, 10, 20)));

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(corFundo.darker());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                botao.setBackground(corFundo);
            }
        });

        return botao;
    }

    private void salvarCliente(ActionEvent e) {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        boolean corporativo = corporativoCheck.isSelected();
        boolean cpfIncompleto = cpf.contains("_");

        // validação dos campos vazios e cpf
        if (nome.isEmpty() || nome.equals(" Nome do Cliente") || cpfIncompleto) {
            JOptionPane.showMessageDialog(this,
                    "<html><font face='Segoe UI' size='4'>Por favor, preencha o Nome e um CPF válido!</font></html>",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validação do tamanho do nome (Requisito: min 2, max 50)
        if (nome.length() < 2 || nome.length() > 50) {
            JOptionPane.showMessageDialog(this,
                    "<html><font face='Segoe UI' size='4'>O nome deve ter entre 2 e 50 caracteres!</font></html>",
                    "Nome Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente(nome, cpf, corporativo);
        if (clienteController.adicionarCliente(cliente)) {
            JOptionPane.showMessageDialog(this,
                    "<html><font face='Segoe UI' size='4'>Cliente cadastrado com sucesso!</font></html>", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "<html><font face='Segoe UI' size='4'>CPF já cadastrado!</font></html>",
                    "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        nomeField.setText(" Nome do Cliente");
        nomeField.setForeground(hintColor);
        ((RoundedCornerBorder) nomeField.getBorder()).setLineColor(borderColor);

        cpfField.setValue(null);
        ((RoundedCornerBorder) cpfField.getBorder()).setLineColor(borderColor);

        corporativoCheck.setSelected(false);
        nomeField.requestFocusInWindow();
    }

    static class RoundedCornerBorder extends LineBorder {
        private int radius;

        RoundedCornerBorder(Color color, int thickness, int radius) {
            super(color, thickness);
            this.radius = radius;
        }

        public void setLineColor(Color color) {
            this.lineColor = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getLineColor());
            g2d.setStroke(new BasicStroke(getThickness()));
            g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2d.dispose();
        }
    }
}
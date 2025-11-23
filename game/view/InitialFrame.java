package view;

import controller.GameController;
import controller.SaveFileManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class InitialFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private GameController controller;
    private JComboBox<Integer> numPlayersCombo;
    private JTextField[] nameFields;
    private JComboBox<String>[] colorCombos;
    private JPanel playerConfigPanel;
    
    public InitialFrame() {
        this.controller = new GameController();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Banco Imobiliário - Configuração");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Layout principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Banco Imobiliário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Número de jogadores
        JPanel numPlayersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel numPlayersLabel = new JLabel("Número de jogadores (2-6):");
        numPlayersCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6});
        numPlayersCombo.setSelectedItem(2);
        numPlayersCombo.addActionListener(e -> updatePlayerFields());
        numPlayersPanel.add(numPlayersLabel);
        numPlayersPanel.add(numPlayersCombo);
        mainPanel.add(numPlayersPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Painel de configuração dos jogadores
        playerConfigPanel = new JPanel();
        playerConfigPanel.setLayout(new BoxLayout(playerConfigPanel, BoxLayout.Y_AXIS));
        mainPanel.add(playerConfigPanel);
        
        // Inicializar campos para 2 jogadores
        updatePlayerFields();
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Botão iniciar
        JButton startButton = new JButton("Iniciar Jogo");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());
        mainPanel.add(startButton);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Botão carregar
        JButton loadButton = new JButton("Carregar Jogo Salvo");
        loadButton.setFont(new Font("Arial", Font.BOLD, 16));
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.addActionListener(e -> loadGame());
        mainPanel.add(loadButton);
        
        // Scroll pane para caso tenha muitos jogadores
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }
    
    private void updatePlayerFields() {
        int numPlayers = (Integer) numPlayersCombo.getSelectedItem();
        playerConfigPanel.removeAll();
        
        nameFields = new JTextField[numPlayers];
        colorCombos = new JComboBox[numPlayers];
        
        String[] availableColors = {"Vermelho", "Azul", "Verde", "Amarelo", "Roxo", "Laranja"};
        
        for (int i = 0; i < numPlayers; i++) {
            JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            playerPanel.setBorder(BorderFactory.createTitledBorder("Jogador " + (i + 1)));
            
            // Campo de nome
            JLabel nameLabel = new JLabel("Nome:");
            nameFields[i] = new JTextField("Jogador " + (i + 1), 15);
            
            // Combo de cor
            JLabel colorLabel = new JLabel("Cor:");
            colorCombos[i] = new JComboBox<>(availableColors);
            colorCombos[i].setSelectedIndex(i);
            
            playerPanel.add(nameLabel);
            playerPanel.add(nameFields[i]);
            playerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            playerPanel.add(colorLabel);
            playerPanel.add(colorCombos[i]);
            
            playerConfigPanel.add(playerPanel);
        }
        
        playerConfigPanel.revalidate();
        playerConfigPanel.repaint();
    }
    
    private void startGame() {
        // Validar dados
        if (!validateInput()) {
            return;
        }
        
        // Coletar nomes e cores
        List<String> names = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        Set<String> usedColors = new HashSet<>();
        
        for (int i = 0; i < nameFields.length; i++) {
            String name = nameFields[i].getText().trim();
            String color = (String) colorCombos[i].getSelectedItem();
            
            names.add(name);
            
            // Verificar cores duplicadas
            if (usedColors.contains(color)) {
                JOptionPane.showMessageDialog(this,
                    "Cada jogador deve ter uma cor diferente!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            usedColors.add(color);
            colors.add(color);
        }
        
        // Iniciar jogo através do controller
        controller.startNewGame(names, colors);
        
        // Abrir janela do tabuleiro
        BoardFrame boardFrame = new BoardFrame(controller);
        boardFrame.setVisible(true);
        
        // Fechar esta janela
        this.dispose();
    }
    
    private boolean validateInput() {
        // Verificar se todos os nomes estão preenchidos
        for (int i = 0; i < nameFields.length; i++) {
            String name = nameFields[i].getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor, preencha o nome do Jogador " + (i + 1),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    private void loadGame() {
        // Create file chooser configured for saves directory
        JFileChooser fileChooser = SaveFileManager.createLoadChooser();
        
        // Show open dialog
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Try to load the game
            boolean loadSuccess = controller.loadGame(selectedFile.getAbsolutePath());
            
            if (loadSuccess) {
                // Open board frame with loaded game
                BoardFrame boardFrame = new BoardFrame(controller);
                boardFrame.setVisible(true);
                
                // Close this window
                this.dispose();
            } else {
                // Show error message
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar o jogo.\nVerifique se o arquivo está no formato correto.\n" +
                    "Consulte SAVE_FORMAT.txt para mais informações.",
                    "Erro ao Carregar",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        // If user cancels, do nothing (dialog closes automatically)
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InitialFrame frame = new InitialFrame();
            frame.setVisible(true);
        });
    }
}

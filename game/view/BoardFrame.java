package view;

import controller.GameController;
import controller.GameState;
import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class BoardFrame extends JFrame implements Observer {
    private static final long serialVersionUID = 1L;
    
    private GameController controller;
    private GameState gameState;
    private BoardPanel boardPanel;
    private JButton rollDiceButton;
    private JButton manualDiceButton;
    private JButton saveAndExitButton;
    private PlayerStatusPanel playerStatusPanel;
    private GameLogPanel gameLogPanel;
    private boolean gameOverShown = false;
    private String lastPlayerName = "";
    
    public BoardFrame(GameController controller) {
        this.controller = controller;
        this.gameState = GameState.getInstance();
        
        // Registrar como observer
        gameState.addObserver(this);
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Banco Imobiliário");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Layout
        setLayout(new BorderLayout());
        
        // BoardPanel
        boardPanel = new BoardPanel(gameState, controller);
        add(boardPanel, BorderLayout.CENTER);
        
        // Botão de rolar dados
        rollDiceButton = new JButton("Rolar Dados");
        rollDiceButton.addActionListener(e -> rollDice());
        
        manualDiceButton = new JButton("Dado Manual");
        manualDiceButton.addActionListener(e -> askForManualRoll());
        
        saveAndExitButton = new JButton("Salvar e Sair");
        saveAndExitButton.addActionListener(e -> saveAndExit());
        saveAndExitButton.setToolTipText("Salva o jogo e encerra a aplicação");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollDiceButton);
        buttonPanel.add(manualDiceButton);
        buttonPanel.add(saveAndExitButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        playerStatusPanel = new PlayerStatusPanel(gameState, controller);
        add(playerStatusPanel, BorderLayout.EAST); // Adiciona o painel à direita

        gameLogPanel = new GameLogPanel(gameState);
        add(gameLogPanel, BorderLayout.WEST);
        
        // Ajustar tamanho da janela ao conteúdo
        pack();
        setLocationRelativeTo(null);
        
        // Configurar teclas de atalho
        setupKeyBindings();
    }

    /**
     * Reseta os botões para o estado inicial de um turno.
     */
    private void resetButtonsForNewTurn() {
        rollDiceButton.setText("Rolar Dados");
        
        // Remove listeners antigos
        for (java.awt.event.ActionListener al : rollDiceButton.getActionListeners()) {
            rollDiceButton.removeActionListener(al);
        }
        
        // Adiciona ação de rolar
        rollDiceButton.addActionListener(e -> rollDice());
        
        // Habilita os botões
        rollDiceButton.setEnabled(true);
        if (manualDiceButton != null) manualDiceButton.setEnabled(true);
    }
    
    private void setupKeyBindings() {
        // Tecla 'C' para comprar propriedade
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('C'), "buyProperty");
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('c'), "buyProperty");
        
        boardPanel.getActionMap().put("buyProperty", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                buyCurrentProperty();
            }
        });
        
        // Tecla 'H' para construir casa
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('H'), "buildHouse");
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('h'), "buildHouse");
        
        boardPanel.getActionMap().put("buildHouse", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                buildHouse();
            }
        });
        
        // Tecla 'O' para construir hotel
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('O'), "buildHotel");
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('o'), "buildHotel");
        
        boardPanel.getActionMap().put("buildHotel", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                buildHotel();
            }
        });


        // Tecla 'V' para vender propriedade
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('V'), "sellProperty");
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('v'), "sellProperty");
        
        boardPanel.getActionMap().put("sellProperty", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sellProperty();
            }
        });
        
        // Tecla 'E' para eliminar jogador em falência
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('E'), "eliminatePlayer");
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('e'), "eliminatePlayer");
        
        boardPanel.getActionMap().put("eliminatePlayer", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                eliminatePlayer();
            }
        });
    }
    
    private void buyCurrentProperty() {
        controller.buyCurrentProperty();
        boardPanel.repaint();
    }
    
    private void buildHouse() {
        controller.buildHouse();
        boardPanel.repaint();
    }

    private void buildHotel() {
        controller.buildHotel();
        boardPanel.repaint();
    }
    
    private void sellProperty() {
        controller.sellProperty();
        boardPanel.repaint();
    }
    
    private void eliminatePlayer() {
        controller.eliminateCurrentPlayer();
        boardPanel.repaint();
    }
    
    private void saveAndExit() {
        // Check if can save
        if (!controller.canSaveGame()) {
            JOptionPane.showMessageDialog(this,
                "Não é possível salvar após rolar os dados.\n" +
                "Você só pode salvar no início do turno, antes de rolar os dados.",
                "Não Pode Salvar",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja salvar o jogo e sair?",
            "Salvar e Sair",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }
        
        // Show file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Jogo");
        
        // Set filter for .txt files
        javax.swing.filechooser.FileNameExtensionFilter filter = 
            new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de Salvamento (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        // Suggest default filename
        fileChooser.setSelectedFile(new java.io.File("banco_imobiliario_save.txt"));
        
        // Show save dialog
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            
            // Ensure .txt extension
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }
            
            // Try to save
            boolean saveSuccess = controller.saveGame(filePath);
            
            if (saveSuccess) {
                JOptionPane.showMessageDialog(this,
                    "Jogo salvo com sucesso!\n" +
                    "Arquivo: " + filePath,
                    "Salvo",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Exit application
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar o jogo.\n" +
                    "Tente novamente ou escolha outro local.",
                    "Erro ao Salvar",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        // If user cancels file chooser, do nothing
    }
    
    private void rollDice() {
        controller.rollDice();
        toggleButtonsAfterRoll();
    }
    
    private void askForManualRoll() {
        String input = JOptionPane.showInputDialog(this, 
            "Quantas casas deseja andar?", 
            "Rolagem Manual", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (input != null && !input.isEmpty()) {
            try {
                int steps = Integer.parseInt(input);
                if (steps <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor deve ser maior que 0.");
                    return;
                }
                
                // Chama o controller
                controller.rollDiceManual(steps);
                
                // Atualiza botões
                toggleButtonsAfterRoll();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Digite um número válido.");
            }
        }
    }

    private void toggleButtonsAfterRoll() {
        rollDiceButton.setEnabled(false);
        manualDiceButton.setEnabled(false);
        
        // Check if player should roll again (rolled doubles)
        if (gameState.shouldRollAgain()) {
            rollDiceButton.setText("Rolar Novamente");
            
            // Remove TODOS os listeners antigos
            for (java.awt.event.ActionListener al : rollDiceButton.getActionListeners()) {
                rollDiceButton.removeActionListener(al);
            }
            
            rollDiceButton.addActionListener(e -> rollDice());
            rollDiceButton.setEnabled(true);
            manualDiceButton.setEnabled(true);
        } else {
            rollDiceButton.setText("Passar Vez");
            
            // Remove TODOS os listeners antigos (limpeza total) -> ISSO É IMPORTANTE PARA ""DESLIGAR"" O BOTAO NO MOMENTO
            for (java.awt.event.ActionListener al : rollDiceButton.getActionListeners()) {
                rollDiceButton.removeActionListener(al);
            }
            
            rollDiceButton.addActionListener(e -> endTurn());
            rollDiceButton.setEnabled(true);
        }
    }
    
    private void endTurn() {
        if (gameState.getCurrentPlayerBalance() < 0) {
            JOptionPane.showMessageDialog(this, 
                "AÇÃO BLOQUEADA:\n" +
                "Você está com saldo negativo (Falência)!\n\n" +
                "Você deve vender propriedades para pagar sua dívida\n" +
                "ou declarar falência (Botão 'Declarar Falência').",
                "Saldo Negativo",
                JOptionPane.WARNING_MESSAGE);
            return; // IMPEDE O FIM DO TURNO
        }
        controller.endTurn();
        //?Isso ja é feito no update agora.
        // rollDiceButton.setText("Rolar Dados");
        // rollDiceButton.removeActionListener(rollDiceButton.getActionListeners()[0]);
        // rollDiceButton.addActionListener(e -> rollDice());
        // rollDiceButton.setEnabled(true);
        // manualDiceButton.setEnabled(true);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        // Quando GameState notifica, redesenhar
        boardPanel.repaint();
        
        if (controller.isGameOver()) {
           if (!gameOverShown) {
                String winner = controller.getWinner();
                
                JOptionPane.showMessageDialog(
                    this,
                    "FIM DE JOGO!\n\nVencedor: " + winner + "!",
                    "Banco Imobiliário",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Desabilitar controles
                rollDiceButton.setEnabled(false);
                if (manualDiceButton != null) manualDiceButton.setEnabled(false);
                if (saveAndExitButton != null) saveAndExitButton.setEnabled(false);
                
                // Marca que já mostrou para não entrar aqui de novo
                gameOverShown = true;
            }
            return;
        }
        
        // Update Save & Exit button state based on dice roll
        if (saveAndExitButton != null) {
            boolean canSave = controller.canSaveGame();
            saveAndExitButton.setEnabled(canSave);
            if (canSave) {
                saveAndExitButton.setToolTipText("Salva o jogo e encerra a aplicação");
            } else {
                saveAndExitButton.setToolTipText("Só é possível salvar antes de rolar os dados");
            }
        }
        
        //logica para garantir botoes corretos a cada turno
        String currentPlayer = gameState.getCurrentPlayerName();
        if (currentPlayer != null && !currentPlayer.equals(lastPlayerName)) {
            resetButtonsForNewTurn();
            lastPlayerName = currentPlayer;
        }

        // Verificar fim de jogo
    }
}

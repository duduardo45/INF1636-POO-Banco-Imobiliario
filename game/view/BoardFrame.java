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
    private PlayerStatusPanel playerStatusPanel;
    
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
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollDiceButton);
        buttonPanel.add(manualDiceButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        playerStatusPanel = new PlayerStatusPanel(gameState);
        add(playerStatusPanel, BorderLayout.EAST); // Adiciona o painel à direita
        
        // Ajustar tamanho da janela ao conteúdo
        pack();
        setLocationRelativeTo(null);
        
        // Configurar teclas de atalho
        setupKeyBindings();
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
        
        rollDiceButton.setText("Passar Vez");
        
        // Remove TODOS os listeners antigos (limpeza total) -> ISSO É IMPORTANTE PARA ""DESLIGAR"" O BOTAO NO MOMENTO
        for (java.awt.event.ActionListener al : rollDiceButton.getActionListeners()) {
            rollDiceButton.removeActionListener(al);
        }
        
        rollDiceButton.addActionListener(e -> endTurn());
        rollDiceButton.setEnabled(true);
    }
    
    private void endTurn() {
        controller.endTurn();
        rollDiceButton.setText("Rolar Dados");
        rollDiceButton.removeActionListener(rollDiceButton.getActionListeners()[0]);
        rollDiceButton.addActionListener(e -> rollDice());
        rollDiceButton.setEnabled(true);
        manualDiceButton.setEnabled(true);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        // Quando GameState notifica, redesenhar
        boardPanel.repaint();
        
        // Verificar fim de jogo
        if (controller.isGameOver()) {
            String winner = controller.getWinner();
            JOptionPane.showMessageDialog(
                this,
                "FIM DE JOGO!\n\nVencedor: " + winner + "!",
                "Banco Imobiliário",
                JOptionPane.INFORMATION_MESSAGE
            );
            // Desabilitar controles
            rollDiceButton.setEnabled(false);
        }
    }
}

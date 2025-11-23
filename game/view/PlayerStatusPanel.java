// Novo Arquivo: view/PlayerStatusPanel.java

package view;

 
import controller.GameController;
import controller.GameState;
import model.core.entities.ModelFacade.PlayerStatusInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlayerStatusPanel extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    private GameController controller;
    private GameState gameState;
    private JPanel playersContainer; // Painel interno para empilhar os jogadores

    public PlayerStatusPanel(GameState gameState, GameController controller) {
        this.gameState = gameState;
        this.controller = controller;
        // Registra este painel como um observador
        gameState.addObserver(this);
        
        initComponents();
        updatePlayerList(); // Carrega os dados iniciais
    }
    
    private void initComponents() {
        // Define um tamanho preferencial para o painel (250px de largura)
        setPreferredSize(new Dimension(250, 0));
        
        // Adiciona um título e uma borda
        setBorder(BorderFactory.createTitledBorder("Status dos Jogadores"));
        
        // Layout principal deste painel
        setLayout(new BorderLayout());
        
        // Painel interno que vai conter as informações de cada jogador
        playersContainer = new JPanel();
        // BoxLayout para empilhar componentes verticalmente
        playersContainer.setLayout(new BoxLayout(playersContainer, BoxLayout.Y_AXIS));
        
        // Adiciona o painel interno dentro de uma barra de rolagem
        // (para o caso de muitos jogadores)
        JScrollPane scrollPane = new JScrollPane(playersContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);

        JButton btnFinishGame = new JButton("Encerrar Jogo (Contar Dinheiro)");
        btnFinishGame.setFont(new Font("Arial", Font.BOLD, 10));
        btnFinishGame.setBackground(new Color(200, 50, 50));
        btnFinishGame.setForeground(Color.WHITE);

        btnFinishGame.addActionListener(e -> {

            if (gameState.isGameOver()) {
                JOptionPane.showMessageDialog(this, 
                    "O jogo já foi encerrado!", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja encerrar o jogo agora e verificar quem tem mais dinheiro?", 
                "Encerrar Jogo", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                controller.finishGameByTimeLimit();
            }
        });
        
        add(btnFinishGame, BorderLayout.SOUTH);
    }
    
    /**
     * Chamado pelo GameState sempre que os dados mudam
     */
    @Override
    public void update(Observable o, Object arg) {
        // Quando o GameState notificar, atualiza a lista de jogadores
        updatePlayerList();
    }
    
    /**
     * Limpa e recria a lista de status dos jogadores
     */
    private void updatePlayerList() {
        // 1. Limpa o painel
        playersContainer.removeAll();
        
        List<PlayerStatusInfo> allStatus = gameState.getAllPlayerStatusInfo();
        if (allStatus == null) return;
        
        // 2. Adiciona as informações de cada jogador
        for (PlayerStatusInfo status : allStatus) {
            
            // Painel para um único jogador
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            playerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
            playerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda

            // 1. Nome (com cor)
            JLabel nameLabel = new JLabel(status.name);
            nameLabel.setForeground(convertColor(status.color));
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            // 2. Dinheiro
            JLabel balanceLabel = new JLabel("Dinheiro: $" + status.balance);
            
            // 3. Casa Atual
            JLabel spaceLabel = new JLabel("Local: " + status.spaceName);
            
            playerPanel.add(nameLabel);
            playerPanel.add(balanceLabel);
            playerPanel.add(spaceLabel);
            
            playersContainer.add(playerPanel);
            
            // Adiciona uma linha separadora
            playersContainer.add(new JSeparator());
        }
        
        // 3. Atualiza a interface gráfica
        playersContainer.revalidate();
        playersContainer.repaint();
    }
    
    /**
     * Converte o nome de uma cor (String) em um objeto Color do Java
     */
    private Color convertColor(String colorName) {
        if (colorName == null) return Color.BLACK;
        
        switch (colorName.toUpperCase()) {
            case "VERMELHO":
                return Color.RED.darker();
                
            case "AZUL":
                return Color.BLUE;
                
            case "VERDE":
                return Color.GREEN.darker();
                
            case "AMARELO":
                return new Color(218, 165, 32); 
                
            case "LARANJA":
                return Color.ORANGE;
                
            case "ROXO":
                return new Color(128, 0, 128); 
                
            default:
                return Color.BLACK;
        }
    }
}
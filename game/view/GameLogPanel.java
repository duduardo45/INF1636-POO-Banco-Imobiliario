package view;

import controller.GameState;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameLogPanel extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    
    private GameState gameState;
    private JTextArea logArea;
    private JScrollPane scrollPane;
    private int lastLogSize = 0;

    public GameLogPanel(GameState gameState) {
        this.gameState = gameState;
        gameState.addObserver(this); // Observa mudanças
        
        initComponents();
    }
    
    private void initComponents() {
        // Define largura fixa (similar ao painel de status)
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createTitledBorder("Histórico do Jogo"));
        setLayout(new BorderLayout());
        
        // Área de texto (somente leitura)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Adiciona scroll
        scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<String> messages = gameState.getLogMessages();
        
        // Se o tamanho da lista mudou, atualizamos o texto
        if (messages.size() > lastLogSize) {
            // Adiciona apenas as mensagens novas para performance
            for (int i = lastLogSize; i < messages.size(); i++) {
                logArea.append("> " + messages.get(i) + "\n\n");
            }
            lastLogSize = messages.size();
            
            // Rola automaticamente para o final (auto-scroll)
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }
}
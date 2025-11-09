# Guia de Implementação - 2ª Iteração

## Arquivos e Diretórios a Criar no Eclipse

### Estrutura Final de Pacotes

```
image.png```

### ⚠️ IMPORTANTE: Arquitetura com Facade

**Regra crítica do enunciado:** As classes `entities` NÃO devem ser públicas.

**Solução:** Usar o padrão **Facade** obrigatório do trabalho:
- Classes em `model.core.entities` permanecem **package-private** (sem `public`)
- Criar `model.core.GameFacade` como **única interface pública** do Model
- Controller acessa Model **APENAS** através do `GameFacade`
- View acessa **APENAS** o Controller (nunca o Model)

---

## Passo a Passo para Criar no Eclipse

### 1. Criar Pacote `game.view`

**No Eclipse:**
1. Botão direito na pasta `src` (ou onde está o código-fonte)
2. New → Package
3. Name: `game.view`
4. Finish

---

### 2. Criar Pacote `game.controller`

**No Eclipse:**
1. Botão direito na pasta `src`
2. New → Package
3. Name: `game.controller`
4. Finish

---

## Arquivos a Criar (em ordem de dependência)

---

### MODEL - Arquivo 1: GameFacade.java (OBRIGATÓRIO)

**Localização:** `game/model/core/GameFacade.java`

**Como criar no Eclipse:**
1. Botão direito no pacote `model.core` (não em entities!)
2. New → Class
3. Package: `model.core`
4. Name: `GameFacade`
5. Modifiers: marcar `public`
6. Finish

**Responsabilidade:** Facade - única interface pública do Model. Encapsula todo acesso às entities.

**Estrutura base:**
```java
package model.core;

import model.core.entities.*;
import java.util.*;

public class GameFacade {
    private Board board;
    private Bank bank;
    private List<Player> players;
    private int currentPlayerIndex;
    private Dice dice1;
    private Dice dice2;
    private int[] lastDiceRoll;
    
    public GameFacade() {
        this.dice1 = new Dice();
        this.dice2 = new Dice();
        this.lastDiceRoll = new int[]{0, 0};
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Inicializa novo jogo com tabuleiro e jogadores
     */
    public void initializeGame(int numPlayers, List<String> playerNames, List<String> colors) {
        // Criar tabuleiro através do inicializador
        this.board = BoardInitializer.createStandardBoard();
        
        // Extrair propriedades do tabuleiro para o banco
        List<Property> properties = extractPropertiesFromBoard();
        this.bank = new Bank(200000, properties);
        
        // Criar jogadores
        this.players = new ArrayList<>();
        Space startSpace = board.getSpaceAt(0);
        
        for (int i = 0; i < numPlayers; i++) {
            Car car = new Car(colors.get(i), startSpace);
            Player player = new Player(playerNames.get(i), colors.get(i), car, 4000);
            players.add(player);
        }
        
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Rola os dados (aleatório)
     */
    public int[] rollDice() {
        int d1 = dice1.roll();
        int d2 = dice2.roll();
        this.lastDiceRoll[0] = d1;
        this.lastDiceRoll[1] = d2;
        return lastDiceRoll;
    }
    
    /**
     * Move o jogador atual e executa evento da casa
     */
    public void moveCurrentPlayer(int steps) {
        Player currentPlayer = players.get(currentPlayerIndex);
        
        // Move o pião
        currentPlayer.getCar().advancePosition(steps);
        
        // Executa evento da casa
        Space currentSpace = currentPlayer.getCar().getPosition();
        currentSpace.event(currentPlayer);
    }
    
    /**
     * Tenta comprar a propriedade onde o jogador atual está
     */
    public boolean buyCurrentProperty() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Space currentSpace = currentPlayer.getCar().getPosition();
        
        if (!(currentSpace instanceof Property)) {
            return false;
        }
        
        Property property = (Property) currentSpace;
        
        if (property.isOwned()) {
            return false;
        }
        
        if (currentPlayer.getBalance() < property.getCost()) {
            return false;
        }
        
        currentPlayer.buyProperty(property);
        bank.markPropertyAsOwned(property);
        return true;
    }
    
    /**
     * Passa para o próximo jogador
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    
    // ===== GETTERS PARA VIEW (retornam tipos simples) =====
    
    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }
    
    public int getCurrentPlayerBalance() {
        return players.get(currentPlayerIndex).getBalance();
    }
    
    public String getCurrentPlayerColor() {
        return players.get(currentPlayerIndex).getCar().getColor();
    }
    
    public List<String> getCurrentPlayerProperties() {
        List<String> propertyNames = new ArrayList<>();
        for (Property prop : players.get(currentPlayerIndex).getLiquidAssets()) {
            propertyNames.add(prop.getName());
        }
        return propertyNames;
    }
    
    public String getCurrentSpaceName() {
        Player currentPlayer = players.get(currentPlayerIndex);
        return currentPlayer.getCar().getPosition().getName();
    }
    
    public int[] getLastDiceRoll() {
        return lastDiceRoll;
    }
    
    public int getNumPlayers() {
        return players.size();
    }
    
    /**
     * Retorna informações de posição de todos os jogadores
     * Formato: [índice do jogador][índice da casa no tabuleiro]
     */
    public Map<Integer, Integer> getAllPlayerPositions() {
        Map<Integer, Integer> positions = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Space position = players.get(i).getCar().getPosition();
            int spaceIndex = findSpaceIndex(position);
            positions.put(i, spaceIndex);
        }
        return positions;
    }
    
    /**
     * Retorna informações sobre a propriedade atual (se houver)
     */
    public PropertyInfo getCurrentPropertyInfo() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Space currentSpace = currentPlayer.getCar().getPosition();
        
        if (currentSpace instanceof Property) {
            Property prop = (Property) currentSpace;
            return new PropertyInfo(
                prop.getName(),
                prop.getCost(),
                prop.isOwned() ? prop.getOwner().getName() : null,
                prop.getCurrentRent()
            );
        }
        return null;
    }
    
    // ===== MÉTODOS AUXILIARES PRIVADOS =====
    
    private List<Property> extractPropertiesFromBoard() {
        List<Property> properties = new ArrayList<>();
        for (int i = 0; i < board.getTotalSpaces(); i++) {
            Space space = board.getSpaceAt(i);
            if (space instanceof Property) {
                properties.add((Property) space);
            }
        }
        return properties;
    }
    
    private int findSpaceIndex(Space space) {
        for (int i = 0; i < board.getTotalSpaces(); i++) {
            if (board.getSpaceAt(i) == space) {
                return i;
            }
        }
        return 0;
    }
    
    // ===== CLASSE INTERNA PARA TRANSFERIR DADOS =====
    
    /**
     * Classe para transferir informações de propriedade para View
     * (usando apenas tipos simples)
     */
    public static class PropertyInfo {
        public final String name;
        public final int cost;
        public final String ownerName; // null se não tiver dono
        public final int rent;
        
        public PropertyInfo(String name, int cost, String ownerName, int rent) {
            this.name = name;
            this.cost = cost;
            this.ownerName = ownerName;
            this.rent = rent;
        }
    }
}
```

**IMPORTANTE:** Esta classe usa as entities internamente, mas expõe apenas tipos simples (String, int, List<String>) para o Controller/View.

---

### MODEL - Arquivo 2: BoardInitializer.java

**Localização:** `game/controller/BoardInitializer.java`

**Como criar no Eclipse:**
1. Botão direito no pacote `game.controller`
2. New → Class
3. Package: `game.controller`
4. Name: `BoardInitializer`
5. Modifiers: marcar `public`
6. Finish

**Responsabilidade:** Criar todas as 40 casas do tabuleiro brasileiro com valores do CSV.

**Estrutura base:**
```java
package model.core;

import model.core.entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BoardInitializer {  // package-private, não public!
    
    static Board createStandardBoard() {  // package-private, não public!
        List<Space> spaces = new ArrayList<>();
        
        // Criar as 40 casas do tabuleiro
        // Ordem baseada no Tabuleiro-Valores.csv
        
        // Casa 0: Start (Ponto de Partida)
        Start start = new Start("Ponto de Partida", null);
        spaces.add(start);
        
        // Casa 1: Leblon - valor 100 do CSV
        // ... continuar para todas as 40 casas
        
        // Conectar todas as casas em círculo
        for (int i = 0; i < spaces.size(); i++) {
            int nextIndex = (i + 1) % spaces.size();
            spaces.get(i).setNext(spaces.get(nextIndex));
        }
        
        return new Board(spaces);
    }
}
```

**Valores do CSV (Tabuleiro-Valores.csv):**
- PARTIDA: recebe 200
- Leblon: 100
- Av. Presidente Vargas: 60
- Av. Nossa Sra. De Copacabana: 60
- Companhia Ferroviária: 200
- Av. Brigadeiro Faria Lima: 240
- Companhia de Viação: 200
- Av. Rebouças: 220
- Av. 9 de Julho: 220
- PRISÃO
- Av. Europa: 200
- Rua Augusta: 180
- Av. Pacaembú: 180
- Companhia de Táxi: 150
- Interlagos: 350
- Lucros ou Dividendos: recebe 200
- Morumbi: 400
- PARADA LIVRE
- Flamengo: 120
- Botafogo: 100
- Imposto de Renda: paga 200
- Companhia de Navegação: 150
- Av. Brasil: 160
- Av. Paulista: 140
- Jardim Europa: 140
- VÁ PARA A PRISÃO
- Copacabana: 260
- Companhia de Aviação: 200
- Av. Vieira Souto: 320
- Av. Atlântica: 300
- Companhia de Táxi Aéreo: 200
- Ipanema: 300
- Jardim Paulista: 280
- Brooklin: 260

**IMPORTANTE:** Você precisará criar classes para os tipos de Space que ainda não existem:
- `LuckSpace` (Sorte/Revés)
- `Profit` (Lucros e Dividendos)
- `Tax` (Imposto)
- `FreeParking` (Parada Livre)

Verifique quais já existem: Start, Prison, GoToPrison, Property, Place, Company já existem.

---

### CONTROLLER - Arquivo 1: GameState.java

**Localização:** `game/controller/GameState.java`

**Como criar:**
1. Botão direito no pacote `game.controller`
2. New → Class
3. Name: `GameState`
4. Modifiers: marcar `public`
5. Finish

**Responsabilidade:** Singleton + Observable para gerenciar estado do jogo e notificar View.

**Estrutura base:**
```java
package game.controller;

import java.util.*;

public class GameState extends Observable {
    private static GameState instance;
    
    private String currentPlayerName;
    private int currentPlayerBalance;
    private String currentPlayerColor;
    private List<String> currentPlayerProperties;
    private String currentSpaceName;
    private int[] lastDiceRoll;
    private Map<Integer, Integer> allPlayerPositions;  // índice jogador -> índice casa
    private String message;
    
    // Singleton
    private GameState() {
        this.lastDiceRoll = new int[]{0, 0};
        this.currentPlayerProperties = new ArrayList<>();
        this.allPlayerPositions = new HashMap<>();
        this.message = "";
    }
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    // ===== SETTERS (chamados pelo Controller) =====
    
    public void updateCurrentPlayer(String name, int balance, String color, List<String> properties) {
        this.currentPlayerName = name;
        this.currentPlayerBalance = balance;
        this.currentPlayerColor = color;
        this.currentPlayerProperties = properties;
        notifyObserversAndUpdate();
    }
    
    public void setDiceRoll(int dice1, int dice2) {
        this.lastDiceRoll[0] = dice1;
        this.lastDiceRoll[1] = dice2;
        notifyObserversAndUpdate();
    }
    
    public void setCurrentSpaceName(String spaceName) {
        this.currentSpaceName = spaceName;
        notifyObserversAndUpdate();
    }
    
    public void setAllPlayerPositions(Map<Integer, Integer> positions) {
        this.allPlayerPositions = positions;
        notifyObserversAndUpdate();
    }
    
    public void setMessage(String message) {
        this.message = message;
        notifyObserversAndUpdate();
    }
    
    // ===== GETTERS (para a View) =====
    
    public String getCurrentPlayerName() {
        return currentPlayerName;
    }
    
    public int getCurrentPlayerBalance() {
        return currentPlayerBalance;
    }
    
    public String getCurrentPlayerColor() {
        return currentPlayerColor;
    }
    
    public List<String> getCurrentPlayerProperties() {
        return currentPlayerProperties;
    }
    
    public String getCurrentSpaceName() {
        return currentSpaceName;
    }
    
    public int[] getLastDiceRoll() {
        return lastDiceRoll;
    }
    
    public Map<Integer, Integer> getAllPlayerPositions() {
        return allPlayerPositions;
    }
    
    public String getMessage() {
        return message;
    }
    
    private void notifyObserversAndUpdate() {
        setChanged();
        notifyObservers();
    }
}
```

**IMPORTANTE:** GameState não acessa o Model diretamente! Apenas armazena dados simples que o Controller coleta do GameFacade.

---

### CONTROLLER - Arquivo 2: GameController.java

**Localização:** `game/controller/GameController.java`

**Como criar:**
1. Botão direito no pacote `game.controller`
2. New → Class
3. Name: `GameController`
4. Modifiers: marcar `public`
5. Finish

**Responsabilidade:** Intermediário entre View e Model. Usa GameFacade para acessar Model e atualiza GameState para notificar View.

**Estrutura base:**
```java
package game.controller;

import model.core.GameFacade;
import model.core.GameFacade.PropertyInfo;
import java.util.*;

public class GameController {
    private GameFacade gameFacade;  // Acesso ao Model via Facade
    private GameState gameState;     // Estado observável para View
    
    public GameController() {
        this.gameFacade = new GameFacade();
        this.gameState = GameState.getInstance();
    }
    
    /**
     * Inicializa novo jogo
     */
    public void startNewGame(List<String> playerNames, List<String> colors) {
        // Delega para GameFacade
        gameFacade.initializeGame(playerNames.size(), playerNames, colors);
        
        // Atualiza GameState com informações do primeiro jogador
        updateGameState();
    }
    
    /**
     * Rola os dados e move o jogador atual
     */
    public void rollDice() {
        // Rola dados através do Facade
        int[] diceResults = gameFacade.rollDice();
        
        // Atualiza GameState com resultado dos dados
        gameState.setDiceRoll(diceResults[0], diceResults[1]);
        
        // Move jogador
        int total = diceResults[0] + diceResults[1];
        gameFacade.moveCurrentPlayer(total);
        
        // Atualiza GameState completo
        updateGameState();
    }
    
    /**
     * Compra propriedade atual
     */
    public boolean buyCurrentProperty() {
        boolean success = gameFacade.buyCurrentProperty();
        
        if (success) {
            gameState.setMessage("Propriedade comprada com sucesso!");
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível comprar a propriedade.");
        }
        
        return success;
    }
    
    /**
     * Finaliza turno e passa para próximo jogador
     */
    public void endTurn() {
        gameFacade.nextTurn();
        gameState.setMessage("");
        updateGameState();
    }
    
    /**
     * Retorna informações sobre a propriedade atual (se houver)
     */
    public PropertyInfo getCurrentPropertyInfo() {
        return gameFacade.getCurrentPropertyInfo();
    }
    
    /**
     * Atualiza GameState com dados do GameFacade
     * Este método coleta informações do Model via Facade e atualiza o estado observável
     */
    private void updateGameState() {
        // Coleta informações do jogador atual
        String name = gameFacade.getCurrentPlayerName();
        int balance = gameFacade.getCurrentPlayerBalance();
        String color = gameFacade.getCurrentPlayerColor();
        List<String> properties = gameFacade.getCurrentPlayerProperties();
        
        // Atualiza GameState
        gameState.updateCurrentPlayer(name, balance, color, properties);
        
        // Atualiza casa atual
        String spaceName = gameFacade.getCurrentSpaceName();
        gameState.setCurrentSpaceName(spaceName);
        
        // Atualiza posições de todos os jogadores
        Map<Integer, Integer> positions = gameFacade.getAllPlayerPositions();
        gameState.setAllPlayerPositions(positions);
    }
    
    public GameState getGameState() {
        return gameState;
    }
}
```

**IMPORTANTE:** Controller acessa Model APENAS através do GameFacade. Nunca acessa entities diretamente.

---

### VIEW - Arquivo 4: InitialFrame.java

**Localização:** `game/view/InitialFrame.java`

**Como criar:**
1. Botão direito no pacote `game.view`
2. New → Class
3. Name: `InitialFrame`
4. Superclass: `javax.swing.JFrame`
5. Modifiers: marcar `public`
6. Finish

**Responsabilidade:** Janela inicial para configurar jogo (PODE usar Swing livremente).

**Estrutura base:**
```java
package game.view;

import game.controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class InitialFrame extends JFrame {
    private GameController controller;
    private JComboBox<Integer> numPlayersCombo;
    private List<JTextField> nameFields;
    private List<JComboBox<String>> colorCombos;
    
    public InitialFrame() {
        this.controller = new GameController();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Banco Imobiliário - Configuração");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        // Adicionar componentes Swing aqui
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Número de jogadores
        JLabel numPlayersLabel = new JLabel("Número de jogadores (3-6):");
        numPlayersCombo = new JComboBox<>(new Integer[]{3, 4, 5, 6});
        mainPanel.add(numPlayersLabel);
        mainPanel.add(numPlayersCombo);
        
        // Botão iniciar
        JButton startButton = new JButton("Iniciar Jogo");
        startButton.addActionListener(e -> startGame());
        mainPanel.add(startButton);
        
        add(mainPanel);
    }
    
    private void startGame() {
        // Coletar dados e iniciar jogo
        List<String> names = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        
        // ... coletar dos campos
        
        controller.startNewGame(names, colors);
        
        // Abrir BoardFrame
        BoardFrame boardFrame = new BoardFrame(controller);
        boardFrame.setVisible(true);
        
        // Fechar esta janela
        this.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InitialFrame frame = new InitialFrame();
            frame.setVisible(true);
        });
    }
}
```

---

### VIEW - Arquivo 5: BoardPanel.java

**Localização:** `game/view/BoardPanel.java`

**Como criar:**
1. Botão direito no pacote `game.view`
2. New → Class
3. Name: `BoardPanel`
4. Superclass: `javax.swing.JPanel`
5. Modifiers: marcar `public`
6. Finish

**Responsabilidade:** Renderizar TUDO via Java2D (tabuleiro, piões, dados, cartas, informações).

**Estrutura base:**
```java
package game.view;

import game.controller.GameState;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BoardPanel extends JPanel {
    private GameState gameState;
    private Map<String, BufferedImage> imageCache;
    
    // Coordenadas das 40 casas do tabuleiro
    private Map<Integer, Point> spaceCoordinates;
    
    private GameController controller;
    
    public BoardPanel(GameState gameState, GameController controller) {
        this.gameState = gameState;
        this.controller = controller;
        this.imageCache = new HashMap<>();
        this.spaceCoordinates = new HashMap<>();
        
        loadImages();
        initializeSpaceCoordinates();
        
        setPreferredSize(new Dimension(1280, 800));
        setBackground(Color.WHITE);
    }
    
    private void loadImages() {
        try {
            // Carregar tabuleiro
            imageCache.put("tabuleiro", ImageIO.read(
                new File("assets/images/tabuleiro.png")));
            
            // Carregar piões
            for (int i = 0; i <= 5; i++) {
                imageCache.put("pin" + i, ImageIO.read(
                    new File("assets/images/pinos/pin" + i + ".png")));
            }
            
            // Carregar dados
            for (int i = 1; i <= 6; i++) {
                imageCache.put("die" + i, ImageIO.read(
                    new File("assets/images/dados/die_face_" + i + ".png")));
            }
            
            // Carregar cartas de propriedades e companhias
            // ... adicionar conforme necessário
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeSpaceCoordinates() {
        // Definir coordenadas (x, y) para cada uma das 40 casas
        // Baseado na imagem do tabuleiro
        // Exemplo (ajustar conforme seu tabuleiro):
        spaceCoordinates.put(0, new Point(900, 900));  // Start (canto inferior direito)
        spaceCoordinates.put(1, new Point(800, 900));  // Leblon
        spaceCoordinates.put(2, new Point(700, 900));  // ...
        // ... continuar para todas as 40 casas
        spaceCoordinates.put(10, new Point(0, 900));   // Prisão (canto inferior esquerdo)
        spaceCoordinates.put(20, new Point(0, 0));     // Parada Livre (canto superior esquerdo)
        spaceCoordinates.put(30, new Point(900, 0));   // Vá para prisão (canto superior direito)
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Anti-aliasing para texto
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 1. Desenhar tabuleiro de fundo
        drawBoard(g2d);
        
        // 2. Desenhar piões
        drawPawns(g2d);
        
        // 3. Desenhar dados
        drawDice(g2d);
        
        // 4. Desenhar informações do jogador atual
        drawPlayerInfo(g2d);
        
        // 5. Desenhar carta da propriedade atual (se houver)
        // Pega informação através do controller
        if (controller != null) {
            PropertyInfo propertyInfo = controller.getCurrentPropertyInfo();
            drawCurrentPropertyCard(g2d, propertyInfo);
        }
        
        // 6. Desenhar mensagens
        drawMessages(g2d);
    }
    
    // Adicionar referência ao controller
    private GameController controller;
    
    public void setController(GameController controller) {
        this.controller = controller;
    }
    
    private void drawBoard(Graphics2D g2d) {
        BufferedImage boardImage = imageCache.get("tabuleiro");
        if (boardImage != null) {
            g2d.drawImage(boardImage, 0, 0, null);
        }
    }
    
    private void drawPawns(Graphics2D g2d) {
        Map<Integer, Integer> playerPositions = gameState.getAllPlayerPositions();
        if (playerPositions == null) return;
        
        for (Map.Entry<Integer, Integer> entry : playerPositions.entrySet()) {
            int playerIndex = entry.getKey();
            int spaceIndex = entry.getValue();
            
            Point coords = spaceCoordinates.get(spaceIndex);
            
            if (coords != null) {
                BufferedImage pinImage = imageCache.get("pin" + playerIndex);
                if (pinImage != null) {
                    // Aplicar offset para 6 pistas (para múltiplos piões na mesma casa)
                    int offsetX = (playerIndex % 3) * 15;
                    int offsetY = (playerIndex / 3) * 15;
                    g2d.drawImage(pinImage, 
                                  coords.x + offsetX, 
                                  coords.y + offsetY, 
                                  null);
                }
            }
        }
    }
    
    private void drawDice(Graphics2D g2d) {
        int[] diceRoll = gameState.getLastDiceRoll();
        
        BufferedImage die1 = imageCache.get("die" + diceRoll[0]);
        BufferedImage die2 = imageCache.get("die" + diceRoll[1]);
        
        // Posição dos dados (centro do tabuleiro, por exemplo)
        int diceX = 500;
        int diceY = 350;
        
        if (die1 != null) {
            g2d.drawImage(die1, diceX, diceY, null);
        }
        if (die2 != null) {
            g2d.drawImage(die2, diceX + 60, diceY, null);
        }
    }
    
    private void drawPlayerInfo(Graphics2D g2d) {
        String playerName = gameState.getCurrentPlayerName();
        if (playerName == null) return;
        
        // Área de informações (canto direito, por exemplo)
        int infoX = 1050;
        int infoY = 100;
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        g2d.drawString("Jogador: " + playerName, infoX, infoY);
        g2d.drawString("Saldo: $" + gameState.getCurrentPlayerBalance(), infoX, infoY + 25);
        
        // Indicador de cor do jogador
        g2d.setColor(getColorFromString(gameState.getCurrentPlayerColor()));
        g2d.fillRect(infoX, infoY + 35, 50, 20);
        
        // Propriedades do jogador
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Propriedades:", infoX, infoY + 70);
        
        List<String> properties = gameState.getCurrentPlayerProperties();
        int yOffset = 90;
        for (String propName : properties) {
            g2d.drawString("- " + propName, infoX, infoY + yOffset);
            yOffset += 15;
        }
    }
    
    private void drawCurrentPropertyCard(Graphics2D g2d, PropertyInfo propertyInfo) {
        if (propertyInfo == null) return;
        
        // Área para carta (centro superior, por exemplo)
        int cardX = 400;
        int cardY = 50;
        
        // Desenhar imagem da carta (se disponível)
        String imageName = propertyInfo.name.toLowerCase().replace(" ", "_");
        BufferedImage cardImage = imageCache.get(imageName);
        
        if (cardImage != null) {
            g2d.drawImage(cardImage, cardX, cardY, null);
        } else {
            // Desenhar retângulo placeholder
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(cardX, cardY, 200, 150);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRect(cardX, cardY, 200, 150);
        }
        
        // Informações da propriedade via drawString
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(propertyInfo.name, cardX + 10, cardY + 170);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Preço: $" + propertyInfo.cost, cardX + 10, cardY + 190);
        
        if (propertyInfo.ownerName != null) {
            g2d.drawString("Dono: " + propertyInfo.ownerName, 
                          cardX + 10, cardY + 210);
        } else {
            g2d.setColor(Color.GREEN);
            g2d.drawString("Disponível - Pressione C para comprar", 
                          cardX + 10, cardY + 210);
        }
    }
    
    private void drawMessages(Graphics2D g2d) {
        String message = gameState.getMessage();
        if (message != null && !message.isEmpty()) {
            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(message, 400, 750);
        }
    }
    
    private Color getColorFromString(String colorName) {
        switch (colorName.toLowerCase()) {
            case "vermelho": return Color.RED;
            case "azul": return Color.BLUE;
            case "verde": return Color.GREEN;
            case "amarelo": return Color.YELLOW;
            case "roxo": return new Color(128, 0, 128);
            case "laranja": return Color.ORANGE;
            default: return Color.BLACK;
        }
    }
    
}
```

**IMPORTANTE:** BoardPanel não acessa entities! Apenas usa dados do GameState e pede informações adicionais ao Controller quando necessário.

---

### VIEW - Arquivo 6: BoardFrame.java

**Localização:** `game/view/BoardFrame.java`

**Como criar:**
1. Botão direito no pacote `game.view`
2. New → Class
3. Name: `BoardFrame`
4. Superclass: `javax.swing.JFrame`
5. Interfaces: `java.util.Observer`
6. Modifiers: marcar `public`
7. Finish

**Responsabilidade:** Janela principal do jogo (container).

**Estrutura base:**
```java
package game.view;

import game.controller.GameController;
import game.controller.GameState;
import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class BoardFrame extends JFrame implements Observer {
    private GameController controller;
    private GameState gameState;
    private BoardPanel boardPanel;
    private JButton rollDiceButton;
    
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
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Layout
        setLayout(new BorderLayout());
        
        // BoardPanel (ocupa quase toda área) - passa controller também
        boardPanel = new BoardPanel(gameState, controller);
        add(boardPanel, BorderLayout.CENTER);
        
        // Botão de rolar dados (ÚNICO componente Swing permitido no tabuleiro)
        rollDiceButton = new JButton("Rolar Dados");
        rollDiceButton.addActionListener(e -> rollDice());
        
        // Posicionar botão
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollDiceButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Teclas de atalho
        setupKeyBindings();
    }
    
    private void setupKeyBindings() {
        // Tecla 'C' para comprar propriedade
        boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke('C'), "buyProperty");
        boardPanel.getActionMap().put("buyProperty", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                buyCurrentProperty();
            }
        });
        
        // Mais teclas conforme necessário
    }
    
    private void rollDice() {
        controller.rollDice();
        rollDiceButton.setText("Passar Vez");
        rollDiceButton.removeActionListener(rollDiceButton.getActionListeners()[0]);
        rollDiceButton.addActionListener(e -> endTurn());
    }
    
    private void endTurn() {
        controller.endTurn();
        rollDiceButton.setText("Rolar Dados");
        rollDiceButton.removeActionListener(rollDiceButton.getActionListeners()[0]);
        rollDiceButton.addActionListener(e -> rollDice());
    }
    
    private void buyCurrentProperty() {
        controller.buyCurrentProperty();
    }
    
    @Override
    public void update(Observable o, Object arg) {
        // Quando GameState notifica, redesenhar tudo
        boardPanel.repaint();
    }
}
```

---

## Arquivos do Model a Completar (MANTER PACKAGE-PRIVATE!)

### 1. Board.java

**Localização:** `model/core/entities/Board.java` (JÁ EXISTE)

**Modificações:**
- **NÃO tornar pública** - manter package-private
- Adicionar construtor completo
- Adicionar métodos getters

```java
package model.core.entities;

import java.util.List;

class Board {  // SEM public!
    private List<Space> all_spaces;
    
    Board(List<Space> spaces) {  // SEM public!
        this.all_spaces = spaces;
    }
    
    Space getSpaceAt(int index) {  // SEM public!
        if (index >= 0 && index < all_spaces.size()) {
            return all_spaces.get(index);
        }
        return null;
    }
    
    int getTotalSpaces() {  // SEM public!
        return all_spaces.size();
    }
    
    int getPrisonPosition() {  // SEM public!
        // Prisão está na posição 10 (baseado no tabuleiro padrão)
        return 10;
    }
    
    List<Space> getAllSpaces() {  // SEM public!
        return all_spaces;
    }
}
```

### 2. ⚠️ NÃO tornar classes entities públicas

**IMPORTANTE:** Todas as classes em `model.core.entities` devem permanecer **package-private**:
- `Space.java` - **SEM** `public`
- `Property.java` - **SEM** `public`
- `Place.java` - **SEM** `public`
- `Company.java` - **SEM** `public`
- `Player.java` - **SEM** `public`
- `Car.java` - **SEM** `public`
- `Bank.java` - **SEM** `public`
- `Dice.java` - **SEM** `public`
- Todas as outras entities - **SEM** `public`

**Única classe pública do Model:** `model.core.GameFacade`

---

## Classes Faltantes a Criar (se não existirem)

Verificar se existem, senão criar:

### LuckSpace.java

```java
package model.core.entities;

class LuckSpace extends Space {  // SEM public!
    LuckSpace(String name, Space next) {
        super(name, next);
    }
    
    @Override
    void event(Player player) {
        // Sortear carta de Sorte/Revés
        // Implementar na 3ª iteração
    }
}
```

### Tax.java

```java
package model.core.entities;

class Tax extends Space {  // SEM public!
    private int taxAmount;
    
    Tax(String name, Space next, int taxAmount) {
        super(name, next);
        this.taxAmount = taxAmount;
    }
    
    @Override
    void event(Player player) {
        player.debit(taxAmount);
    }
}
```

### Profit.java

```java
package model.core.entities;

class Profit extends Space {  // SEM public!
    private int profitAmount;
    
    Profit(String name, Space next, int profitAmount) {
        super(name, next);
        this.profitAmount = profitAmount;
    }
    
    @Override
    void event(Player player) {
        player.credit(profitAmount);
    }
}
```

### FreeParking.java

```java
package model.core.entities;

class FreeParking extends Space {  // SEM public!
    FreeParking(String name, Space next) {
        super(name, next);
    }
    
    @Override
    void event(Player player) {
        // Não faz nada - apenas visita
    }
}
```

---

## Resumo da Ordem de Criação

1. **Pacotes:** `game.view` e `game.controller`
2. **Model (Facade e auxiliares):**
   - `model/core/GameFacade.java` ⭐ (PÚBLICO - Facade do Model)
   - `model/core/BoardInitializer.java` (package-private)
   - `model/core/entities/Board.java` - completar (package-private)
   - Classes de Space faltantes: LuckSpace, Tax, Profit, FreeParking (package-private)
3. **Controller:**
   - `game/controller/GameState.java` (Singleton + Observable)
   - `game/controller/GameController.java` (usa GameFacade)
4. **View:**
   - `game/view/BoardPanel.java` (renderização Java2D)
   - `game/view/BoardFrame.java` (container + Observer)
   - `game/view/InitialFrame.java` (com método main)

---

## Observações Finais

1. **Imagens:** Certifique-se de que o caminho `assets/images/` está correto no seu projeto Eclipse
2. **Design Patterns aplicados:**
   - **Facade:** `GameFacade` é a única interface pública do Model
   - **Singleton:** `GameState` tem única instância
   - **Observer:** `GameState` (Observable) notifica `BoardFrame` (Observer)
3. **Arquitetura em camadas:**
   - **View** → acessa → **Controller** → acessa → **GameFacade** → acessa → **entities**
   - View NUNCA acessa Model diretamente
   - Controller NUNCA acessa entities diretamente
4. **Java2D:** Todo o tabuleiro renderizado via `drawImage()`, `drawString()`, `fillRect()`, etc no `BoardPanel.paintComponent()`
5. **Único Swing no tabuleiro:** JButton "Rolar Dados"
6. **Coordenadas:** Você precisará ajustar as coordenadas das 40 casas baseado na imagem real do tabuleiro
7. **Entities package-private:** Todas as classes em `model.core.entities` devem permanecer SEM o modificador `public`

---

## Passo a Passo de Implementação (Bite-Sized)

### Fase 1: Preparar Model (30 min)

**Passo 1.1:** Completar `Board.java`
- Adicionar construtor que recebe `List<Space>`
- Adicionar métodos getters (package-private)
- ✅ Testar compilação

**Passo 1.2:** Criar classes Space faltantes
- Criar `LuckSpace.java` (package-private)
- Criar `Tax.java` (package-private)
- Criar `Profit.java` (package-private)
- Criar `FreeParking.java` (package-private)
- ✅ Testar compilação

**Passo 1.3:** Criar `BoardInitializer.java`
- Criar classe package-private em `model.core`
- Implementar método `createStandardBoard()` com as 40 casas
- Usar valores do CSV
- ✅ Testar criando Board no main temporário

**Passo 1.4:** Criar `GameFacade.java`
- Criar classe pública em `model.core`
- Adicionar atributos básicos (board, bank, players)
- Implementar construtor
- Implementar `initializeGame()`
- ✅ Testar criando GameFacade e inicializando jogo

### Fase 2: Controller Básico (20 min)

**Passo 2.1:** Criar `GameState.java`
- Criar Singleton
- Extender `Observable`
- Adicionar atributos simples (String, int, List<String>)
- Implementar getters e setters
- ✅ Testar getInstance()

**Passo 2.2:** Criar `GameController.java`
- Criar classe com GameFacade e GameState
- Implementar `startNewGame()`
- Implementar `updateGameState()` (privado)
- ✅ Testar inicializando jogo e verificando GameState

### Fase 3: View Inicial Simples (30 min)

**Passo 3.1:** Criar `InitialFrame.java`
- Criar JFrame básico com componentes Swing
- Adicionar campo para número de jogadores (fixo em 3 por ora)
- Adicionar nomes hardcoded (Player 1, Player 2, Player 3)
- Adicionar cores hardcoded (Vermelho, Azul, Verde)
- Botão "Iniciar Jogo"
- ✅ Testar exibindo janela

**Passo 3.2:** Conectar InitialFrame com Controller
- No botão "Iniciar", chamar `controller.startNewGame()`
- Abrir `BoardFrame` (criar stub vazio por ora)
- Fechar InitialFrame
- ✅ Testar fluxo completo

### Fase 4: BoardPanel Básico (40 min)

**Passo 4.1:** Criar `BoardPanel.java` vazio
- Criar JPanel que extende JPanel
- Override `paintComponent()` vazio
- Adicionar GameState e Controller no construtor
- ✅ Testar compilação

**Passo 4.2:** Implementar carregamento de imagens
- Criar `loadImages()` privado
- Carregar apenas imagem do tabuleiro
- Criar HashMap para cache
- ✅ Testar carregando imagem

**Passo 4.3:** Desenhar tabuleiro
- Implementar `drawBoard(Graphics2D)`
- Chamar no `paintComponent()`
- ✅ Testar vendo tabuleiro na tela

**Passo 4.4:** Adicionar coordenadas das casas
- Criar método `initializeSpaceCoordinates()`
- Adicionar coordenadas das 4 casas de canto (0, 10, 20, 30)
- ✅ Testar com println verificando coordenadas

**Passo 4.5:** Carregar imagens dos piões
- Adicionar carregamento de imagens dos piões no `loadImages()`
- ✅ Testar verificando se carregou

**Passo 4.6:** Desenhar piões
- Implementar `drawPawns(Graphics2D)`
- Usar `gameState.getAllPlayerPositions()`
- Desenhar na casa 0 (Start)
- ✅ Testar vendo piões no tabuleiro

### Fase 5: BoardFrame e Integração (30 min)

**Passo 5.1:** Criar `BoardFrame.java`
- Criar JFrame que implementa Observer
- Adicionar BoardPanel
- Registrar como observer do GameState
- ✅ Testar abrindo janela com tabuleiro

**Passo 5.2:** Adicionar JButton de dados
- Adicionar botão "Rolar Dados"
- Por ora, apenas println no click
- ✅ Testar clicando botão

**Passo 5.3:** Implementar Observer pattern
- Implementar método `update()`
- Chamar `boardPanel.repaint()`
- ✅ Testar mudando GameState manualmente e vendo repaint

### Fase 6: Lógica de Dados e Movimento (40 min)

**Passo 6.1:** Implementar `rollDice()` no GameFacade
- Adicionar Dice no GameFacade
- Implementar método que rola e retorna resultado
- ✅ Testar com println

**Passo 6.2:** Implementar `rollDice()` no GameController
- Chamar gameFacade.rollDice()
- Atualizar GameState com resultado
- Chamar moveCurrentPlayer()
- ✅ Testar vendo posição mudar

**Passo 6.3:** Desenhar dados
- Carregar imagens dos dados no `loadImages()`
- Implementar `drawDice(Graphics2D)`
- Usar `gameState.getLastDiceRoll()`
- ✅ Testar vendo dados na tela

**Passo 6.4:** Conectar botão com Controller
- No click do botão, chamar `controller.rollDice()`
- ✅ Testar clicando e vendo pião se mover

**Passo 6.5:** Implementar `moveCurrentPlayer()` no GameFacade
- Usar `car.advancePosition(steps)`
- Chamar `space.event(player)`
- ✅ Testar movimento funcionando

**Passo 6.6:** Adicionar todas as coordenadas das 40 casas
- Completar `initializeSpaceCoordinates()` com todas as casas
- Ajustar coordenadas baseado na imagem real
- ✅ Testar movimento por todo tabuleiro

### Fase 7: Informações do Jogador (20 min)

**Passo 7.1:** Implementar `drawPlayerInfo()`
- Desenhar nome do jogador atual
- Desenhar saldo
- Desenhar cor (retângulo colorido)
- ✅ Testar vendo informações na tela

**Passo 7.2:** Implementar `drawMessages()`
- Desenhar mensagem do GameState
- ✅ Testar setando mensagem manualmente

### Fase 8: Propriedades (30 min)

**Passo 8.1:** Implementar `getCurrentPropertyInfo()` no GameFacade
- Verificar se casa atual é Property
- Retornar PropertyInfo ou null
- ✅ Testar parando em propriedade

**Passo 8.2:** Implementar `drawCurrentPropertyCard()`
- Desenhar retângulo placeholder por ora
- Desenhar nome e preço da propriedade
- ✅ Testar parando em propriedade

**Passo 8.3:** Carregar imagens das propriedades
- Adicionar carregamento no `loadImages()`
- Usar nomes dos arquivos de imagem
- ✅ Testar vendo carta da propriedade

**Passo 8.4:** Implementar compra de propriedade
- Adicionar key binding para tecla 'C'
- Implementar `buyCurrentProperty()` no GameFacade
- Implementar no GameController
- ✅ Testar comprando propriedade

### Fase 9: Sistema de Turnos (15 min)

**Passo 9.1:** Implementar troca de turno
- Mudar botão "Rolar Dados" para "Passar Vez" após rolar
- Implementar `endTurn()` no Controller
- Voltar botão para "Rolar Dados"
- ✅ Testar alternando entre jogadores

**Passo 9.2:** Desenhar lista de propriedades do jogador
- Adicionar no `drawPlayerInfo()`
- Listar propriedades do jogador atual
- ✅ Testar vendo propriedades adquiridas

### Fase 10: Polimento (20 min)

**Passo 10.1:** Ajustar InitialFrame
- Permitir input real de nomes
- Permitir seleção de cores
- Validar dados
- ✅ Testar com diferentes configurações

**Passo 10.2:** Ajustar coordenadas finais
- Testar movimento em todas as casas
- Ajustar coordenadas conforme necessário
- Ajustar offsets dos piões
- ✅ Testar com 6 jogadores

**Passo 10.3:** Testes finais
- Testar fluxo completo do jogo
- Verificar todos os requisitos da 2ª iteração
- Corrigir bugs encontrados
- ✅ Entregar!

### Dicas para Implementação Incremental

1. **Sempre compile após cada passo** - não acumule erros
2. **Teste cada funcionalidade isoladamente** - use println, debugger
3. **Commit no git após cada fase completa** - você pode voltar se algo der errado
4. **Se travar em um passo, pule temporariamente** - volte depois
5. **Mantenha um TODO.txt** com problemas encontrados
6. **Teste com diferentes números de jogadores** desde cedo

### Tempo Total Estimado: ~4-5 horas

- Model: 30 min
- Controller: 20 min  
- View Inicial: 30 min
- BoardPanel: 40 min
- BoardFrame: 30 min
- Dados/Movimento: 40 min
- Info Jogador: 20 min
- Propriedades: 30 min
- Turnos: 15 min
- Polimento: 20 min

**Total:** ~4h15min + tempo para ajustes e testes


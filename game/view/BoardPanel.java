package view;

import controller.GameState;
import controller.GameController;
import model.core.entities.ModelFacade.PropertyInfo;
import model.core.entities.ModelFacade.PlayerStatusInfo;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private GameState gameState;
    private GameController controller;
    private Map<String, BufferedImage> imageCache;
    private Map<Integer, Point> spaceCoordinates;
    
    public BoardPanel(GameState gameState, GameController controller) {
        this.gameState = gameState;
        this.controller = controller;
        this.imageCache = new HashMap<>();
        this.spaceCoordinates = new HashMap<>();
        
        loadImages();
        initializeSpaceCoordinates();
        
        setPreferredSize(new Dimension(1000, 800));  // Máximo 1280x800 do enunciado
        setBackground(Color.WHITE);
    }
    
    private void loadImages() {
        try {
            // Carregar tabuleiro
            imageCache.put("tabuleiro", ImageIO.read(
                new File("assets/images/tabuleiro.png")));
            
            // Carregar piões (6 jogadores possíveis)
            for (int i = 0; i <= 5; i++) {
                imageCache.put("pin" + i, ImageIO.read(
                    new File("assets/images/pinos/pin" + i + ".png")));
            }
            
            // Carregar dados
            for (int i = 1; i <= 6; i++) {
                imageCache.put("die" + i, ImageIO.read(
                    new File("assets/images/dados/die_face_" + i + ".png")));
            }
            
            // Carregar imagens dos territórios
            // Mapeamento: nome no jogo -> nome do arquivo
            Map<String, String> territoryMap = new HashMap<>();
            territoryMap.put("Leblon", "Leblon");
            territoryMap.put("Av. Presidente Vargas", "Av. Presidente Vargas");
            territoryMap.put("Av. Nossa Sra. De Copacabana", "Av. Nossa S. de Copacabana");
            territoryMap.put("Av. Brigadeiro Faria Lima", "Av. Brigadero Faria Lima");
            territoryMap.put("Av. Rebouças", "Av. Rebouças");
            territoryMap.put("Av. 9 de Julho", "Av. 9 de Julho");
            territoryMap.put("Av. Europa", "Av. Europa");
            territoryMap.put("Rua Augusta", "Rua Augusta");
            territoryMap.put("Av. Pacaembú", "Av. Pacaembú");
            territoryMap.put("Interlagos", "Interlagos");
            territoryMap.put("Morumbi", "Morumbi");
            territoryMap.put("Flamengo", "Flamengo");
            territoryMap.put("Botafogo", "Botafogo");
            territoryMap.put("Av. Brasil", "Av. Brasil");
            territoryMap.put("Av. Paulista", "Av. Paulista");
            territoryMap.put("Jardim Europa", "Jardim Europa");
            territoryMap.put("Copacabana", "Copacabana");
            territoryMap.put("Av. Vieira Souto", "Av. Vieira Souto");
            territoryMap.put("Av. Atlântica", "Av. Atlântica");
            territoryMap.put("Ipanema", "Ipanema");
            territoryMap.put("Jardim Paulista", "Jardim Paulista");
            territoryMap.put("Brooklin", "Brooklin");
            
            for (Map.Entry<String, String> entry : territoryMap.entrySet()) {
                try {
                    imageCache.put(entry.getKey(), ImageIO.read(
                        new File("assets/images/territorios/" + entry.getValue() + ".png")));
                } catch (IOException e) {
                    System.err.println("Erro ao carregar território " + entry.getKey() + ": " + e.getMessage());
                }
            }
            
            // Carregar imagens das companhias (mapeamento manual)
            Map<String, String> companyMap = new HashMap<>();
            companyMap.put("Companhia Ferroviária", "company1");
            companyMap.put("Companhia de Viação", "company2");
            companyMap.put("Companhia de Táxi", "company3");
            companyMap.put("Companhia de Navegação", "company4");
            companyMap.put("Companhia de Aviação", "company5");
            companyMap.put("Companhia de Táxi Aéreo", "company6");
            
            for (Map.Entry<String, String> entry : companyMap.entrySet()) {
                try {
                    imageCache.put(entry.getKey(), ImageIO.read(
                        new File("assets/images/companhias/" + entry.getValue() + ".png")));
                } catch (IOException e) {
                    System.err.println("Erro ao carregar companhia " + entry.getKey() + ": " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeSpaceCoordinates() {
        // Dimensões reais do tabuleiro baseadas na imagem
        int border = 6;
        int cornerSize = 94;
        int horizontalWidth = 54;   // largura das casas horizontais
        int horizontalHeight = 94;  // altura das casas horizontais
        int verticalWidth = 94;     // largura das casas verticais
        int verticalHeight = 54;    // altura das casas verticais
        
        // Calcular posição do canto inferior direito (casa 0 - Início)
        int bottomRightX = border + cornerSize + (9 * horizontalWidth);
        int bottomRightY = border + cornerSize + (9 * verticalHeight);
        
        // Casa 0 (canto inferior direito - Início)
        spaceCoordinates.put(0, new Point(bottomRightX, bottomRightY));
        
        // Linha inferior (1-9): da direita para esquerda
        for (int i = 1; i <= 9; i++) {
            int x = bottomRightX - (i * horizontalWidth);
            int y = bottomRightY;
            spaceCoordinates.put(i, new Point(x, y));
        }
        
        // Casa 10 (canto inferior esquerdo - Prisão)
        spaceCoordinates.put(10, new Point(border, bottomRightY));
        
        // Linha esquerda (11-19): de baixo para cima
        for (int i = 11; i <= 19; i++) {
            int x = border;
            int y = bottomRightY - ((i - 10) * verticalHeight);
            spaceCoordinates.put(i, new Point(x, y));
        }
        
        // Casa 20 (canto superior esquerdo - Parada Livre)
        spaceCoordinates.put(20, new Point(border, border));
        
        // Linha superior (21-29): da esquerda para direita
        for (int i = 21; i <= 29; i++) {
            int x = border + cornerSize + ((i - 21) * horizontalWidth);
            int y = border;
            spaceCoordinates.put(i, new Point(x, y));
        }
        
        // Casa 30 (canto superior direito - Vá para Prisão)
        spaceCoordinates.put(30, new Point(bottomRightX, border));
        
        // Linha direita (31-39): de cima para baixo
        for (int i = 31; i <= 39; i++) {
            int x = bottomRightX;
            int y = border + cornerSize + ((i - 31) * verticalHeight);
            spaceCoordinates.put(i, new Point(x, y));
        }
        
        // Validação: verificar que todas as coordenadas estão dentro dos limites
        for (Map.Entry<Integer, Point> entry : spaceCoordinates.entrySet()) {
            Point p = entry.getValue();
            if (p.x < 0 || p.x > 700 || p.y < 0 || p.y > 700) {
                System.err.println("AVISO: Coordenada fora dos limites para casa " 
                    + entry.getKey() + ": (" + p.x + ", " + p.y + ")");
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 1. Desenhar tabuleiro
        drawBoard(g2d);
        
        // 2. Desenhar piões
        drawPawns(g2d);
        
        // 3. Desenhar dados
        drawDice(g2d);
        
        // 4. Desenhar informações do jogador
        drawPlayerInfo(g2d);
        
        // 5. Desenhar carta da propriedade atual
        drawCurrentPropertyCard(g2d);
    }
    
    private void drawBoard(Graphics2D g2d) {
        BufferedImage boardImage = imageCache.get("tabuleiro");
        if (boardImage != null) {
            // Desenhar imagem no tamanho exato do painel
            g2d.drawImage(boardImage, 0, 0, 700, 700, null);
        } else {
            // Fallback se imagem não carregar
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, 700, 700);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Tabuleiro não carregado", 300, 350);
        }
    }
    
    private void drawPawns(Graphics2D g2d) {
        Map<Integer, Integer> playerPositions = gameState.getAllPlayerPositions();
        
        java.util.List<PlayerStatusInfo> allStatus = gameState.getAllPlayerStatusInfo();//TODO linha feia, importar corretamente depois para cada pacote
        
        if (playerPositions == null || playerPositions.isEmpty() || 
                allStatus == null || allStatus.isEmpty() ||
                playerPositions.size() != allStatus.size()) {
                return; // Ainda não carregou
            }
        
        for (Map.Entry<Integer, Integer> entry : playerPositions.entrySet()) {
            int playerIndex = entry.getKey();
            int spaceIndex = entry.getValue();
            
            Point coords = spaceCoordinates.get(spaceIndex);
            if (coords == null) continue;
            
            // Pegar o status do jogador atual (pelo índice)
            model.core.entities.ModelFacade.PlayerStatusInfo playerStatus = allStatus.get(playerIndex);
            
            //Pegar o nome da cor que ele escolheu
            String colorName = playerStatus.color; // Etipo: "VERMELHO", "AZUL", etc.
            
            //Traduzir o nome da cor para o nome do arquivo do pino
            String pinFilename = getPinFilenameFromColor(colorName);
            
            //  Buscar a imagem correta do cache
            BufferedImage pinImage = imageCache.get(pinFilename);
            
            if (pinImage != null) {
                // Offset para múltiplos piões na mesma casa
                int offsetX = (playerIndex % 3) * 20;
                int offsetY = (playerIndex / 3) * 20;
                
                g2d.drawImage(pinImage, 
                              coords.x + offsetX, 
                              coords.y + offsetY, 
                              30, 30,  // tamanho do pião
                              null);
            }
        }
    }
    
    /**
     * Mapeia o nome da cor (em Português) para o nome do arquivo de imagem do pino.
     * Baseado na sua descrição:
     * pin0: Vermelho, pin1: Azul, pin2: Laranja, 
     * pin3: Amarelo, pin4: Roxo, pin5: Verde.
     */
    private String getPinFilenameFromColor(String colorName) {
        if (colorName == null) {
            return "pin0"; // Padrão (Vermelho)
        }
        
        switch (colorName.toUpperCase()) {
            case "VERMELHO":
                return "pin0";
            case "AZUL":
                return "pin1";
            case "LARANJA":
                return "pin2";
            case "AMARELO":
                return "pin3";
            case "ROXO":
                return "pin4";
            case "VERDE":
                return "pin5";
            default:
                // Se a cor não for mapeada (ex: "Pink"), usa o pin0 - so para nao quebrar o codigo, mas isso nao deve ocorrer
                return "pin0";
        }
    }
    
    private void drawDice(Graphics2D g2d) {
        int[] diceRoll = gameState.getLastDiceRoll();
        
        // Se ainda não rolou os dados, não desenha
        if (diceRoll[0] == 0 && diceRoll[1] == 0) {
            return;
        }
        
        BufferedImage die1 = imageCache.get("die" + diceRoll[0]);
        BufferedImage die2 = imageCache.get("die" + diceRoll[1]);
        
        // Posição dos dados (centro do tabuleiro)
        int diceX = 250;
        int diceY = 300;
        int diceSize = 60;
        
        // Desenhar fundo branco semi-transparente
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(diceX - 10, diceY - 10, 180, 80, 10, 10);
        
        // Desenhar dados
        if (die1 != null) {
            g2d.drawImage(die1, diceX, diceY, diceSize, diceSize, null);
        }
        if (die2 != null) {
            g2d.drawImage(die2, diceX + 80, diceY, diceSize, diceSize, null);
        }
    }
    
    private void drawPlayerInfo(Graphics2D g2d) {
        String playerName = gameState.getCurrentPlayerName();
        if (playerName == null) return;
        
        int infoY = 710;
        int balance = gameState.getCurrentPlayerBalance();
        
        // Fundo (vermelho se falência, cinza normal)
        if (balance < 0) {
            g2d.setColor(new Color(255, 200, 200)); // Vermelho claro
        } else {
            g2d.setColor(new Color(240, 240, 240)); // Cinza claro
        }
        g2d.fillRect(0, 700, 1000, 100);
        
        // Nome do jogador
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Jogador: " + playerName, 10, infoY + 20);
        
        // Saldo (vermelho se negativo)
        if (balance < 0) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("FALÊNCIA! Saldo: $" + balance, 250, infoY + 20);
        } else {
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Saldo: $" + balance, 250, infoY + 20);
        }
        
        // Casa atual
        g2d.setColor(Color.BLACK);
        String spaceName = gameState.getCurrentSpaceName();
        if (spaceName != null) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Casa: " + spaceName, 10, infoY + 40);
        }
        
        // Lista de propriedades
        java.util.List<String> properties = gameState.getCurrentPlayerProperties();
        if (properties != null && !properties.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.setColor(Color.BLACK);
            
            // Mostrar até 5 propriedades
            int maxToShow = Math.min(5, properties.size());
            String propertiesText = String.join(", ", properties.subList(0, maxToShow));
            if (properties.size() > 5) {
                propertiesText += "... (+" + (properties.size() - 5) + ")";
            }
            
            g2d.drawString("Propriedades (" + properties.size() + "): " + propertiesText, 
                          10, infoY + 60);
        }
        
        // Mensagem (destaque se falência)
        String message = gameState.getMessage();
        if (message != null && !message.isEmpty()) {
            if (balance < 0) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
            } else {
                g2d.setColor(Color.BLUE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
            }
            g2d.drawString(message, 10, infoY + 80);
        }
        
        // Instrução de eliminação se falência
        if (balance < 0) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.drawString("Pressione E para eliminar ou V para vender propriedades", 500, infoY + 80);
        }
    }
    
    private void drawCurrentPropertyCard(Graphics2D g2d) {
        if (controller == null) return;
        
        PropertyInfo propertyInfo = controller.getCurrentPropertyInfo();
        if (propertyInfo == null) return;
        
        // Tentar carregar imagem da propriedade
        BufferedImage cardImage = imageCache.get(propertyInfo.name);
        
        int cardX = 720;
        int cardY = 20;
        
        if (cardImage != null) {
            // Desenhar imagem completa na resolução normal
            g2d.drawImage(cardImage, cardX, cardY, null);
            
            int infoY = cardY + cardImage.getHeight() + 10;
            
            g2d.setColor(Color.BLACK);
            
            int totalValue = propertyInfo.totalValue;
            int sellValue = (int)(totalValue * 0.9);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Preço Total: $" + totalValue, cardX, infoY);
            infoY += 15; // Pula linha
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 11)); // Fonte menor
            g2d.drawString("Preço de Venda (90%): $" + sellValue, cardX, infoY);
            infoY += 20; // Pula linha com espaço
            
            g2d.setFont(new Font("Arial", Font.BOLD, 12)); // Restaura fonte
            
            
            // Usamos um ArrayList para o caso de termos 2 linhas (casas e hotel)
            java.util.ArrayList<String> buildingInfoLines = new java.util.ArrayList<>(); //TODO melhorar imports aqui tbm
            
            //Verifica se tem hotel
            if (propertyInfo.hasHotel) {
                buildingInfoLines.add("Construção: 1 Hotel");
            }
            
            //  vverifica se tem casas 
            if (propertyInfo.houses > 0) {
                buildingInfoLines.add("Construção: " + propertyInfo.houses + " Casa(s)");
            }
            
            // Desenha as linhas que foram adicionadas
            if (!buildingInfoLines.isEmpty()) {
                for (String line : buildingInfoLines) {
                    g2d.drawString(line, cardX, infoY);
                    infoY += 15; // Pula uma linha para cada item
                }
            }
            
            if (propertyInfo.ownerName != null) {
                // Verificar se é do jogador atual
                String currentPlayer = gameState.getCurrentPlayerName();
                
                if (propertyInfo.ownerName.equals(currentPlayer)) {
                    g2d.setColor(new Color(0, 100, 200));
                    g2d.drawString("Sua propriedade!", cardX, infoY);
                    
                    g2d.setFont(new Font("Arial", Font.BOLD, 10));
                    g2d.drawString("H: Construir Casa", cardX, infoY + 15);
                    g2d.drawString("V: Vender (90%)", cardX, infoY + 30);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.drawString("Dono: " + propertyInfo.ownerName, cardX, infoY);
                }
            } else {
                g2d.setColor(new Color(0, 150, 0));
                g2d.drawString("Disponível!", cardX, infoY);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                g2d.drawString("C: Comprar", cardX, infoY + 15);
            }
        } else { //TODO: refatorar aqui
            // Placeholder se não houver imagem (150x200)
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(cardX, cardY, 150, 200);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(cardX, cardY, 150, 200);
            
            // Desenhar informações no placeholder
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(propertyInfo.name, cardX + 10, cardY + 30);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Preço: $" + propertyInfo.cost, cardX + 10, cardY + 60);
            g2d.drawString("Aluguel: $" + propertyInfo.rent, cardX + 10, cardY + 80);
            
            int buildY = cardY + 95;
            
            if (propertyInfo.hasHotel) {
                g2d.drawString("Construção: 1 Hotel", cardX + 10, buildY);
                buildY += 15; // Move para a próxima linha
            }
            
            if (propertyInfo.houses > 0) {
                g2d.drawString("Construção: " + propertyInfo.houses + " Casa(s)", cardX + 10, buildY);
            }
            
            
            if (propertyInfo.ownerName != null) {
                String currentPlayer = gameState.getCurrentPlayerName();
                
                if (propertyInfo.ownerName.equals(currentPlayer)) {
                    g2d.setColor(new Color(0, 100, 200));
                    g2d.drawString("Sua!", cardX + 10, cardY + 140); 
                    g2d.setFont(new Font("Arial", Font.BOLD, 9));
                    g2d.drawString("H: Casa V: Vender", cardX + 10, cardY + 155); 
                } else {
                    g2d.setColor(Color.RED);
                    g2d.drawString("Dono: " + propertyInfo.ownerName, cardX + 10, cardY + 140); 
                }
            } else {
                g2d.setColor(new Color(0, 150, 0));
                g2d.drawString("Disponível!", cardX + 10, cardY + 140);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString("C: Comprar", cardX + 10, cardY + 155);
            }
        }
    }
}

package model.core.entities;

import java.util.*;

public class ModelFacade {
    private Board board;
    private Bank bank;
    private List<Player> players;
    private int currentPlayerIndex;
    private Dice dice1;
    private Dice dice2;
    private int[] lastDiceRoll;
    private String lastEventMessage;
    
    public ModelFacade() {
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
        Space startSpace = board.getStartSpace();
        
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
        
        // Process dice roll to check for consecutive doubles
        boolean shouldGoToPrison = currentPlayer.processDiceRoll(lastDiceRoll[0], lastDiceRoll[1]);
        
        if (shouldGoToPrison) {
            // Send player to prison for 3 consecutive doubles
            Prison prisonSpace = board.getPrisonSpace();
            currentPlayer.sendToPrison(prisonSpace);
            lastEventMessage = "3 duplas consecutivas! Você foi enviado para a PRISÃO!";
            return;
        }
        
        // Get start space and current position before moving
        Space startSpace = board.getStartSpace();
        Space positionBeforeMove = currentPlayer.getCar().getPosition();
        
        // Move the car
        currentPlayer.getCar().advancePosition(steps);
        
        // Check if passed over start (but didn't land on it)
        Space finalPosition = currentPlayer.getCar().getPosition();
        boolean passedStart = checkPassedStart(positionBeforeMove, finalPosition, startSpace, steps);
        
        // Apply start pass bonus if player passed over start (but didn't land on it)
        if (passedStart) {
            Start start = (Start) startSpace;
            currentPlayer.credit(start.getPassBonus());
        }
        
        // Executa evento da casa e captura mensagem
        lastEventMessage = finalPosition.event(currentPlayer);
    }
    
    /**
     * Checks if the player passed over the start space during movement
     */
    private boolean checkPassedStart(Space startPosition, Space endPosition, Space startSpace, int steps) {
        // If landed on start, didn't pass over it
        if (endPosition == startSpace) {
            return false;
        }
        
        // Traverse the path taken and check if we crossed start
        Space current = startPosition;
        for (int i = 0; i < steps; i++) {
            current = current.getNext();
            // If we hit start before the last step, we passed over it
            if (current == startSpace && i < steps - 1) {
                return true;
            }
        }
        
        return false;
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
    
    /**
     * Verifica se o jogador atual está em situação de falência
     */
    public boolean isCurrentPlayerBankrupt() {
        Player currentPlayer = players.get(currentPlayerIndex);
        return currentPlayer.getBalance() < 0;
    }
    
    /**
     * Conta quantos jogadores ainda estão ativos (saldo >= 0)
     */
    public int countActivePlayers() {
        int count = 0;
        for (Player player : players) {
            if (player.getBalance() >= 0) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Retorna o nome do vencedor (último jogador ativo)
     */
    public String getWinnerName() {
        for (Player player : players) {
            if (player.getBalance() >= 0) {
                return player.getName();
            }
        }
        return null;
    }
    
    /**
     * Elimina o jogador atual (saldo negativo sem como pagar)
     */
    public void eliminateCurrentPlayer() {
        Player currentPlayer = players.get(currentPlayerIndex);
        
        // Devolver todas as propriedades ao banco
        List<Property> playerProperties = new ArrayList<>(currentPlayer.getLiquidAssets());
        for (Property prop : playerProperties) {
            currentPlayer.sellProperty(prop);
            bank.returnPropertyToBank(prop);
        }
        
        // Marcar como eliminado (saldo muito negativo)
        currentPlayer.debit(1000000);
    }
    
    // ===== GETTERS PARA CONTROLLER (retornam tipos simples) =====
    
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
    
    public String getLastEventMessage() {
        return lastEventMessage != null ? lastEventMessage : "";
    }
    
    /**
     * Retorna informações de posição de todos os jogadores
     * Formato: Map<índice do jogador, índice da casa no tabuleiro>
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
        for (int i = 0; i < board.getBoardSize(); i++) {
            Space space = board.getSpace(i);
            if (space instanceof Property) {
                properties.add((Property) space);
            }
        }
        return properties;
    }
    
    private int findSpaceIndex(Space space) {
        for (int i = 0; i < board.getBoardSize(); i++) {
            if (board.getSpace(i) == space) {
                return i;
            }
        }
        return 0;
    }
    
    /**
     * Retorna lista detalhada das propriedades do jogador atual
     */
    public List<PropertyDetails> getCurrentPlayerPropertyDetails() {
        Player currentPlayer = players.get(currentPlayerIndex);
        List<PropertyDetails> details = new ArrayList<>();
        
        for (Property prop : currentPlayer.getLiquidAssets()) {
            int houses = 0;
            boolean hasHotel = false;
            
            if (prop instanceof Place) {
                Place place = (Place) prop;
                houses = place.getNumOfHouses();
                hasHotel = place.getNumOfHotels() > 0;
            }
            
            details.add(new PropertyDetails(
                prop.getName(),
                prop.getCost(),
                prop.getCurrentRent(),
                houses,
                hasHotel
            ));
        }
        
        return details;
    }
    
    /**
     * Tenta construir casa na propriedade atual
     */
    public boolean buildHouseOnCurrentProperty() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Space currentSpace = currentPlayer.getCar().getPosition();
        
        if (!(currentSpace instanceof Place)) {
            return false;
        }
        
        Place place = (Place) currentSpace;
        
        // Verificar se o jogador é dono
        if (!place.isOwned() || place.getOwner() != currentPlayer) {
            return false;
        }
        
        // Verificar se pode construir casa
        if (!place.canBuildHouse()) {
            return false;
        }
        
        // Verificar se tem dinheiro
        int housePrice = place.getHousePrice();
        if (currentPlayer.getBalance() < housePrice) {
            return false;
        }
        
        // Construir casa
        place.buildHouse();
        currentPlayer.debit(housePrice);
        return true;
    }
    
    /**
     * Vende propriedade atual ao banco por 90% do valor
     */
    public boolean sellCurrentPropertyToBank() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Space currentSpace = currentPlayer.getCar().getPosition();
        
        if (!(currentSpace instanceof Property)) {
            return false;
        }
        
        Property property = (Property) currentSpace;
        
        // Verificar se o jogador é dono
        if (!property.isOwned() || property.getOwner() != currentPlayer) {
            return false;
        }
        
        // Calcular valor de venda (90%)
        int sellValue = (int) (property.getCost() * 0.9);
        
        // Vender ao banco
        currentPlayer.sellProperty(property);
        currentPlayer.credit(sellValue);
        bank.returnPropertyToBank(property);
        
        return true;
    }
    
    // ===== CLASSES INTERNAS PARA TRANSFERIR DADOS =====
    
    /**
     * Classe para transferir informações de propriedade para Controller/View
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
    
    /**
     * Classe para transferir detalhes de propriedade (incluindo construções)
     */
    public static class PropertyDetails {
        public final String name;
        public final int cost;
        public final int rent;
        public final int houses;
        public final boolean hasHotel;
        
        public PropertyDetails(String name, int cost, int rent, int houses, boolean hasHotel) {
            this.name = name;
            this.cost = cost;
            this.rent = rent;
            this.houses = houses;
            this.hasHotel = hasHotel;
        }
    }
}


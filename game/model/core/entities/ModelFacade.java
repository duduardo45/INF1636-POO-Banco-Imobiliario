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
    private boolean hasBuiltThisTurn = false;
    private Property propertyJustBought = null;
    private boolean hasTradedThisTurn = false;
    private boolean diceRolledThisTurn = false;
    
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
        
        // Configure the shared luck deck with players list
        LuckDeck sharedDeck = BoardInitializer.getSharedLuckDeck();
        if (sharedDeck != null) {
            sharedDeck.setAllPlayers(this.players);
        }
        
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Rola os dados (aleatório)
     */
    public int[] rollDice() {
        this.diceRolledThisTurn = true;
        int d1 = dice1.roll();
        int d2 = dice2.roll();
        this.lastDiceRoll[0] = d1;
        this.lastDiceRoll[1] = d2;
        return lastDiceRoll;
    }
    

    public boolean hasDiceRolled() {
        return diceRolledThisTurn;
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
        
        if (finalPosition instanceof Company) {
            // Se for, injetamos o valor dos dados (steps) nela
            ((Company) finalPosition).setDiceRollForRent(steps);
        }
        // Executa evento da casa e captura mensagem
        lastEventMessage = finalPosition.event(currentPlayer);
    }

    /**
     * Retorna uma lista com o(s) nome(s) do(s) jogador(es) com mais dinheiro.
     * Usado para encerramento abrupto do jogo.
     */
    public List<String> getRichestPlayers() {
        List<String> winners = new ArrayList<>();
        int maxBalance = Integer.MIN_VALUE;//Usamos esse valor para o caso extremo de todos os jogadores terem saldo mega negativo.
        
        // 1. Encontra o maior saldo
        for (Player p : players) {
            if (p.getBalance() > maxBalance) {
                maxBalance = p.getBalance();
            }
        }
        
        // 2. Coleta todos os jogadores que têm esse saldo (empate)
        for (Player p : players) {
            if (p.getBalance() == maxBalance) {
                winners.add(p.getName());
            }
        }
        
        return winners;
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
        this.propertyJustBought = property;
        
        return true;
    }
    
    /**
     * Passa para o próximo jogador
     */
    public void nextTurn() {
    	this.hasBuiltThisTurn = false;
        this.propertyJustBought = null;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        this.diceRolledThisTurn = false;
    }
    
    /**
     * Verifica se o jogador atual está em situação de falência
     */
    public boolean isCurrentPlayerBankrupt() {
        Player currentPlayer = players.get(currentPlayerIndex);
        return currentPlayer.getBalance() < 0;
    }
    
    /**
     * Conta quantos jogadores ainda estão no jogo.
     * (Atualizado para usar o tamanho da lista, já que removemos os falidos)
     */
    public int countActivePlayers() {
        return players.size();
    }
    
    /**
     * Retorna o nome do vencedor (o único que sobrou na lista).
     */
    public String getWinnerName() {
        if (!players.isEmpty()) {
            // Retorna o único jogador restante
            return players.get(0).getName();
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
            prop.setOwner(null);
        }
        
        // Marcar como eliminado (saldo muito negativo)
        // currentPlayer.debit(1000000);
        players.remove(currentPlayerIndex);
        currentPlayerIndex--;
        if (currentPlayerIndex < 0) {
            currentPlayerIndex = 0;
        }

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
    
    /**
     * Checks if the last dice roll was a double
     */
    public boolean wasLastRollDouble() {
        return lastDiceRoll[0] == lastDiceRoll[1];
    }
    
    /**
     * Checks if the current player was sent to prison due to 3 consecutive doubles
     */
    public boolean wasPlayerSentToPrisonForDoubles() {
        Player currentPlayer = players.get(currentPlayerIndex);
        return currentPlayer.isInPrison() && currentPlayer.getConsecutiveDoubles() == 0;
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
            
            int houses = 0;
            boolean hasHotel = false;
            boolean isPlace = false;
            boolean canBuildHouse = false;
            boolean canBuildHotel = false;
            int currentPrice = prop.getCost();
            
            // Verifica se esta propriedade é um "Place" (onde se pode construir)
            if (prop instanceof Place) {
                Place place = (Place) prop;
                houses = place.getNumOfHouses(); 
                hasHotel = place.getNumOfHotels() > 0;
                currentPrice = place.getTotalValue();
                isPlace = true;
                if (place.isOwned() && place.getOwner() == currentPlayer && !this.hasBuiltThisTurn) {//regra de limite por turno
                     // Verifica também a regra do "propertyJustBought"
                     boolean justBought = (this.propertyJustBought != null && this.propertyJustBought == place);
                     
                     if (!justBought) {
                         canBuildHouse = place.canBuildHouse();
                         canBuildHotel = place.canBuildHotel();
                     }
                }
            }
            return new PropertyInfo(
                    prop.getName(),
                    prop.getCost(),
                    prop.isOwned() ? prop.getOwner().getName() : null,
                    prop.getCurrentRent(),
                    houses,          
                    hasHotel,
                    currentPrice,
                    isPlace,
                    canBuildHouse,  
                    canBuildHotel
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
    	
    	if (this.hasBuiltThisTurn) {
            return false; // Erro: Jogador já construiu nesta rodada.
        }
    	
        Player currentPlayer = players.get(currentPlayerIndex);
        Space currentSpace = currentPlayer.getCar().getPosition();
        
        if (this.propertyJustBought != null && this.propertyJustBought == currentSpace) {
            return false; // Erro: Não pode construir na mesma rodada que comprou.
        }
        
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
        
        this.hasBuiltThisTurn = true;
        
        return true;
    }

    /**
     * Tenta construir HOTEL na propriedade atual - Função bem parecida com a de construir casa, mas com regras adicionais fornecidas pelo Model.
     */
    public boolean buildHotelOnCurrentProperty() {
        // Regra de Limite por Turno
        if (this.hasBuiltThisTurn) {
            return false; // Já construiu algo nesta rodada
        }
        
        Player currentPlayer = players.get(currentPlayerIndex);
        Space currentSpace = currentPlayer.getCar().getPosition();
        
        // Regra de Compra Recente
        if (this.propertyJustBought != null && this.propertyJustBought == currentSpace) {
            return false; // Não pode construir na mesma rodada que comprou
        }
        
        if (!(currentSpace instanceof Place)) {
            return false;
        }
        
        Place place = (Place) currentSpace;
        
        // Verificações de Dono
        if (!place.isOwned() || place.getOwner() != currentPlayer) {
            return false;
        }
        
        // Verifica se a regra do Place permite hotel (ex: ter pelo menos 1 casa)
        if (!place.canBuildHotel()) {
            return false;
        }
        
        // Verifica Saldo
        int hotelPrice = place.getHotelPrice();
        if (currentPlayer.getBalance() < hotelPrice) {
            return false;
        }
        
        // Executa a construção
        place.buildHotel();
        currentPlayer.debit(hotelPrice);
        
        // Marca que já construiu nesta rodada
        this.hasBuiltThisTurn = true;
        
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
        
        if (this.propertyJustBought != null && this.propertyJustBought == property) {
            // Se acabou de comprar nesta rodada, não pode vender com 'V'
            // (Você pode retornar false ou setar uma mensagem de erro no lastEventMessage se quiser)
            lastEventMessage = "Não pode vender imóvel recém-comprado!";
            return false;
        }

        int totalValue = property.getCost();
        if (property instanceof Place) {
            totalValue = ((Place) property).getTotalValue();
        }
        
        int sellValue = (int) (totalValue * 0.9);
        
        // Vender ao banco
        currentPlayer.sellProperty(property);
        currentPlayer.credit(sellValue);
        property.setOwner(null);
        bank.returnPropertyToBank(property);
        
        return true;
    }

    /**
     * Retorna uma lista de pares (Nome, Valor de Venda) das propriedades do jogador atual.
     * Usado para preencher o menu de venda.
     */
    public Map<String, Integer> getCurrentPlayerSellableProperties() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Map<String, Integer> sellableProps = new HashMap<>();
        
        for (Property prop : currentPlayer.getLiquidAssets()) {
            // CALCULA O VALOR TOTAL (Terreno + Construções)
            int totalValue = prop.getCost(); // Valor base
            
            if (prop instanceof Place) {
                totalValue = ((Place) prop).getTotalValue();
            }
            
            // Regra: 90% do valor total
            int sellValue = (int) (totalValue * 0.9);
            sellableProps.put(prop.getName(), sellValue);
        }
        return sellableProps;

    }

    /**
     * Vende uma propriedade específica pelo nome.
     * Retorna Strings de erro ou sucesso para exibir na tela.
     */
    public String sellPropertyByName(String propertyName) {
        Player currentPlayer = players.get(currentPlayerIndex);
        
        // 1. Encontrar a propriedade na lista do jogador
        Property targetProp = null;
        for (Property prop : currentPlayer.getLiquidAssets()) {
            if (prop.getName().equals(propertyName)) {
                targetProp = prop;
                break;
            }
        }
        
        if (targetProp == null) {
            return "Erro: Propriedade não encontrada.";
        }
        
        // 2. IMPEDIR LOOP: Verifica se a propriedade foi comprada NESTA rodada
        if (this.propertyJustBought != null && this.propertyJustBought == targetProp) {
            return "Não é permitido vender uma propriedade no mesmo turno em que foi comprada.";
        }
        
        // 3. Calcular valor de venda (90% do TOTAL)
        int totalValue = targetProp.getCost();
        if (targetProp instanceof Place) {
            totalValue = ((Place) targetProp).getTotalValue();
        }
        int sellValue = (int) (totalValue * 0.9);
        
        // 4. Realizar a venda
        currentPlayer.sellProperty(targetProp);
        currentPlayer.credit(sellValue);
        bank.returnPropertyToBank(targetProp);
        
        // Resetar dono
        targetProp.setOwner(null);
        
        return "Vendida " + propertyName + " por $" + sellValue;
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
        public final int houses;
        public final boolean hasHotel;
        public final int totalValue;
        public final boolean isPlace;       
        public final boolean canBuildHouse; 
        public final boolean canBuildHotel;
        
        public PropertyInfo(String name, int cost, String ownerName, int rent, int houses, boolean hasHotel, int totalValue, boolean isPlace, boolean canBuildHouse, boolean canBuildHotel) {
            this.name = name;
            this.cost = cost;
            this.ownerName = ownerName;
            this.rent = rent;
            this.houses = houses;
            this.hasHotel = hasHotel;
            this.totalValue = totalValue;
            this.isPlace = isPlace;
            this.canBuildHouse = canBuildHouse;
            this.canBuildHotel = canBuildHotel;
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
    
    /**
     * Classe para transferir status de um jogador para Controller/View
     */
    public static class PlayerStatusInfo {
        public final String name;
        public final String color;
        public final int balance;
        public final String spaceName;
    
        public PlayerStatusInfo(String name, String color, int balance, String spaceName) {
            this.name = name;
            this.color = color;
            this.balance = balance;
            this.spaceName = spaceName;
        }
    }
   
   /**
    * Classe para transferir informações de carta de sorte para Controller/View
    */
   public static class LuckCardInfo {
       public final String imageId;
       public final String type;
       public final String story;
       
       public LuckCardInfo(String imageId, String type, String story) {
           this.imageId = imageId;
           this.type = type;
           this.story = story;
       }
   }
    
    /**
    * Retorna lista com status de TODOS os jogadores
    */
   public List<PlayerStatusInfo> getAllPlayerStatusInfo() {
       List<PlayerStatusInfo> allStatus = new ArrayList<>();
       
       // Loop por todos os jogadores, não apenas o atual
       for (Player player : players) {
           String playerName = player.getName();
           String playerColor = player.getCar().getColor();
           int playerBalance = player.getBalance();
           String playerSpaceName = player.getCar().getPosition().getName();
           
           allStatus.add(new PlayerStatusInfo(
               playerName,
               playerColor,
               playerBalance,
               playerSpaceName
           ));
       }
       
       return allStatus;
   }
   
   public int[] rollDiceManual(int d1, int d2) {
       this.diceRolledThisTurn = true;     
       // Pede para cada dado assumir um valor específico
       int result1 = dice1.rollFixed(d1);
       int result2 = dice2.rollFixed(d2);
       
       // Atualiza o histórico da jogada
       this.lastDiceRoll[0] = result1;
       this.lastDiceRoll[1] = result2;
       
       return lastDiceRoll;
   }
   
   /**
    * Retorna um mapa de todas as propriedades com seus donos
    * Formato: Map<índice da casa, nome do dono (ou null se sem dono)>
    */
   public Map<Integer, String> getAllPropertiesWithOwners() {
       Map<Integer, String> propertiesMap = new HashMap<>();
       
       for (int i = 0; i < board.getBoardSize(); i++) {
           Space space = board.getSpace(i);
           if (space instanceof Property) {
               Property prop = (Property) space;
               String ownerName = prop.isOwned() ? prop.getOwner().getName() : null;
               propertiesMap.put(i, ownerName);
           }
       }
       
       return propertiesMap;
   }
   
   /**
    * Retorna a cor do jogador pelo nome
    */
   public String getPlayerColorByName(String playerName) {
       for (Player player : players) {
           if (player.getName().equals(playerName)) {
               return player.getCar().getColor();
           }
       }
       return null;
   }
   
   /**
    * Retorna informações sobre a carta de sorte atual (se o jogador estiver em um LuckSpace)
    */
   public LuckCardInfo getCurrentLuckCardInfo() {
       Player currentPlayer = players.get(currentPlayerIndex);
       Space currentSpace = currentPlayer.getCar().getPosition();
       
       if (currentSpace instanceof LuckSpace) {
           LuckSpace luckSpace = (LuckSpace) currentSpace;
           LuckCard card = luckSpace.getCurrentCard();
           
           if (card != null) {
               return new LuckCardInfo(
                   card.getImageId(),
                   card.getType().toString(),
                   card.getStory()
               );
           }
       }
       return null;
   }
   
   /**
    * Checks if the current player is in prison
    */
   public boolean isCurrentPlayerInPrison() {
       Player currentPlayer = players.get(currentPlayerIndex);
       return currentPlayer.isInPrison();
   }
   
   /**
    * Releases the current player from prison
    */
   public void releasePlayerFromPrison() {
       Player currentPlayer = players.get(currentPlayerIndex);
       currentPlayer.releaseFromPrison();
   }
   
   /**
    * Increments the prison turn counter for the current player
    */
   public void incrementPlayerPrisonTurns() {
       Player currentPlayer = players.get(currentPlayerIndex);
       currentPlayer.incrementTurnsInPrison();
   }
   
   /**
    * Returns the number of turns the current player has spent in prison
    */
   public int getCurrentPlayerTurnsInPrison() {
       Player currentPlayer = players.get(currentPlayerIndex);
       return currentPlayer.getTurnsInPrison();
   }
   
   /**
    * Returns the current luck card object (if player is on a luck space)
    */
   public LuckCard getCurrentLuckCard() {
       Player currentPlayer = players.get(currentPlayerIndex);
       Space currentSpace = currentPlayer.getCar().getPosition();
       
       if (currentSpace instanceof LuckSpace) {
           LuckSpace luckSpace = (LuckSpace) currentSpace;
           return luckSpace.getCurrentCard();
       }
       return null;
   }
   
   /**
    * Returns the current player object
    */
   public Player getCurrentPlayer() {
       return players.get(currentPlayerIndex);
   }
   
   /**
    * Handles manual luck cards that require special processing
    * (e.g., GetOutPrisonCard needs to be given to the player)
    * 
    * @return A message describing what was done, or null if no action was taken
    */
   public String handleManualLuckCard() {
       LuckCard card = getCurrentLuckCard();
       
       if (card == null) {
           return null;
       }
       
       Player currentPlayer = players.get(currentPlayerIndex);
       
       // Check if it's a GetOutPrisonCard by checking the class name
       if (card.getClass().getSimpleName().equals("GetOutPrisonCard")) {
           // Cast to GetOutPrisonCard and give it to the player
           GetOutPrisonCard prisonCard = (GetOutPrisonCard) card;
           prisonCard.setOwner(currentPlayer);
           currentPlayer.receiveGetOutPrisonCard(prisonCard);
           
           return "Você recebeu uma carta 'Saída Livre da Prisão'!";
       }
       
       return null;
   }
   
   /**
    * Checks if the current player has a GetOutPrisonCard
    * 
    * @return true if the player has the card, false otherwise
    */
   public boolean hasGetOutPrisonCard() {
       Player currentPlayer = players.get(currentPlayerIndex);
       return currentPlayer.hasGetOutPrisonCard();
   }
   
   /**
    * Uses the GetOutPrisonCard to release the player from prison
    * 
    * @return true if the card was used successfully, false otherwise
    */
   public boolean useGetOutPrisonCard() {
       Player currentPlayer = players.get(currentPlayerIndex);
       
       if (!currentPlayer.hasGetOutPrisonCard()) {
           return false;
       }
       
       GetOutPrisonCard card = (GetOutPrisonCard) currentPlayer.useGetOutPrisonCard();
       if (card != null) {
           return card.use(currentPlayer);
       }
       
       return false;
   }
}


package controller;

import model.core.entities.ModelFacade;
import model.core.entities.ModelFacade.PropertyInfo;
import model.core.entities.ModelFacade.PropertyDetails;
import java.util.*;

public class GameController {
    private ModelFacade modelFacade;  // Acesso ao Model via Facade
    private GameState gameState;       // Estado observável para View
    
    public GameController() {
        this.modelFacade = new ModelFacade();
        this.gameState = GameState.getInstance();
    }
    
    /**
     * Inicializa novo jogo
     */
    public void startNewGame(List<String> playerNames, List<String> colors) {
        // Delega para ModelFacade
        modelFacade.initializeGame(playerNames.size(), playerNames, colors);
        
        // Atualiza GameState com informações do primeiro jogador
        updateGameState();
    }
    
    /**
     * Rola os dados e move o jogador atual
     */
    public void rollDice() {
        // Rola dados através do Facade
        int[] diceResults = modelFacade.rollDice();
        
        // Atualiza GameState com resultado dos dados
        gameState.setDiceRoll(diceResults[0], diceResults[1]);
        
        // Move jogador
        int total = diceResults[0] + diceResults[1];
        modelFacade.moveCurrentPlayer(total);
        
        // Captura mensagem do evento
        String eventMessage = modelFacade.getLastEventMessage();
        if (!eventMessage.isEmpty()) {
            gameState.setMessage(eventMessage);
        }
        
        // Atualiza GameState completo
        updateGameState();
        
        // Verifica falência após o movimento
        if (modelFacade.isCurrentPlayerBankrupt()) {
            gameState.setMessage("FALÊNCIA! Saldo negativo. Venda propriedades ou será eliminado!");
        }
    }
    
    /**
     * Compra propriedade atual
     */
    public boolean buyCurrentProperty() {
        boolean success = modelFacade.buyCurrentProperty();
        
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
        modelFacade.nextTurn();
        gameState.setMessage("");
        updateGameState();
    }
    
    /**
     * Retorna informações sobre a propriedade atual (se houver)
     */
    public PropertyInfo getCurrentPropertyInfo() {
        return modelFacade.getCurrentPropertyInfo();
    }
    
    /**
     * Constrói casa na propriedade atual
     */
    public boolean buildHouse() {
        boolean success = modelFacade.buildHouseOnCurrentProperty();
        
        if (success) {
            gameState.setMessage("Casa construída com sucesso!");
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível construir casa.");
        }
        
        return success;
    }
    
    /**
     * Vende propriedade atual ao banco
     */
    public boolean sellProperty() {
        boolean success = modelFacade.sellCurrentPropertyToBank();
        
        if (success) {
            gameState.setMessage("Propriedade vendida ao banco por 90%!");
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível vender a propriedade.");
        }
        
        return success;
    }
    
    /**
     * Retorna lista detalhada das propriedades
     */
    public List<PropertyDetails> getPlayerPropertyDetails() {
        return modelFacade.getCurrentPlayerPropertyDetails();
    }
    
    /**
     * Elimina o jogador atual por falência
     */
    public void eliminateCurrentPlayer() {
        modelFacade.eliminateCurrentPlayer();
        gameState.setMessage("Jogador eliminado por falência!");
        
        // Verificar se o jogo acabou
        if (modelFacade.countActivePlayers() <= 1) {
            String winner = modelFacade.getWinnerName();
            gameState.setMessage("FIM DE JOGO! Vencedor: " + winner);
            gameState.setGameOver(true);
            gameState.setWinner(winner);
        } else {
            endTurn();
        }
    }
    
    /**
     * Verifica se o jogo acabou
     */
    public boolean isGameOver() {
        return modelFacade.countActivePlayers() <= 1;
    }
    
    /**
     * Retorna o nome do vencedor
     */
    public String getWinner() {
        return modelFacade.getWinnerName();
    }
    
    /**
     * Atualiza GameState com dados do ModelFacade
     * Este método coleta informações do Model via Facade e atualiza o estado observável
     */
    private void updateGameState() {
        // Coleta informações do jogador atual
        String name = modelFacade.getCurrentPlayerName();
        int balance = modelFacade.getCurrentPlayerBalance();
        String color = modelFacade.getCurrentPlayerColor();
        List<String> properties = modelFacade.getCurrentPlayerProperties();
        
        // Atualiza GameState
        gameState.updateCurrentPlayer(name, balance, color, properties);
        
        // Atualiza casa atual
        String spaceName = modelFacade.getCurrentSpaceName();
        gameState.setCurrentSpaceName(spaceName);
        
        // Atualiza posições de todos os jogadores
        Map<Integer, Integer> positions = modelFacade.getAllPlayerPositions();
        gameState.setAllPlayerPositions(positions);
        List<ModelFacade.PlayerStatusInfo> allStatus = modelFacade.getAllPlayerStatusInfo();
        gameState.setAllPlayerStatusInfo(allStatus);
    }
    
    public GameState getGameState() {
        return gameState;
    }
}

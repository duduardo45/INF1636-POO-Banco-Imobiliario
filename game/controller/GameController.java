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
    
    private void log(String msg) {
        gameState.addLogMessage(msg);
    }
    /**
     * Rola os dados e move o jogador atual
     */
    public void rollDice() {
        // Reset shouldRollAgain at the start of a new roll
        gameState.setShouldRollAgain(false);
        
        // Rola dados através do Facade
        int[] diceResults = modelFacade.rollDice();
        
        // Atualiza GameState com resultado dos dados
        gameState.setDiceRoll(diceResults[0], diceResults[1]);
        
        // Verifica se o jogador está na prisão
        if (modelFacade.isCurrentPlayerInPrison()) {
            handlePrisonTurn(diceResults[0], diceResults[1]);
            return;
        }
        
        // Move jogador
        int total = diceResults[0] + diceResults[1];
        modelFacade.moveCurrentPlayer(total);
        
        // Captura mensagem do evento
        String eventMessage = modelFacade.getLastEventMessage();
        if (!eventMessage.isEmpty()) {
            log(modelFacade.getCurrentPlayerName() + ": " + eventMessage);
            gameState.setMessage(eventMessage);
        }
        else{
            log(modelFacade.getCurrentPlayerName() + " avançou para " + modelFacade.getCurrentSpaceName());
        }
        
        // Handle manual luck cards (e.g., GetOutPrisonCard)
        handleManualLuckCard();
        
        // Atualiza GameState completo
        updateGameState();
        
        // Verifica falência após o movimento
        if (modelFacade.isCurrentPlayerBankrupt()) {
            gameState.setMessage("FALÊNCIA! Saldo negativo. Venda propriedades ou será eliminado!");
        }
        
        // Verifica se rolou dupla - se sim, permite rolar novamente (mas não na 3ª dupla)
        if (modelFacade.wasLastRollDouble()) {
            // Check if player was sent to prison for 3 consecutive doubles
            if (modelFacade.wasPlayerSentToPrisonForDoubles()) {
                // 3rd double - player was sent to prison, no roll again
                gameState.setShouldRollAgain(false);
            } else {
                // 1st or 2nd double - allow roll again
                gameState.setShouldRollAgain(true);
                gameState.setMessage(gameState.getMessage() + " Dupla! Você pode rolar novamente!");
                log(modelFacade.getCurrentPlayerName() + " rolou dupla e pode rolar novamente!");
            }
        } else {
            gameState.setShouldRollAgain(false);
        }
    }
    
    public boolean hasDiceRolled() {
        return modelFacade.hasDiceRolled();
    }

    /**
     * Handles a player's turn while in prison
     */
    private void handlePrisonTurn(int dice1, int dice2) {
        String playerName = modelFacade.getCurrentPlayerName();
        
        // Check if player has a GetOutPrisonCard and use it automatically
        if (modelFacade.hasGetOutPrisonCard()) {
            if (modelFacade.useGetOutPrisonCard()) {
                log(playerName + " usou a carta 'Saída Livre da Prisão' e saiu da prisão!");
                gameState.setMessage("Você usou a carta 'Saída Livre da Prisão' e saiu da prisão!");
                
                // Now move the player
                int total = dice1 + dice2;
                modelFacade.moveCurrentPlayer(total);
                
                String eventMessage = modelFacade.getLastEventMessage();
                if (!eventMessage.isEmpty()) {
                    log(playerName + ": " + eventMessage);
                    gameState.setMessage(gameState.getMessage() + " " + eventMessage);
                }
                
                // Handle manual luck cards after moving
                handleManualLuckCard();
                updateGameState();
                return;
            }
        }
        
        // Check if rolled doubles
        if (dice1 == dice2) {
            modelFacade.releasePlayerFromPrison();
            log(playerName + " rolou dupla e saiu da prisão!");
            gameState.setMessage("Dupla! Você saiu da prisão e pode se mover!");
            
            // Now move the player
            int total = dice1 + dice2;
            modelFacade.moveCurrentPlayer(total);
            
            String eventMessage = modelFacade.getLastEventMessage();
            if (!eventMessage.isEmpty()) {
                log(playerName + ": " + eventMessage);
                gameState.setMessage(gameState.getMessage() + " " + eventMessage);
            }
            
            // Handle manual luck cards after moving
            handleManualLuckCard();
        } else {
            // Did not roll doubles - stay in prison
            modelFacade.incrementPlayerPrisonTurns();
            int turnsInPrison = modelFacade.getCurrentPlayerTurnsInPrison();
            
            if (turnsInPrison >= 3) {
                // After 3 turns, must be released
                modelFacade.releasePlayerFromPrison();
                log(playerName + " completou 3 turnos na prisão e foi liberado!");
                gameState.setMessage("Você completou 3 turnos na prisão e foi liberado!");
                
                // Now move the player
                int total = dice1 + dice2;
                modelFacade.moveCurrentPlayer(total);
                
                String eventMessage = modelFacade.getLastEventMessage();
                if (!eventMessage.isEmpty()) {
                    log(playerName + ": " + eventMessage);
                    gameState.setMessage(gameState.getMessage() + " " + eventMessage);
                }
                
                // Handle manual luck cards after moving
                handleManualLuckCard();
            } else {
                log(playerName + " não rolou dupla. Permanece na prisão (" + turnsInPrison + "/3 turnos)");
                gameState.setMessage("Não rolou dupla. Permanece na prisão (" + turnsInPrison + "/3 turnos)");
            }
        }
        
        updateGameState();
    }
    
    public void rollDiceManual(int totalSteps) {
        // Reset shouldRollAgain at the start of a new roll
        gameState.setShouldRollAgain(false);
        
        // Divide o valor total em dois dados para manter a consistência visual
        // Ex: Se o usuário quer andar 7, fazemos d1=3 e d2=4.
        int d1 = totalSteps / 2;
        int d2 = totalSteps - d1;
        
        // Garante que os dados visuais fiquem entre 1 e 6 para carregar a imagem correta.
        
        int visualD1 = (d1 > 6) ? 6 : (d1 < 1 ? 1 : d1);
        int visualD2 = (d2 > 6) ? 6 : (d2 < 1 ? 1 : d2);

        // 1. Atualiza os valores dos dados no Model (usando a nova lógica do Dice)
        modelFacade.rollDiceManual(visualD1, visualD2);
        
        // 2. Atualiza o GameState para a View desenhar os dados
        gameState.setDiceRoll(visualD1, visualD2);
        
        // 3. Verifica se o jogador está na prisão
        if (modelFacade.isCurrentPlayerInPrison()) {
            handlePrisonTurn(visualD1, visualD2);
            return;
        }
        
        // 4. Move o jogador o total de passos solicitado
        modelFacade.moveCurrentPlayer(totalSteps);
        
        // 5. Captura mensagem do evento (cair em propriedade, sorte, etc)
        String eventMessage = modelFacade.getLastEventMessage();
        if (!eventMessage.isEmpty()) {
            log(modelFacade.getCurrentPlayerName() + ": " + eventMessage);
            gameState.setMessage(eventMessage);
        }
        else {
            log(modelFacade.getCurrentPlayerName() + " avançou para " + modelFacade.getCurrentSpaceName());
        }
        
        // Handle manual luck cards (e.g., GetOutPrisonCard)
        handleManualLuckCard();
        
        // 6. Atualiza toda a tela
        updateGameState();
        
        // 7. Verifica falência
        if (modelFacade.isCurrentPlayerBankrupt()) {
            gameState.setMessage("FALÊNCIA! Saldo negativo. Venda propriedades ou será eliminado!");
        }
        
        // 8. Verifica se rolou dupla - se sim, permite rolar novamente (mas não na 3ª dupla)
        if (modelFacade.wasLastRollDouble()) {
            // Check if player was sent to prison for 3 consecutive doubles
            if (modelFacade.wasPlayerSentToPrisonForDoubles()) {
                // 3rd double - player was sent to prison, no roll again
                gameState.setShouldRollAgain(false);
            } else {
                // 1st or 2nd double - allow roll again
                gameState.setShouldRollAgain(true);
                gameState.setMessage(gameState.getMessage() + " Dupla! Você pode rolar novamente!");
                log(modelFacade.getCurrentPlayerName() + " rolou dupla e pode rolar novamente!");
            }
        } else {
            gameState.setShouldRollAgain(false);
        }
    }
    
    /**
     * Compra propriedade atual
     */
    public boolean buyCurrentProperty() {
        boolean success = modelFacade.buyCurrentProperty();
        
        if (success) {
            gameState.setMessage("Propriedade comprada com sucesso!");
            log(modelFacade.getCurrentPlayerName() + " comprou a propriedade " + modelFacade.getCurrentSpaceName());
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível comprar a propriedade.");
        }
        
        return success;
    }
    
    /**
     * Finaliza turno e passa para próximo jogador
     * Se o jogador rolou dupla, não passa para o próximo jogador
     */
    public void endTurn() {
        // Se o jogador pode rolar novamente (dupla), não passa o turno
        if (gameState.shouldRollAgain()) {
            gameState.setMessage("Você rolou dupla! Prepare-se para rolar novamente.");
            return;
        }
        
        // Caso contrário, passa para o próximo jogador
        modelFacade.nextTurn();
        gameState.setMessage("");
        gameState.setShouldRollAgain(false);
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
            log(modelFacade.getCurrentPlayerName() + " construiu uma casa na propriedade " + modelFacade.getCurrentSpaceName());
            gameState.setMessage("Casa construída com sucesso!");
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível construir casa.");
        }
        
        return success;
    }
    
    /**
     * Constrói um HOTEL na propriedade atual
     */
    public boolean buildHotel() {
        boolean success = modelFacade.buildHotelOnCurrentProperty();
        
        if (success) {
            log(modelFacade.getCurrentPlayerName() + " construiu um hotel na propriedade " + modelFacade.getCurrentSpaceName());
            gameState.setMessage("Hotel construído com sucesso!");
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível construir hotel.");
        }
        
        return success;
    }

    /**
     * Vende propriedade atual ao banco
     */
    public boolean sellProperty() {
        boolean success = modelFacade.sellCurrentPropertyToBank();
        
        if (success) {
            log(modelFacade.getCurrentPlayerName() + " vendeu a propriedade " + modelFacade.getCurrentSpaceName() + " ao banco.");
            gameState.setMessage("Propriedade vendida ao banco por 90%!");
            updateGameState();
        } else {
            gameState.setMessage("Não foi possível vender a propriedade.");
        }
        
        return success;
    }

    /**
     * Retorna mapa de propriedades que o jogador pode vender
     */
    public Map<String, Integer> getSellableProperties() {
        return modelFacade.getCurrentPlayerSellableProperties();
    }
    

    /**
     * Realiza a venda de uma propriedade específica
     */
    public void sellSpecificProperty(String propertyName) {
        String result = modelFacade.sellPropertyByName(propertyName);
        log(modelFacade.getCurrentPlayerName() + ": " + result);
        gameState.setMessage(result);
        updateGameState();
    }

    /**
     * Retorna lista detalhada das propriedades
     */
    public List<PropertyDetails> getPlayerPropertyDetails() {
        return modelFacade.getCurrentPlayerPropertyDetails();
    }

    /**
     * Encerra o jogo imediatamente e declara vencedor quem tiver mais dinheiro.
     */
    public void finishGameByTimeLimit() {
        List<String> winners = modelFacade.getRichestPlayers();
        String winnerText;
        
        if (winners.size() == 1) {
            // Apenas um vencedor
            winnerText = winners.get(0);
        } else {
            // Empate: "Os jogadores A, B venceram!"
            winnerText = "Os jogadores " + String.join(", ", winners) + " venceram (Empate)!";
        }
        
        // Atualiza o GameState para disparar o Fim de Jogo na View
        gameState.setMessage("Jogo Encerrado Manualmente.");
        gameState.setWinner(winnerText);
        gameState.setGameOver(true);
        updateGameState();
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
            gameState.setWinner(winner);
            gameState.setGameOver(true);
            updateGameState();
        } else {
            endTurn();
        }
    }
    
    /**
     * Verifica se o jogo acabou
     */
    public boolean isGameOver() {

        if (gameState.isGameOver()) {
            return true;
        }
        return modelFacade.countActivePlayers() <= 1;
    }
    
    /**
     * Retorna o nome do vencedor
     */
    public String getWinner() {
        String manualWinner = gameState.getWinner();
        if (manualWinner != null && !manualWinner.isEmpty()) {
            return manualWinner;
        }
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
    
    /**
     * Retorna um mapa de todas as propriedades com seus donos
     * Formato: Map<índice da casa, nome do dono (ou null se sem dono)>
     */
    public Map<Integer, String> getAllPropertiesWithOwners() {
        return modelFacade.getAllPropertiesWithOwners();
    }
    
    /**
     * Retorna a cor do jogador pelo nome
     */
    public String getPlayerColorByName(String playerName) {
        return modelFacade.getPlayerColorByName(playerName);
    }
    
    /**
     * Retorna informações sobre a carta de sorte atual (se houver)
     */
    public ModelFacade.LuckCardInfo getCurrentLuckCardInfo() {
        return modelFacade.getCurrentLuckCardInfo();
    }
    
    /**
     * Handles manual luck cards that require controller action
     * (e.g., GetOutPrisonCard needs to be given to the player)
     */
    public void handleManualLuckCard() {
        String result = modelFacade.handleManualLuckCard();
        
        if (result != null && !result.isEmpty()) {
            gameState.setMessage(result);
            log(modelFacade.getCurrentPlayerName() + ": " + result);
        }
    }
    
    /**
     * Uses the GetOutPrisonCard to escape prison
     */
    public boolean useGetOutPrisonCard() {
        if (modelFacade.useGetOutPrisonCard()) {
            String playerName = modelFacade.getCurrentPlayerName();
            log(playerName + " usou a carta 'Saída Livre da Prisão' e saiu da prisão!");
            gameState.setMessage("Você usou a carta e saiu da prisão!");
            updateGameState();
            return true;
        } else {
            gameState.setMessage("Você não tem uma carta 'Saída Livre da Prisão'!");
            return false;
        }
    }
}

package controller;

import java.util.*;
import model.core.entities.ModelFacade.PlayerStatusInfo;

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
    private boolean gameOver;
    private String winner;
    private List<PlayerStatusInfo> allPlayerStatusInfo;
    private List<String> logMessages;
    private boolean shouldRollAgain;
    
    // Singleton
    private GameState() {
        this.lastDiceRoll = new int[]{0, 0};
        this.currentPlayerProperties = new ArrayList<>();
        this.allPlayerStatusInfo = new ArrayList<>();
        this.allPlayerPositions = new HashMap<>();
        this.logMessages = new ArrayList<>();
        this.message = "";
        this.shouldRollAgain = false;
    }
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    public void addLogMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;
        
        this.logMessages.add(message);
        // Notifica a View que algo mudou
        setChanged();
        notifyObservers();
    }

// ===== SETTERS (chamados pelo Controller) =====

public void setAllPlayerStatusInfo(List<PlayerStatusInfo> allStatus) {
    this.allPlayerStatusInfo = allStatus;
    notifyObserversAndUpdate();
}

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
    
    public List<String> getLogMessages() {
        return new ArrayList<>(logMessages); // Retorna cópia para segurança
    }
    public List<PlayerStatusInfo> getAllPlayerStatusInfo() {
        return allPlayerStatusInfo;
    }
    
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
    
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        notifyObserversAndUpdate();
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public void setWinner(String winner) {
        this.winner = winner;
        notifyObserversAndUpdate();
    }
    
    public String getWinner() {
        return winner;
    }
    
    public void setShouldRollAgain(boolean shouldRoll) {
        this.shouldRollAgain = shouldRoll;
        notifyObserversAndUpdate();
    }
    
    public boolean shouldRollAgain() {
        return shouldRollAgain;
    }
    
    private void notifyObserversAndUpdate() {
        setChanged();
        notifyObservers();
    }
}

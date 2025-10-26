package controller;

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
    private boolean gameOver;
    private String winner;
    
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
    
    private void notifyObserversAndUpdate() {
        setChanged();
        notifyObservers();
    }
}

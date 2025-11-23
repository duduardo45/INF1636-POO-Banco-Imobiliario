package model.core.entities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * GameStateSaver - Serializes game state to plain text format
 * 
 * This class is responsible for saving the complete game state to a .txt file
 * in UTF-8 encoding. The format is human-readable and can be manually edited.
 * See SAVE_FORMAT.txt for complete format specification.
 */
public class GameStateSaver {
    
    /**
     * Saves the complete game state to a file
     * 
     * @param filePath Path to save the file
     * @param facade ModelFacade containing game state
     * @throws IOException If file writing fails
     * @throws IllegalStateException If trying to save after dice roll
     */
    public static void saveToFile(String filePath, ModelFacade facade) throws IOException {
        // Validate that we can save (dice not rolled)
        if (facade.getDiceRolledThisTurn()) {
            throw new IllegalStateException("Cannot save game after dice have been rolled. Please save at the start of a turn.");
        }
        
        StringBuilder content = new StringBuilder();
        
        // Header
        content.append("# Banco Imobiliário - Partida Salva\n");
        content.append("# Data: ").append(new Date().toString()).append("\n");
        content.append("# Encoding: UTF-8\n");
        content.append("\n");
        
        // Game State Section
        content.append(formatGameStateSection(facade));
        content.append("\n");
        
        // Players Section
        content.append(formatPlayersSection(facade));
        content.append("\n");
        
        // Properties Section
        content.append(formatPropertiesSection(facade));
        content.append("\n");
        
        // Log Section (historical messages)
        content.append(formatLogSection());
        
        // Write to file with UTF-8 encoding
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(content.toString());
        }
    }
    
    /**
     * Formats the [GAME_STATE] section
     */
    private static String formatGameStateSection(ModelFacade facade) {
        StringBuilder sb = new StringBuilder();
        sb.append("[GAME_STATE]\n");
        sb.append("CurrentPlayerIndex=").append(facade.getCurrentPlayerIndex()).append("\n");
        sb.append("HasBuiltThisTurn=").append(facade.getHasBuiltThisTurn()).append("\n");
        sb.append("DiceRolledThisTurn=").append(facade.getDiceRolledThisTurn()).append("\n");
        
        Property justBought = facade.getPropertyJustBought();
        sb.append("PropertyJustBought=").append(justBought != null ? justBought.getName() : "null").append("\n");
        
        int[] diceRoll = facade.getLastDiceRollArray();
        sb.append("LastDiceRoll=").append(diceRoll[0]).append(",").append(diceRoll[1]).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Formats the [PLAYERS] section
     */
    private static String formatPlayersSection(ModelFacade facade) {
        StringBuilder sb = new StringBuilder();
        sb.append("[PLAYERS]\n");
        
        List<Player> players = facade.getAllPlayers();
        sb.append("PlayerCount=").append(players.size()).append("\n");
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            sb.append(formatPlayerData(i, player, facade.getBoard()));
        }
        
        return sb.toString();
    }
    
    /**
     * Formats data for a single player
     */
    private static String formatPlayerData(int index, Player player, Board board) {
        StringBuilder sb = new StringBuilder();
        String prefix = "Player" + index + "_";
        
        sb.append(prefix).append("Name=").append(player.getName()).append("\n");
        sb.append(prefix).append("Color=").append(player.getCar().getColor()).append("\n");
        sb.append(prefix).append("Balance=").append(player.getBalance()).append("\n");
        
        // Find player position on board
        Space playerPosition = player.getCar().getPosition();
        int positionIndex = findSpaceIndex(board, playerPosition);
        sb.append(prefix).append("Position=").append(positionIndex).append("\n");
        
        sb.append(prefix).append("InPrison=").append(player.isInPrison()).append("\n");
        sb.append(prefix).append("TurnsInPrison=").append(player.getTurnsInPrison()).append("\n");
        sb.append(prefix).append("GetOutPrisonCards=").append(player.getGetOutPrisonCardCount()).append("\n");
        sb.append(prefix).append("ConsecutiveDoubles=").append(player.getConsecutiveDoubles()).append("\n");
        
        // Format properties list
        List<Property> properties = player.getLiquidAssets();
        sb.append(prefix).append("Properties=");
        if (!properties.isEmpty()) {
            StringJoiner joiner = new StringJoiner(",");
            for (Property prop : properties) {
                joiner.add(prop.getName());
            }
            sb.append(joiner.toString());
        }
        sb.append("\n");
        
        return sb.toString();
    }
    
    /**
     * Formats the [PROPERTIES] section
     */
    private static String formatPropertiesSection(ModelFacade facade) {
        StringBuilder sb = new StringBuilder();
        sb.append("[PROPERTIES]\n");
        
        Board board = facade.getBoard();
        List<Property> properties = extractPropertiesFromBoard(board);
        
        sb.append("PropertyCount=").append(properties.size()).append("\n");
        
        for (int i = 0; i < properties.size(); i++) {
            Property prop = properties.get(i);
            sb.append(formatPropertyData(i, prop, board));
        }
        
        return sb.toString();
    }
    
    /**
     * Formats data for a single property
     */
    private static String formatPropertyData(int index, Property property, Board board) {
        StringBuilder sb = new StringBuilder();
        String prefix = "Property_" + index + "_";
        
        sb.append(prefix).append("Name=").append(property.getName()).append("\n");
        
        int boardIndex = findSpaceIndex(board, property);
        sb.append(prefix).append("BoardIndex=").append(boardIndex).append("\n");
        
        sb.append(prefix).append("Owner=").append(property.isOwned() ? property.getOwner().getName() : "null").append("\n");
        
        // Houses and hotels (only for Place instances)
        int houses = 0;
        int hotels = 0;
        if (property instanceof Place) {
            Place place = (Place) property;
            houses = place.getNumOfHouses();
            hotels = place.getNumOfHotels();
        }
        sb.append(prefix).append("Houses=").append(houses).append("\n");
        sb.append(prefix).append("Hotels=").append(hotels).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Extracts all Property instances from the board
     */
    private static List<Property> extractPropertiesFromBoard(Board board) {
        List<Property> properties = new ArrayList<>();
        for (int i = 0; i < board.getBoardSize(); i++) {
            Space space = board.getSpace(i);
            if (space instanceof Property) {
                properties.add((Property) space);
            }
        }
        return properties;
    }
    
    /**
     * Finds the index of a space on the board
     */
    private static int findSpaceIndex(Board board, Space space) {
        for (int i = 0; i < board.getBoardSize(); i++) {
            if (board.getSpace(i) == space) {
                return i;
            }
        }
        return -1; // Not found
    }
    
    /**
     * Formats the [LOG] section with game history messages
     */
    private static String formatLogSection() {
        StringBuilder sb = new StringBuilder();
        sb.append("[LOG]\n");
        
        // Get log messages from GameState
        controller.GameState gameState = controller.GameState.getInstance();
        List<String> logMessages = gameState.getLogMessages();
        
        sb.append("MessageCount=").append(logMessages.size()).append("\n");
        
        // Save each log message (escape newlines and special characters)
        for (int i = 0; i < logMessages.size(); i++) {
            String message = logMessages.get(i);
            // Escape special characters: newline, equals sign
            message = message.replace("\n", "\\n").replace("=", "\\=");
            sb.append("Message_").append(i).append("=").append(message).append("\n");
        }
        
        return sb.toString();
    }
}


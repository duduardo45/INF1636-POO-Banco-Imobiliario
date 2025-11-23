package model.core.entities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * GameStateLoader - Deserializes game state from plain text format
 * 
 * This class is responsible for loading and reconstructing the complete game state
 * from a .txt file in UTF-8 encoding. The format must match the specification in
 * SAVE_FORMAT.txt.
 */
public class GameStateLoader {
    
    /**
     * Loads game state from a file and returns a configured ModelFacade
     * 
     * @param filePath Path to the save file
     * @return ModelFacade with loaded game state
     * @throws IOException If file reading fails
     * @throws IllegalArgumentException If file format is invalid
     */
    public static ModelFacade loadFromFile(String filePath) throws IOException {
        Map<String, String> gameStateData = new HashMap<>();
        Map<Integer, Map<String, String>> playersData = new HashMap<>();
        Map<Integer, Map<String, String>> propertiesData = new HashMap<>();
        Map<Integer, String> logData = new HashMap<>();
        
        // Read and parse file
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            
            String currentSection = null;
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Check for section headers
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    continue;
                }
                
                // Parse key=value pairs
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    
                    if (currentSection == null) {
                        throw new IllegalArgumentException("Data found outside of any section: " + line);
                    }
                    
                    switch (currentSection) {
                        case "GAME_STATE":
                            gameStateData.put(key, value);
                            break;
                        case "PLAYERS":
                            parsePlayerLine(key, value, playersData);
                            break;
                        case "PROPERTIES":
                            parsePropertyLine(key, value, propertiesData);
                            break;
                        case "LOG":
                            parseLogLine(key, value, logData);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown section: " + currentSection);
                    }
                }
            }
        }
        
        // Validate required sections
        if (gameStateData.isEmpty()) {
            throw new IllegalArgumentException("Missing [GAME_STATE] section");
        }
        if (playersData.isEmpty()) {
            throw new IllegalArgumentException("Missing [PLAYERS] section");
        }
        if (propertiesData.isEmpty()) {
            throw new IllegalArgumentException("Missing [PROPERTIES] section");
        }
        // LOG section is optional for backward compatibility
        
        // Reconstruct game state
        return reconstructGameState(gameStateData, playersData, propertiesData, logData);
    }
    
    /**
     * Parses a player data line and stores it
     */
    private static void parsePlayerLine(String key, String value, Map<Integer, Map<String, String>> playersData) {
        if (key.equals("PlayerCount")) {
            return; // We'll determine count from the data itself
        }
        
        // Extract player index from key like "Player0_Name"
        if (key.startsWith("Player") && key.contains("_")) {
            String[] parts = key.split("_", 2);
            int playerIndex = Integer.parseInt(parts[0].substring(6)); // "Player0" -> 0
            String fieldName = parts[1];
            
            playersData.putIfAbsent(playerIndex, new HashMap<>());
            playersData.get(playerIndex).put(fieldName, value);
        }
    }
    
    /**
     * Parses a property data line and stores it
     */
    private static void parsePropertyLine(String key, String value, Map<Integer, Map<String, String>> propertiesData) {
        if (key.equals("PropertyCount")) {
            return; // We'll determine count from the data itself
        }
        
        // Extract property index from key like "Property_0_Name"
        if (key.startsWith("Property_") && key.contains("_")) {
            String[] parts = key.split("_", 3);
            if (parts.length >= 3) {
                int propIndex = Integer.parseInt(parts[1]);
                String fieldName = parts[2];
                
                propertiesData.putIfAbsent(propIndex, new HashMap<>());
                propertiesData.get(propIndex).put(fieldName, value);
            }
        }
    }
    
    /**
     * Parses a log message line and stores it
     */
    private static void parseLogLine(String key, String value, Map<Integer, String> logData) {
        if (key.equals("MessageCount")) {
            return; // We'll determine count from the data itself
        }
        
        // Extract message index from key like "Message_0"
        if (key.startsWith("Message_")) {
            String indexStr = key.substring(8); // "Message_0" -> "0"
            int messageIndex = Integer.parseInt(indexStr);
            // Unescape special characters
            String unescapedValue = value.replace("\\n", "\n").replace("\\=", "=");
            logData.put(messageIndex, unescapedValue);
        }
    }
    
    /**
     * Reconstructs complete game state from parsed data
     */
    private static ModelFacade reconstructGameState(
            Map<String, String> gameStateData,
            Map<Integer, Map<String, String>> playersData,
            Map<Integer, Map<String, String>> propertiesData,
            Map<Integer, String> logData) {
        
        // Create standard board
        Board board = BoardInitializer.createStandardBoard();
        
        // Reconstruct players
        List<Player> players = reconstructPlayers(playersData, board);
        
        // Extract properties from board and apply ownership/buildings
        List<Property> boardProperties = new ArrayList<>();
        for (int i = 0; i < board.getBoardSize(); i++) {
            Space space = board.getSpace(i);
            if (space instanceof Property) {
                boardProperties.add((Property) space);
            }
        }
        
        // Create bank
        Bank bank = new Bank(200000, boardProperties);
        
        // Apply property ownership and buildings
        applyPropertyState(propertiesData, board, players, bank);
        
        // Parse game state values
        int currentPlayerIndex = Integer.parseInt(gameStateData.get("CurrentPlayerIndex"));
        boolean hasBuiltThisTurn = Boolean.parseBoolean(gameStateData.get("HasBuiltThisTurn"));
        boolean diceRolledThisTurn = Boolean.parseBoolean(gameStateData.get("DiceRolledThisTurn"));
        
        String justBoughtName = gameStateData.get("PropertyJustBought");
        Property propertyJustBought = null;
        if (justBoughtName != null && !justBoughtName.equals("null") && !justBoughtName.isEmpty()) {
            propertyJustBought = findPropertyByName(board, justBoughtName);
        }
        
        String[] diceValues = gameStateData.get("LastDiceRoll").split(",");
        int[] lastDiceRoll = new int[]{Integer.parseInt(diceValues[0]), Integer.parseInt(diceValues[1])};
        
        // Create and configure ModelFacade
        ModelFacade facade = new ModelFacade();
        facade.loadGameState(board, bank, players, currentPlayerIndex, hasBuiltThisTurn, 
                           diceRolledThisTurn, propertyJustBought, lastDiceRoll);
        
        // Restore log messages to GameState
        if (!logData.isEmpty()) {
            List<String> logMessages = new ArrayList<>();
            // Sort by index to maintain order
            List<Integer> indices = new ArrayList<>(logData.keySet());
            Collections.sort(indices);
            for (int index : indices) {
                logMessages.add(logData.get(index));
            }
            
            // Set messages in GameState
            controller.GameState gameState = controller.GameState.getInstance();
            gameState.setLogMessages(logMessages);
        }
        
        return facade;
    }
    
    /**
     * Reconstructs all players from parsed data
     */
    private static List<Player> reconstructPlayers(Map<Integer, Map<String, String>> playersData, Board board) {
        List<Player> players = new ArrayList<>();
        
        // Sort by index to maintain order
        List<Integer> indices = new ArrayList<>(playersData.keySet());
        Collections.sort(indices);
        
        for (int index : indices) {
            Map<String, String> playerData = playersData.get(index);
            
            String name = playerData.get("Name");
            String color = playerData.get("Color");
            int balance = Integer.parseInt(playerData.get("Balance"));
            int position = Integer.parseInt(playerData.get("Position"));
            boolean inPrison = Boolean.parseBoolean(playerData.get("InPrison"));
            int turnsInPrison = Integer.parseInt(playerData.get("TurnsInPrison"));
            int getOutPrisonCards = Integer.parseInt(playerData.get("GetOutPrisonCards"));
            int consecutiveDoubles = Integer.parseInt(playerData.get("ConsecutiveDoubles"));
            
            // Create car at the correct position
            Space startSpace = board.getSpace(position);
            Car car = new Car(color, startSpace);
            
            // Create player with initial balance (will be set correctly below)
            Player player = new Player(name, color, car, 0);
            player.setBalance(balance);
            player.setPrisonState(inPrison, turnsInPrison);
            player.setConsecutiveDoubles(consecutiveDoubles);
            player.addGetOutPrisonCards(getOutPrisonCards);
            
            players.add(player);
        }
        
        return players;
    }
    
    /**
     * Applies property ownership and building state from parsed data
     */
    private static void applyPropertyState(
            Map<Integer, Map<String, String>> propertiesData,
            Board board,
            List<Player> players,
            Bank bank) {
        
        for (Map<String, String> propData : propertiesData.values()) {
            String name = propData.get("Name");
            int boardIndex = Integer.parseInt(propData.get("BoardIndex"));
            String ownerName = propData.get("Owner");
            int houses = Integer.parseInt(propData.get("Houses"));
            int hotels = Integer.parseInt(propData.get("Hotels"));
            
            // Find property on board
            Space space = board.getSpace(boardIndex);
            if (!(space instanceof Property)) {
                throw new IllegalArgumentException("Board index " + boardIndex + " is not a property");
            }
            
            Property property = (Property) space;
            
            // Verify name matches
            if (!property.getName().equals(name)) {
                throw new IllegalArgumentException("Property name mismatch at index " + boardIndex + 
                                                 ": expected " + property.getName() + ", found " + name);
            }
            
            // Set owner
            if (ownerName != null && !ownerName.equals("null") && !ownerName.isEmpty()) {
                Player owner = findPlayerByName(players, ownerName);
                if (owner == null) {
                    throw new IllegalArgumentException("Property owner not found: " + ownerName);
                }
                
                owner.addPropertyWithoutPayment(property);
                bank.markPropertyAsOwned(property);
            }
            
            // Set buildings (only for Place instances)
            if (property instanceof Place) {
                Place place = (Place) property;
                place.setHouses(houses);
                place.setHotels(hotels);
            }
        }
    }
    
    /**
     * Finds a player by name
     */
    private static Player findPlayerByName(List<Player> players, String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * Finds a property by name on the board
     */
    private static Property findPropertyByName(Board board, String name) {
        for (int i = 0; i < board.getBoardSize(); i++) {
            Space space = board.getSpace(i);
            if (space instanceof Property) {
                Property prop = (Property) space;
                if (prop.getName().equals(name)) {
                    return prop;
                }
            }
        }
        return null;
    }
}


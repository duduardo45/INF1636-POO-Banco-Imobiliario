package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;


import java.util.List;
import java.util.ArrayList;

public class ReceiveFromOthersCardTest {
    
    private ReceiveFromOthersCard receiveFromOthersCard;
    private Player player1, player2, player3;
    private Car car1, car2, car3;
    private Space startSpace;
    private List<Player> allPlayers;
    
    @Before
    public void setUp() {
        startSpace = new Start("Start", null);
        car1 = new Car("Red", startSpace);
        car2 = new Car("Blue", startSpace);
        car3 = new Car("Green", startSpace);
        
        player1 = new Player("Player1", "Red", car1, 1000);
        player2 = new Player("Player2", "Blue", car2, 1000);
        player3 = new Player("Player3", "Green", car3, 1000);
        
        allPlayers = new ArrayList<>();
        allPlayers.add(player1);
        allPlayers.add(player2);
        allPlayers.add(player3);
        
        // Set up Turn singleton for the card to work
        Turn.setPlayerOrder(allPlayers);
        
        receiveFromOthersCard = new ReceiveFromOthersCard("It's your birthday! Everyone gives you a gift", 50);
    }
    
    @After
    public void tearDown() {
        // Clean up Turn singleton
        Turn.setPlayerOrder(new ArrayList<>());
    }
    
    @Test
    public void testConstructor() {
        assertEquals("It's your birthday! Everyone gives you a gift", receiveFromOthersCard.getStory());
        assertEquals(LuckType.LUCKY, receiveFromOthersCard.getType());
        assertEquals(50, receiveFromOthersCard.getValue());
    }
    
    @Test
    public void testUse() {
        int initialBalance1 = player1.getBalance();
        int initialBalance2 = player2.getBalance();
        int initialBalance3 = player3.getBalance();
        
        boolean result = receiveFromOthersCard.use(player1);
        
        assertTrue(result);
        assertEquals(initialBalance1 + 100, player1.getBalance()); // Receives from 2 other players
        assertEquals(initialBalance2 - 50, player2.getBalance());   // Pays to player1
        assertEquals(initialBalance3 - 50, player3.getBalance());   // Pays to player1
    }
    
    @Test
    public void testUseWithSinglePlayer() {
        List<Player> singlePlayerList = new ArrayList<>();
        singlePlayerList.add(player1);
        Turn.setPlayerOrder(singlePlayerList);
        
        int initialBalance = player1.getBalance();
        boolean result = receiveFromOthersCard.use(player1);
        
        assertTrue(result);
        assertEquals(initialBalance, player1.getBalance()); // No change, no other players
    }
    
    @Test
    public void testUseWithTwoPlayers() {
        List<Player> twoPlayerList = new ArrayList<>();
        twoPlayerList.add(player1);
        twoPlayerList.add(player2);
        Turn.setPlayerOrder(twoPlayerList);
        
        int initialBalance1 = player1.getBalance();
        int initialBalance2 = player2.getBalance();
        
        boolean result = receiveFromOthersCard.use(player1);
        
        assertTrue(result);
        assertEquals(initialBalance1 + 50, player1.getBalance()); // Receives from 1 other player
        assertEquals(initialBalance2 - 50, player2.getBalance());  // Pays to player1
    }
    
    @Test
    public void testOnDraw() {
        int initialBalance1 = player1.getBalance();
        int initialBalance2 = player2.getBalance();
        int initialBalance3 = player3.getBalance();
        
        boolean result = receiveFromOthersCard.onDraw(player1);
        
        assertTrue(result);
        assertEquals(initialBalance1 + 100, player1.getBalance());
        assertEquals(initialBalance2 - 50, player2.getBalance());
        assertEquals(initialBalance3 - 50, player3.getBalance());
    }
    
    @Test
    public void testUseWithZeroValue() {
        ReceiveFromOthersCard zeroCard = new ReceiveFromOthersCard("Nothing happens", 0);
        
        int initialBalance1 = player1.getBalance();
        int initialBalance2 = player2.getBalance();
        int initialBalance3 = player3.getBalance();
        
        boolean result = zeroCard.use(player1);
        
        assertTrue(result);
        assertEquals(initialBalance1, player1.getBalance()); // No change
        assertEquals(initialBalance2, player2.getBalance()); // No change
        assertEquals(initialBalance3, player3.getBalance()); // No change
    }
    
    @Test
    public void testUseCanMakeOtherPlayersBalanceNegative() {
        Player poorPlayer = new Player("PoorPlayer", "Yellow", car1, 25);
        allPlayers.set(1, poorPlayer); // Replace player2 with poor player
        Turn.setPlayerOrder(allPlayers);
        
        int initialBalance1 = player1.getBalance();
        int initialPoorBalance = poorPlayer.getBalance();
        
        boolean result = receiveFromOthersCard.use(player1);
        
        assertTrue(result);
        assertEquals(initialBalance1 + 100, player1.getBalance());
        assertEquals(initialPoorBalance - 50, poorPlayer.getBalance()); // Goes negative
    }
}
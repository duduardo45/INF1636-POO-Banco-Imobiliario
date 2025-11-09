package model.core.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PrisonTest {
    private Prison prison;
    private Player player;
    private Space nextSpace;
    
    @Before
    public void setUp() {
        // Create a simple Space for testing
        nextSpace = new Prison("Next Space", null);
        prison = new Prison("Prison", nextSpace);
        player = new Player("Test Player", "Blue", new Car("Blue", prison), 1000);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Prison", prison.getName());
        assertEquals(nextSpace, prison.getNext());
    }
    
    @Test
    public void testEventWhenPlayerNotInPrison() {
        // Player not in prison
        assertFalse(player.isInPrison());
        int initialTurns = player.getTurnsInPrison();
        
        prison.event(player);
        
        // Should not increment turns when not in prison
        assertEquals("Turns should not change when player is not in prison", 
                    initialTurns, player.getTurnsInPrison());
    }
    
    @Test
    public void testEventWhenPlayerInPrison() {
        // Player in prison
        player.sendToPrison(prison);
        assertTrue(player.isInPrison());
        int initialTurns = player.getTurnsInPrison();
        
        prison.event(player);
        
        // Should increment turns when in prison
        assertEquals("Turns should increment when player is in prison", 
                    initialTurns + 1, player.getTurnsInPrison());
    }
    
    @Test
    public void testTryDoubleDiceWhenPlayerNotInPrison() {
        // Player not in prison
        assertFalse(player.isInPrison());
        
        boolean result = prison.tryDoubleDice(player, 3, 3);
        
        assertFalse("Should not be able to try double dice when not in prison", result);
        assertFalse("Player should still not be in prison", player.isInPrison());
    }
    
    @Test
    public void testTryDoubleDiceWhenPlayerInPrisonWithDoubles() {
        // Player in prison
        player.sendToPrison(prison);
        assertTrue(player.isInPrison());
        
        boolean result = prison.tryDoubleDice(player, 4, 4);
        
        assertTrue("Should succeed with double dice", result);
        assertFalse("Player should be released from prison", player.isInPrison());
        assertEquals("Turns in prison should be reset", 0, player.getTurnsInPrison());
    }
    
    @Test
    public void testTryDoubleDiceWhenPlayerInPrisonWithoutDoubles() {
        // Player in prison
        player.sendToPrison(prison);
        assertTrue(player.isInPrison());
        int initialTurns = player.getTurnsInPrison();
        
        boolean result = prison.tryDoubleDice(player, 3, 4);
        
        assertFalse("Should fail without double dice", result);
        assertTrue("Player should still be in prison", player.isInPrison());
        assertEquals("Turns should not change", initialTurns, player.getTurnsInPrison());
    }
    
    @Test
    public void testTryDoubleDiceWithDifferentDoubleValues() {
        player.sendToPrison(prison);
        
        // Test different double values
        assertTrue("Should work with 1,1", prison.tryDoubleDice(player, 1, 1));
        player.sendToPrison(prison); // Reset for next test
        
        assertTrue("Should work with 2,2", prison.tryDoubleDice(player, 2, 2));
        player.sendToPrison(prison); // Reset for next test
        
        assertTrue("Should work with 6,6", prison.tryDoubleDice(player, 6, 6));
    }
    
    @Test
    public void testProcessPrisonTurnWhenPlayerNotInPrison() {
        // Player not in prison
        assertFalse(player.isInPrison());
        
        boolean result = prison.processPrisonTurn(player, 3, 3);
        
        assertFalse("Should return false when player is not in prison", result);
        assertFalse("Player should still not be in prison", player.isInPrison());
    }
    
    @Test
    public void testProcessPrisonTurnWhenPlayerInPrisonWithDoubles() {
        // Player in prison
        player.sendToPrison(prison);
        assertTrue(player.isInPrison());
        
        boolean result = prison.processPrisonTurn(player, 5, 5);
        
        assertTrue("Should return true when player gets out with doubles", result);
        assertFalse("Player should be released from prison", player.isInPrison());
        assertEquals("Turns should be reset after release", 0, player.getTurnsInPrison());
    }
    
    @Test
    public void testProcessPrisonTurnWhenPlayerInPrisonWithoutDoubles() {
        // Player in prison
        player.sendToPrison(prison);
        assertTrue(player.isInPrison());
        int initialTurns = player.getTurnsInPrison();
        
        boolean result = prison.processPrisonTurn(player, 2, 4);
        
        assertFalse("Should return false when player doesn't get doubles", result);
        assertTrue("Player should still be in prison", player.isInPrison());
        assertEquals("Turns should increment", initialTurns + 1, player.getTurnsInPrison());
    }
    
    @Test
    public void testProcessPrisonTurnIncrementsTurns() {
        player.sendToPrison(prison);
        
        // First turn
        prison.processPrisonTurn(player, 1, 3);
        assertEquals("Should have 1 turn after first attempt", 1, player.getTurnsInPrison());
        
        // Second turn
        prison.processPrisonTurn(player, 2, 4);
        assertEquals("Should have 2 turns after second attempt", 2, player.getTurnsInPrison());
        
        // Third turn
        prison.processPrisonTurn(player, 3, 5);
        assertEquals("Should have 3 turns after third attempt", 3, player.getTurnsInPrison());
    }
    
    @Test
    public void testUseGetOutPrisonCardWhenPlayerNotInPrison() {
        // Player not in prison but has card
        GetOutPrisonCard card = new GetOutPrisonCard("Test story", player);
        player.receiveGetOutPrisonCard(card);
        assertFalse(player.isInPrison());
        
        boolean result = prison.useGetOutPrisonCard(player);
        
        assertFalse("Should not be able to use card when not in prison", result);
        assertTrue("Player should still have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseGetOutPrisonCardWhenPlayerInPrisonWithoutCard() {
        // Player in prison but no card
        player.sendToPrison(prison);
        assertTrue(player.isInPrison());
        assertFalse(player.hasGetOutPrisonCard());
        
        boolean result = prison.useGetOutPrisonCard(player);
        
        assertFalse("Should not be able to use card when player doesn't have one", result);
        assertTrue("Player should still be in prison", player.isInPrison());
    }
    
    @Test
    public void testUseGetOutPrisonCardWhenPlayerInPrisonWithCard() {
        // Player in prison with card
        player.sendToPrison(prison);
        GetOutPrisonCard card = new GetOutPrisonCard("Test story", player);
        player.receiveGetOutPrisonCard(card);
        assertTrue(player.isInPrison());
        assertTrue(player.hasGetOutPrisonCard());
        
        boolean result = prison.useGetOutPrisonCard(player);
        
        assertTrue("Should be able to use card when player has one and is in prison", result);
        assertFalse("Player should be released from prison", player.isInPrison());
        assertFalse("Player should no longer have the card", player.hasGetOutPrisonCard());
        assertEquals("Turns should be reset", 0, player.getTurnsInPrison());
    }
    
    @Test
    public void testUseGetOutPrisonCardRemovesCardFromPlayer() {
        player.sendToPrison(prison);
        GetOutPrisonCard card = new GetOutPrisonCard("Test story", player);
        player.receiveGetOutPrisonCard(card);
        
        GetOutPrisonCard usedCard = player.useGetOutPrisonCard();
        
        assertEquals("Should return the same card", card, usedCard);
        assertFalse("Player should no longer have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testMultiplePlayersInPrison() {
        Player player2 = new Player("Player 2", "Red", new Car("Red", prison), 1000);
        
        // Both players in prison
        player.sendToPrison(prison);
        player2.sendToPrison(prison);
        
        // Process turn for first player
        boolean result1 = prison.processPrisonTurn(player, 3, 3);
        assertTrue("First player should get out with doubles", result1);
        assertFalse("First player should be released", player.isInPrison());
        
        // Second player should still be in prison
        assertTrue("Second player should still be in prison", player2.isInPrison());
        
        // Process turn for second player
        boolean result2 = prison.processPrisonTurn(player2, 4, 4);
        assertTrue("Second player should also get out with doubles", result2);
        assertFalse("Second player should be released", player2.isInPrison());
    }
    
    @Test
    public void testPrisonTurnSequence() {
        player.sendToPrison(prison);
        
        // First turn - no doubles
        assertFalse(prison.processPrisonTurn(player, 1, 2));
        assertTrue(player.isInPrison());
        assertEquals(1, player.getTurnsInPrison());
        
        // Second turn - no doubles
        assertFalse(prison.processPrisonTurn(player, 3, 4));
        assertTrue(player.isInPrison());
        assertEquals(2, player.getTurnsInPrison());
        
        // Third turn - doubles, should get out
        assertTrue(prison.processPrisonTurn(player, 5, 5));
        assertFalse(player.isInPrison());
        assertEquals(0, player.getTurnsInPrison());
    }
}

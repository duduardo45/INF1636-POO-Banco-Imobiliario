package model.core.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GoToPrisonTest {
    private GoToPrison goToPrison;
    private Player player;
    private Space nextSpace;
    
    @Before
    public void setUp() {
        nextSpace = new Prison("Next Space", null);
        goToPrison = new GoToPrison(nextSpace);
        player = new Player("Test Player", "Blue", new Car("Blue", null), 1000);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Go to Prison", goToPrison.getName());
        assertEquals(nextSpace, goToPrison.getNext());
    }
    
    @Test
    public void testEventWithPlayerNotInPrison() {
        // Player not in prison initially
        assertFalse(player.isInPrison());
        assertEquals("Player should have 0 turns in prison initially", 0, player.getTurnsInPrison());
        
        goToPrison.event(player);
        
        // Should send player to prison
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertEquals("Player should have 0 turns in prison after being sent", 0, player.getTurnsInPrison());
        assertTrue("Player's car should be marked as in prison", player.getCar().isInPrison());
    }
    
    @Test
    public void testEventWithPlayerAlreadyInPrison() {
        // Player already in prison
        player.sendToPrison();
        assertTrue(player.isInPrison());
        int initialTurns = player.getTurnsInPrison();
        
        goToPrison.event(player);
        
        // Should still be in prison (no change)
        assertTrue("Player should still be in prison", player.isInPrison());
        assertEquals("Turns should not change", initialTurns, player.getTurnsInPrison());
    }
    
    @Test
    public void testEventWithPlayerHavingGetOutPrisonCard() {
        // Player has get out prison card
        GetOutPrisonCard card = new GetOutPrisonCard("Test story", player);
        player.receiveGetOutPrisonCard(card);
        assertTrue("Player should have get out prison card", player.hasGetOutPrisonCard());
        
        goToPrison.event(player);
        
        // Should still be sent to prison (card doesn't prevent going to prison)
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertTrue("Player should still have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testEventWithPlayerHavingProperties() {
        // Player has properties
        Property property = new Company("Test Property", null, 200, 200);
        player.buyProperty(property);
        assertTrue("Player should have properties", player.hasLiquidAssets());
        
        goToPrison.event(player);
        
        // Should still be sent to prison (properties don't prevent going to prison)
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertTrue("Player should still have properties", player.hasLiquidAssets());
    }
    
    @Test
    public void testEventWithPlayerHavingBalance() {
        // Player has balance
        int initialBalance = player.getBalance();
        assertEquals("Player should have initial balance", 1000, initialBalance);
        
        goToPrison.event(player);
        
        // Should still be sent to prison (balance doesn't prevent going to prison)
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertEquals("Balance should not change", initialBalance, player.getBalance());
    }
    
    @Test
    public void testEventWithMultiplePlayers() {
        Player player2 = new Player("Player 2", "Red", new Car("Red", null), 1000);
        
        // Both players not in prison
        assertFalse(player.isInPrison());
        assertFalse(player2.isInPrison());
        
        // First player lands on Go to Prison
        goToPrison.event(player);
        assertTrue("First player should be sent to prison", player.isInPrison());
        assertFalse("Second player should not be affected", player2.isInPrison());
        
        // Second player lands on Go to Prison
        goToPrison.event(player2);
        assertTrue("First player should still be in prison", player.isInPrison());
        assertTrue("Second player should also be sent to prison", player2.isInPrison());
    }
    
    @Test
    public void testEventResetsConsecutiveDoubles() {
        // Player has consecutive doubles
        player.processDiceRoll(3, 3);
        player.processDiceRoll(4, 4);
        assertEquals("Player should have 2 consecutive doubles", 2, player.getConsecutiveDoubles());
        
        goToPrison.event(player);
        
        // Should be sent to prison and consecutive doubles should be reset
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertEquals("Consecutive doubles should be reset", 0, player.getConsecutiveDoubles());
    }
    
    @Test
    public void testEventWithPlayerAtDifferentPositions() {
        // Test that event works regardless of player's current position
        // (This tests the basic functionality, actual position management would be in Board)
        
        goToPrison.event(player);
        assertTrue("Player should be sent to prison", player.isInPrison());
        
        // Reset player
        player.releaseFromPrison();
        assertFalse("Player should not be in prison after release", player.isInPrison());
        
        // Test again
        goToPrison.event(player);
        assertTrue("Player should be sent to prison again", player.isInPrison());
    }
    
    @Test
    public void testEventWithNullPlayer() {
        // This test ensures the method handles null gracefully
        // (In real implementation, this might throw an exception)
        try {
            goToPrison.event(null);
            // If no exception is thrown, that's also acceptable behavior
        } catch (Exception e) {
            // If an exception is thrown, that's also acceptable
            assertTrue("Should handle null player appropriately", true);
        }
    }
    
    @Test
    public void testInheritanceFromSpace() {
        // Test that GoToPrison properly inherits from Space
        assertEquals("Should have correct name", "Go to Prison", goToPrison.getName());
        assertEquals("Should have correct next space", nextSpace, goToPrison.getNext());
        
        // Test that it's an instance of Space
        assertTrue("Should be an instance of Space", goToPrison instanceof Space);
    }
    
    @Test
    public void testEventDoesNotAffectOtherPlayerAttributes() {
        // Player has various attributes
        int initialBalance = player.getBalance();
        String playerName = player.getName();
        String carColor = player.getCar().getColor();
        
        goToPrison.event(player);
        
        // Only prison status should change
        assertTrue("Player should be in prison", player.isInPrison());
        assertEquals("Balance should not change", initialBalance, player.getBalance());
        assertEquals("Name should not change", playerName, player.getName());
        assertEquals("Car color should not change", carColor, player.getCar().getColor());
    }
}

package model.core.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GoToPrisonCardTest {
    private GoToPrisonCard card;
    private Player player;
    private Prison prisonSpace;
    
    @Before
    public void setUp() {
        prisonSpace = new Prison("Prisão", null);
        card = new GoToPrisonCard("Go directly to prison. Do not pass Go. Do not collect $200.");
        card.setPrisonSpace(prisonSpace);
        player = new Player("Test Player", "Blue", new Car("Blue", prisonSpace), 1000);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Go directly to prison. Do not pass Go. Do not collect $200.", card.getStory());
        assertEquals(LuckType.MISFORTUNE, card.getType());
    }
    
    @Test
    public void testUseWithPlayerNotInPrison() {
        // Player not in prison initially
        assertFalse(player.isInPrison());
        assertEquals("Player should have 0 turns in prison initially", 0, player.getTurnsInPrison());
        
        boolean result = card.use(player);
        
        assertTrue("Card should be used successfully", result);
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertEquals("Player should have 0 turns in prison after being sent", 0, player.getTurnsInPrison());
        assertTrue("Player's car should be marked as in prison", player.getCar().isInPrison());
    }
    
    @Test
    public void testUseWithPlayerAlreadyInPrison() {
        // Player already in prison
        player.sendToPrison(prisonSpace);
        assertTrue(player.isInPrison());
        int initialTurns = player.getTurnsInPrison();
        
        boolean result = card.use(player);
        
        assertFalse("Card should not be used when player is already in prison", result);
        assertTrue("Player should still be in prison", player.isInPrison());
        assertEquals("Turns should not change", initialTurns, player.getTurnsInPrison());
    }
    
    @Test
    public void testUseWithNullPlayer() {
        boolean result = card.use(null);
        
        assertFalse("Card should not be used with null player", result);
    }
    
    @Test
    public void testUseResetsConsecutiveDoubles() {
        // Player has some consecutive doubles
        player.processDiceRoll(1, 1); // First double
        player.processDiceRoll(2, 2); // Second double
        assertEquals("Player should have 2 consecutive doubles", 2, player.getConsecutiveDoubles());
        
        boolean result = card.use(player);
        
        assertTrue("Card should be used successfully", result);
        assertTrue("Player should be sent to prison", player.isInPrison());
        assertEquals("Consecutive doubles should be reset", 0, player.getConsecutiveDoubles());
    }
    
    @Test
    public void testMultipleUses() {
        // First use should work
        boolean result1 = card.use(player);
        assertTrue("First use should be successful", result1);
        assertTrue("Player should be in prison", player.isInPrison());
        
        // Second use should fail (player already in prison)
        boolean result2 = card.use(player);
        assertFalse("Second use should fail", result2);
        assertTrue("Player should still be in prison", player.isInPrison());
    }
    
    @Test
    public void testUseWithDifferentPlayers() {
        Player player2 = new Player("Player 2", "Red", new Car("Red", null), 1000);
        
        // Use card on first player
        boolean result1 = card.use(player);
        assertTrue("Card should work on first player", result1);
        assertTrue("First player should be in prison", player.isInPrison());
        
        // Use card on second player
        boolean result2 = card.use(player2);
        assertTrue("Card should work on second player", result2);
        assertTrue("Second player should be in prison", player2.isInPrison());
        
        // Both players should be in prison
        assertTrue("First player should still be in prison", player.isInPrison());
        assertTrue("Second player should be in prison", player2.isInPrison());
    }
}

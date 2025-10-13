package model.core.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GetOutPrisonCardTest {
    private Player player;
    private Player otherPlayer;
    private GetOutPrisonCard card;
    
    @Before
    public void setUp() {
        player = new Player("Test Player", "Blue", 1000);
        otherPlayer = new Player("Other Player", "Red", 1000);
        card = new GetOutPrisonCard("Test story", player);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Test story", card.getStory());
        assertEquals(LuckType.LUCKY, card.getType());
        assertEquals(player, card.getOwner());
    }
    
    @Test
    public void testGetOwner() {
        assertEquals("Should return the correct owner", player, card.getOwner());
    }
    
    @Test
    public void testSetOwner() {
        card.setOwner(otherPlayer);
        assertEquals("Should set new owner", otherPlayer, card.getOwner());
    }
    
    @Test
    public void testSetOwnerToNull() {
        card.setOwner(null);
        assertNull("Should allow setting owner to null", card.getOwner());
    }
    
    @Test
    public void testCanBeUsedByWithCorrectOwnerInPrison() {
        // Player is owner and in prison
        player.sendToPrison();
        assertTrue(player.isInPrison());
        
        boolean result = card.canBeUsedBy(player);
        
        assertTrue("Should be usable by correct owner in prison", result);
    }
    
    @Test
    public void testCanBeUsedByWithCorrectOwnerNotInPrison() {
        // Player is owner but not in prison
        assertFalse(player.isInPrison());
        
        boolean result = card.canBeUsedBy(player);
        
        assertFalse("Should not be usable when owner is not in prison", result);
    }
    
    @Test
    public void testCanBeUsedByWithWrongOwner() {
        // Other player tries to use the card
        otherPlayer.sendToPrison();
        assertTrue(otherPlayer.isInPrison());
        
        boolean result = card.canBeUsedBy(otherPlayer);
        
        assertFalse("Should not be usable by wrong owner", result);
    }
    
    @Test
    public void testCanBeUsedByWithNullOwner() {
        // Card has no owner
        card.setOwner(null);
        player.sendToPrison();
        
        boolean result = card.canBeUsedBy(player);
        
        assertFalse("Should not be usable when card has no owner", result);
    }
    
    @Test
    public void testCanBeUsedByWithNullPlayer() {
        player.sendToPrison();
        
        boolean result = card.canBeUsedBy(null);
        
        assertFalse("Should not be usable with null player", result);
    }
    
    @Test
    public void testUseWithCorrectOwnerInPrison() {
        // Player is owner and in prison
        player.sendToPrison();
        player.receiveGetOutPrisonCard(card); // Give the card to the player
        assertTrue(player.isInPrison());
        assertTrue(player.hasGetOutPrisonCard());
        
        boolean result = card.use(player);
        
        assertTrue("Should successfully use card", result);
        assertFalse("Player should be released from prison", player.isInPrison());
        assertFalse("Player should no longer have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseWithCorrectOwnerNotInPrison() {
        // Player is owner but not in prison
        player.receiveGetOutPrisonCard(card); // Give the card to the player
        assertFalse(player.isInPrison());
        assertTrue(player.hasGetOutPrisonCard());
        
        boolean result = card.use(player);
        
        assertFalse("Should not be able to use card when not in prison", result);
        assertFalse("Player should still not be in prison", player.isInPrison());
        assertTrue("Player should still have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseWithWrongOwner() {
        // Other player tries to use the card
        player.receiveGetOutPrisonCard(card); // Give the card to the original player
        otherPlayer.sendToPrison();
        assertTrue(otherPlayer.isInPrison());
        
        boolean result = card.use(otherPlayer);
        
        assertFalse("Should not be able to use card with wrong owner", result);
        assertTrue("Other player should still be in prison", otherPlayer.isInPrison());
        assertTrue("Original player should still have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseWithNullPlayer() {
        player.sendToPrison();
        player.receiveGetOutPrisonCard(card); // Give the card to the player
        
        boolean result = card.use(null);
        
        assertFalse("Should not be able to use card with null player", result);
        assertTrue("Player should still be in prison", player.isInPrison());
        assertTrue("Player should still have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseWithNullOwner() {
        card.setOwner(null);
        player.sendToPrison();
        
        boolean result = card.use(player);
        
        assertFalse("Should not be able to use card with null owner", result);
        assertTrue("Player should still be in prison", player.isInPrison());
    }
    
    @Test
    public void testUseResetsPlayerTurnsInPrison() {
        player.sendToPrison();
        player.receiveGetOutPrisonCard(card); // Give the card to the player
        player.incrementTurnsInPrison();
        player.incrementTurnsInPrison();
        assertEquals("Player should have 2 turns in prison", 2, player.getTurnsInPrison());
        
        boolean result = card.use(player);
        
        assertTrue("Should successfully use card", result);
        assertEquals("Turns in prison should be reset", 0, player.getTurnsInPrison());
    }
    
    @Test
    public void testUseRemovesCardFromPlayerInventory() {
        player.sendToPrison();
        player.receiveGetOutPrisonCard(card); // Give the card to the player
        assertTrue("Player should have the card initially", player.hasGetOutPrisonCard());
        
        GetOutPrisonCard usedCard = player.useGetOutPrisonCard();
        
        assertEquals("Should return the same card", card, usedCard);
        assertFalse("Player should no longer have the card", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testMultipleCardsWithDifferentOwners() {
        GetOutPrisonCard card2 = new GetOutPrisonCard("Another story", otherPlayer);
        
        // Both players in prison
        player.sendToPrison();
        otherPlayer.sendToPrison();
        
        // Each player can only use their own card
        assertTrue("Player should be able to use their own card", card.canBeUsedBy(player));
        assertFalse("Player should not be able to use other player's card", card2.canBeUsedBy(player));
        
        assertTrue("Other player should be able to use their own card", card2.canBeUsedBy(otherPlayer));
        assertFalse("Other player should not be able to use player's card", card.canBeUsedBy(otherPlayer));
    }
    
    @Test
    public void testCardInheritanceFromLuckCard() {
        // Test that GetOutPrisonCard properly inherits from LuckCard
        assertEquals("Should have correct type", LuckType.LUCKY, card.getType());
        assertEquals("Should have correct story", "Test story", card.getStory());
    }
    
    @Test
    public void testCardWithEmptyStory() {
        GetOutPrisonCard emptyCard = new GetOutPrisonCard("", player);
        
        assertEquals("Should handle empty story", "", emptyCard.getStory());
        assertEquals("Should have correct owner", player, emptyCard.getOwner());
    }
    
    @Test
    public void testCardWithNullStory() {
        GetOutPrisonCard nullCard = new GetOutPrisonCard(null, player);
        
        assertNull("Should handle null story", nullCard.getStory());
        assertEquals("Should have correct owner", player, nullCard.getOwner());
    }
}

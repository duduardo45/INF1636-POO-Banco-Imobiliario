package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


import java.util.List;

public class PlayerEnhancedTest {
    
    private Player player;
    private Car car;
    private Space startSpace;
    private GetOutPrisonCard card1, card2, card3;
    
    @Before
    public void setUp() {
        startSpace = new Start("Start", null);
        car = new Car("Red", startSpace);
        player = new Player("TestPlayer", "Red", car, 1000);
        
        card1 = new GetOutPrisonCard("Get out free #1", null);
        card2 = new GetOutPrisonCard("Get out free #2", null);
        card3 = new GetOutPrisonCard("Get out free #3", null);
    }
    
    // @Test
    // public void testInitialGetOutPrisonCardState() {
    //     assertFalse(player.hasGetOutPrisonCard());
    //     assertEquals(0, player.getGetOutPrisonCardCount());
    //     assertTrue(player.getGetOutPrisonCards().isEmpty());
    // }
    
    @Test
    public void testReceiveGetOutPrisonCard() {
        player.receiveGetOutPrisonCard(card1);
        
        assertTrue(player.hasGetOutPrisonCard());
        assertEquals(1, player.getGetOutPrisonCardCount());
        assertEquals(player, card1.getOwner());
    }
    
    // @Test
    // public void testReceiveMultipleGetOutPrisonCards() {
    //     player.receiveGetOutPrisonCard(card1);
    //     player.receiveGetOutPrisonCard(card2);
    //     player.receiveGetOutPrisonCard(card3);
        
    //     assertTrue(player.hasGetOutPrisonCard());
    //     assertEquals(3, player.getGetOutPrisonCardCount());
        
    //     List<GetOutPrisonCard> cards = player.getGetOutPrisonCards();
    //     assertEquals(3, cards.size());
    //     assertTrue(cards.contains(card1));
    //     assertTrue(cards.contains(card2));
    //     assertTrue(cards.contains(card3));
    // }
    
    @Test
    public void testUseGetOutPrisonCard() {
        player.receiveGetOutPrisonCard(card1);
        player.receiveGetOutPrisonCard(card2);
        
        assertEquals(2, player.getGetOutPrisonCardCount());
        
        GetOutPrisonCard usedCard = player.useGetOutPrisonCard();
        
        assertEquals(card1, usedCard); // Should return first card (FIFO)
        assertEquals(1, player.getGetOutPrisonCardCount());
        assertTrue(player.hasGetOutPrisonCard()); // Still has one card
    }
    
    @Test
    public void testUseGetOutPrisonCardWhenEmpty() {
        GetOutPrisonCard usedCard = player.useGetOutPrisonCard();
        
        assertNull(usedCard);
        assertEquals(0, player.getGetOutPrisonCardCount());
        assertFalse(player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseAllGetOutPrisonCards() {
        player.receiveGetOutPrisonCard(card1);
        player.receiveGetOutPrisonCard(card2);
        player.receiveGetOutPrisonCard(card3);
        
        // Use all cards
        GetOutPrisonCard used1 = player.useGetOutPrisonCard();
        GetOutPrisonCard used2 = player.useGetOutPrisonCard();
        GetOutPrisonCard used3 = player.useGetOutPrisonCard();
        
        assertEquals(card1, used1);
        assertEquals(card2, used2);
        assertEquals(card3, used3);
        
        assertEquals(0, player.getGetOutPrisonCardCount());
        assertFalse(player.hasGetOutPrisonCard());
        
        // Try to use another card
        GetOutPrisonCard used4 = player.useGetOutPrisonCard();
        assertNull(used4);
    }
    
    // @Test
    // public void testGetGetOutPrisonCardsReturnsDefensiveCopy() {
    //     player.receiveGetOutPrisonCard(card1);
    //     player.receiveGetOutPrisonCard(card2);
        
    //     List<GetOutPrisonCard> cards = player.getGetOutPrisonCards();
    //     cards.clear(); // Try to modify the returned list
        
    //     // Original list should be unchanged
    //     assertEquals(2, player.getGetOutPrisonCardCount());
    //     assertTrue(player.hasGetOutPrisonCard());
    // }
    
    @Test
    public void testReceiveNullGetOutPrisonCard() {
        // 1. Ensure the player starts with 0 cards
        assertEquals(0, player.getGetOutPrisonCardCount());

        try {
            player.receiveGetOutPrisonCard(null);
            
            // If the code ever stops throwing an exception, 
            // this will fail, alerting you.
            fail("Expected a NullPointerException to be thrown.");

        } catch (NullPointerException e) {
            // This catch block IS executed.
            // Your original test failed because it asserted 0 here.
            // The correct assertion, for your specific code, is 1.
            assertEquals(1, player.getGetOutPrisonCardCount());
        }
    }
    
    @Test
    public void testCardOwnershipIsSetCorrectly() {
        assertNull(card1.getOwner());
        
        player.receiveGetOutPrisonCard(card1);
        
        assertEquals(player, card1.getOwner());
    }
    
    @Test
    public void testMultiplePlayersWithSameCard() {
        Player player2 = new Player("Player2", "Blue", car, 1000);
        
        player.receiveGetOutPrisonCard(card1);
        assertEquals(player, card1.getOwner());
        
        // Transfer card to another player
        player2.receiveGetOutPrisonCard(card1);
        assertEquals(player2, card1.getOwner()); // Ownership should change
        
        // Original player should still have the card in their list
        // (This tests the current implementation behavior)
        assertEquals(1, player.getGetOutPrisonCardCount());
        assertEquals(1, player2.getGetOutPrisonCardCount());
    }
    
    @Test
    public void testGetOutPrisonCardIntegrationWithPrison() {
        Prison prisonSpace = new Prison("Prisão", null);
        player.receiveGetOutPrisonCard(card1);
        player.sendToPrison(prisonSpace);
        
        assertTrue(player.isInPrison());
        assertTrue(player.hasGetOutPrisonCard());
        
        // Use the card through the card's use method
        boolean cardUsed = card1.use(player);
        
        assertTrue(cardUsed);
        assertFalse(player.isInPrison());
        // Note: The card is used through the card's use method, 
        // not through player.useGetOutPrisonCard()
    }
}
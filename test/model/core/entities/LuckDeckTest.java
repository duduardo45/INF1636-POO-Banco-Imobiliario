package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;



public class LuckDeckTest {
    
    private LuckDeck luckDeck;
    
    @Before
    public void setUp() {
        luckDeck = new LuckDeck();
    }
    
    @Test
    public void testConstructorInitializesDeck() {
        assertFalse(luckDeck.isEmpty());
        assertEquals(5, luckDeck.size()); // Should have 5 default cards
    }
    
    @Test
    public void testDrawCard() {
        int initialSize = luckDeck.size();
        LuckCard card = luckDeck.drawCard();
        
        assertNotNull(card);
        assertEquals(initialSize - 1, luckDeck.size()); // Size should decrease by 1
    }
    
    @Test
    public void testDrawCardFromEmptyDeck() {
        LuckDeck emptyDeck = new LuckDeck();
        // Remove all cards by drawing them
        while (!emptyDeck.isEmpty()) {
            emptyDeck.drawCard();
        }
        
        // Now deck should be empty
        assertTrue(emptyDeck.isEmpty());
        LuckCard card = emptyDeck.drawCard();
        assertNull(card);
    }
    
    @Test
    public void testDrawAllCards() {
        int deckSize = luckDeck.size();
        List<LuckCard> drawnCards = new ArrayList<>();
        
        // Draw all cards
        for (int i = 0; i < deckSize; i++) {
            LuckCard card = luckDeck.drawCard();
            assertNotNull(card);
            drawnCards.add(card);
            assertEquals(deckSize - i - 1, luckDeck.size());
        }
        
        // Deck should now be empty
        assertTrue(luckDeck.isEmpty());
        assertNull(luckDeck.drawCard());
        
        // Verify we drew the expected number of cards
        assertEquals(deckSize, drawnCards.size());
    }
    
    @Test
    public void testAddCard() {
        int initialSize = luckDeck.size();
        ReceiveCard newCard = new ReceiveCard("Test card", 100);
        
        luckDeck.addCard(newCard);
        
        assertEquals(initialSize + 1, luckDeck.size());
    }
    
    @Test
    public void testAddNullCard() {
        int initialSize = luckDeck.size();
        
        luckDeck.addCard(null);
        
        assertEquals(initialSize + 1, luckDeck.size()); // Null is added to the list
    }
    
    @Test
    public void testShuffle() {
        // Record initial size
        int initialSize = luckDeck.size();
        
        // Shuffle the deck
        luckDeck.shuffle();
        
        // Size should remain the same after shuffle
        assertEquals(initialSize, luckDeck.size());
        
        // Should still be able to draw cards
        LuckCard card = luckDeck.drawCard();
        assertNotNull(card);
        assertEquals(initialSize - 1, luckDeck.size());
    }
    
    @Test
    public void testSize() {
        assertEquals(5, luckDeck.size());
        
        luckDeck.addCard(new PayCard(50, "Test"));
        assertEquals(6, luckDeck.size());
        
        luckDeck.addCard(new ReceiveCard("Test2", 75));
        assertEquals(7, luckDeck.size());
    }
    
    @Test
    public void testIsEmpty() {
        assertFalse(luckDeck.isEmpty()); // Should not be empty initially
        
        // Draw all cards
        while (!luckDeck.isEmpty()) {
            luckDeck.drawCard();
        }
        
        assertTrue(luckDeck.isEmpty()); // Should be empty after drawing all cards
    }
    
    @Test
    public void testDrawCardRemovesFromDeck() {
        // Create a deck with known cards
        LuckDeck testDeck = new LuckDeck();
        int initialSize = testDeck.size();
        
        // Draw first card
        LuckCard firstCard = testDeck.drawCard();
        assertNotNull(firstCard);
        assertEquals(initialSize - 1, testDeck.size());
        
        // Draw second card - should be different from first
        LuckCard secondCard = testDeck.drawCard();
        assertNotNull(secondCard);
        assertEquals(initialSize - 2, testDeck.size());
        
        // Cards should be different instances (unless deck has duplicates)
        // We can't guarantee they're different since the deck might have similar cards
        // But we can verify the deck size keeps decreasing
    }
    
    @Test
    public void testEmptyDeckBehavior() {
        // Create a small deck and empty it
        LuckDeck smallDeck = new LuckDeck();
        List<LuckCard> drawnCards = new ArrayList<>();
        
        // Draw all cards
        while (!smallDeck.isEmpty()) {
            drawnCards.add(smallDeck.drawCard());
        }
        
        // Verify deck is empty
        assertTrue(smallDeck.isEmpty());
        assertEquals(0, smallDeck.size());
        
        // Try to draw from empty deck
        assertNull(smallDeck.drawCard());
        
        // Add a card back
        smallDeck.addCard(new ReceiveCard("New card", 100));
        assertFalse(smallDeck.isEmpty());
        assertEquals(1, smallDeck.size());
        
        // Should be able to draw the added card
        LuckCard newCard = smallDeck.drawCard();
        assertNotNull(newCard);
        assertTrue(smallDeck.isEmpty());
    }
    
    @Test
    public void testInitializedCardsTypes() {
        // Test that the deck contains different types of cards
        boolean hasReceiveCard = false;
        boolean hasPayCard = false;
        boolean hasReceiveFromOthersCard = false;
        boolean hasGetOutPrisonCard = false;
        boolean hasGoToPrisonCard = false;
        
        int deckSize = luckDeck.size();
        for (int i = 0; i < deckSize; i++) {
            LuckCard card = luckDeck.drawCard();
            if (card instanceof ReceiveCard) hasReceiveCard = true;
            if (card instanceof PayCard) hasPayCard = true;
            if (card instanceof ReceiveFromOthersCard) hasReceiveFromOthersCard = true;
            if (card instanceof GetOutPrisonCard) hasGetOutPrisonCard = true;
            if (card instanceof GoToPrisonCard) hasGoToPrisonCard = true;
        }
        
        assertTrue("Deck should contain ReceiveCard", hasReceiveCard);
        assertTrue("Deck should contain PayCard", hasPayCard);
        assertTrue("Deck should contain ReceiveFromOthersCard", hasReceiveFromOthersCard);
        assertTrue("Deck should contain GetOutPrisonCard", hasGetOutPrisonCard);
        assertTrue("Deck should contain GoToPrisonCard", hasGoToPrisonCard);
    }
    
    @Test
    public void testMultipleDrawsAndAdds() {
        // Test complex scenario with multiple operations
        int initialSize = luckDeck.size();
        
        // Draw some cards (size decreases)
        luckDeck.drawCard();
        luckDeck.drawCard();
        assertEquals(initialSize - 2, luckDeck.size());
        
        // Add some cards (size increases)
        luckDeck.addCard(new ReceiveCard("Bonus", 150));
        luckDeck.addCard(new PayCard(75, "Tax"));
        assertEquals(initialSize, luckDeck.size()); // Back to original size
        
        // Shuffle
        luckDeck.shuffle();
        assertEquals(initialSize, luckDeck.size()); // Size unchanged by shuffle
        
        // Draw more cards (size decreases)
        assertNotNull(luckDeck.drawCard());
        assertEquals(initialSize - 1, luckDeck.size());
        assertNotNull(luckDeck.drawCard());
        assertEquals(initialSize - 2, luckDeck.size());
        assertNotNull(luckDeck.drawCard());
        assertEquals(initialSize - 3, luckDeck.size());
    }
    
    @Test
    public void testReset() {
        int initialSize = luckDeck.size();
        
        // Draw some cards
        luckDeck.drawCard();
        luckDeck.drawCard();
        luckDeck.drawCard();
        
        assertTrue(luckDeck.size() < initialSize);
        
        // Reset the deck
        luckDeck.reset();
        
        // Should be back to initial size
        assertEquals(initialSize, luckDeck.size());
        assertFalse(luckDeck.isEmpty());
        
        // Should be able to draw cards again
        assertNotNull(luckDeck.drawCard());
    }
    
    @Test
    public void testResetEmptyDeck() {
        // Empty the deck completely
        while (!luckDeck.isEmpty()) {
            luckDeck.drawCard();
        }
        
        assertTrue(luckDeck.isEmpty());
        assertEquals(0, luckDeck.size());
        
        // Reset should restore the deck
        luckDeck.reset();
        
        assertEquals(5, luckDeck.size()); // Should have 5 default cards
        assertFalse(luckDeck.isEmpty());
        assertNotNull(luckDeck.drawCard());
    }}

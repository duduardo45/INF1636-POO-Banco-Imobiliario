package model.core.entities;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class LuckDeck {
    private List<LuckCard> deck;
    private int currentIndex;
    
    public LuckDeck() {
        this.deck = new ArrayList<>();
        this.currentIndex = 0;
        initializeDeck();
    }
    
    /**
     * Initializes the deck with standard luck cards.
     */
    private void initializeDeck() {
        // Add some example cards - in a real game these would be loaded from configuration
        deck.add(new ReceiveCard("Você ganhou na loteria!", 200));
        deck.add(new PayCard(100, "Pague multa de trânsito"));
        deck.add(new ReceiveFromOthersCard("É seu aniversário! Todos te dão presente", 50));
        deck.add(new GetOutPrisonCard("Saia da prisão", null));
        deck.add(new GoToPrisonCard("Vá para a prisão"));
        
        shuffle();
    }
    
    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(this.deck);
        this.currentIndex = 0;
    }
    
    /**
     * Draws the next card from the deck.
     * 
     * @return The next luck card.
     */
    public LuckCard drawCard() {
        if (deck.isEmpty()) {
            return null;
        }
        
        LuckCard card = deck.get(currentIndex);
        currentIndex = (currentIndex + 1) % deck.size();
        
        return card;
    }
    
    /**
     * Adds a card to the deck.
     * 
     * @param card The card to add.
     */
    public void addCard(LuckCard card) {
        this.deck.add(card);
    }
    
    /**
     * Returns the number of cards in the deck.
     * 
     * @return The deck size.
     */
    public int size() {
        return this.deck.size();
    }
    
    /**
     * Checks if the deck is empty.
     * 
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.deck.isEmpty();
    }
}

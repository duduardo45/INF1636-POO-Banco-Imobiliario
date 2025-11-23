package model.core.entities;

import java.util.List;

class LuckSpace extends Space {
    private LuckDeck deck;
    private LuckCard lastDrawnCard;
    
    LuckSpace(String name, Space next, LuckDeck deck) {
        super(name, next);
        this.deck = deck;
        this.lastDrawnCard = null;
    }
    
    /**
     * Sets the list of all players for cards that need it.
     * 
     * @param allPlayers List of all players in the game.
     */
    public void setAllPlayers(List<Player> allPlayers) {
        if (deck != null) {
            deck.setAllPlayers(allPlayers);
        }
    }
    
    @Override
    public String event(Player player) {
        // Draw a card from the deck
        LuckCard card = deck.drawCard();
        
        // If deck is empty, reset it and draw again
        if (card == null) {
            deck.reset();
            card = deck.drawCard();
        }
        
        // If still null (shouldn't happen), return empty message
        if (card == null) {
            return "Sem cartas disponíveis";
        }
        
        // Store the drawn card
        this.lastDrawnCard = card;
        
        // Execute the card's onDraw effect
        card.onDraw(player);
        
        // Return the card's story as the message
        return card.getStory();
    }
    
    public LuckCard getCurrentCard() {
        return lastDrawnCard;
    }
}

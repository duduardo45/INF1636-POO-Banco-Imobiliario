package model.core.entities;

class LuckSpace extends Space {
    private LuckDeck deck;
    
    LuckSpace(String name, Space next, LuckDeck deck) {
        super(name, next);
        this.deck = deck;
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
        
        // Execute the card's onDraw effect
        card.onDraw(player);
        
        // Return the card's story as the message
        return card.getStory();
    }
}

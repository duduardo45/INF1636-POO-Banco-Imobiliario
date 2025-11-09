package model.core.entities;

class ReceiveCard extends AutomaticCard {
    private final int value;
    
    public ReceiveCard(String story, int value) {
    	super(LuckType.LUCKY, story);
    	this.value = value;
    }
    
    /**
     * Returns the amount to be received.
     * 
     * @return The value to receive.
     */
    public int getValue() {
        return this.value;
    }
    
    /**
     * Executes the card effect: player receives money from the bank.
     * 
     * @param player The player who drew the card.
     */
    @Override
    public boolean use(Player player) {
        player.credit(this.value);
        return true;
    }
}

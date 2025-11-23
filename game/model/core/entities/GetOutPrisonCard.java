package model.core.entities;

class GetOutPrisonCard extends ManualCard {
    private Player owner;
    
    public GetOutPrisonCard(String imageId, String story, Player owner) {
    	super(imageId, LuckType.LUCKY, story);
    	
    	this.owner = owner;
    }
    
    /**
     * Returns the player who owns this card.
     * 
     * @return The player owner of the card.
     */
    public Player getOwner() {
        return this.owner;
    }
    
    /**
     * Sets the new owner of the card.
     * 
     * @param newOwner The new player owner.
     */
    public void setOwner(Player newOwner) {
        this.owner = newOwner;
    }
    
    /**
     * Checks if the card can be used by the specified player.
     * 
     * @param player The player trying to use the card.
     * @return true if the player is the owner and is in prison, false otherwise.
     */
    public boolean canBeUsedBy(Player player) {
        return this.owner != null && this.owner.equals(player) && player.isInPrison();
    }
    
    /**
     * Uses the card to release the player from prison.
     * 
     * @param player The player using the card.
     * @return true if the card was used successfully, false otherwise.
     */
    @Override
    public boolean use(Player player) {
        if (canBeUsedBy(player)) {
            player.releaseFromPrison();
            // Removes the card from the player
            player.useGetOutPrisonCard();
            return true;
        }
        return false;
    }
}

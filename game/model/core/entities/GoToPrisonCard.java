package model.core.entities;

class GoToPrisonCard extends AutomaticCard {
    private Prison prisonSpace;
    
    public GoToPrisonCard(String story) {
        super(LuckType.MISFORTUNE, story);
    }
    
    /**
     * Sets the prison space reference.
     * 
     * @param prisonSpace The prison space to send players to.
     */
    public void setPrisonSpace(Prison prisonSpace) {
        this.prisonSpace = prisonSpace;
    }
    
    /**
     * Uses the card to send the player to prison.
     * 
     * @param player The player using the card.
     * @return true if the card was used successfully, false otherwise.
     */
    @Override
    public boolean use(Player player) {
        if (player != null && !player.isInPrison() && prisonSpace != null) {
            player.sendToPrison(prisonSpace);
            return true;
        }
        return false;
    }
}

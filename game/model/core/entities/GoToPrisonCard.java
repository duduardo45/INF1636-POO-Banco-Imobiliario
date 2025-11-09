package model.core.entities;

class GoToPrisonCard extends AutomaticCard {
    
    public GoToPrisonCard(String story) {
        super(LuckType.MISFORTUNE, story);
    }
    
    /**
     * Uses the card to send the player to prison.
     * 
     * @param player The player using the card.
     * @return true if the card was used successfully, false otherwise.
     */
    @Override
    public boolean use(Player player) {
        if (player != null && !player.isInPrison()) {
            player.sendToPrison();
            return true;
        }
        return false;
    }
}

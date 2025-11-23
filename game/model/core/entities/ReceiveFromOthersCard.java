package model.core.entities;

import java.util.List;

class ReceiveFromOthersCard extends AutomaticCard {
    private final int value;
    private List<Player> allPlayers;
    
    public ReceiveFromOthersCard(String imageId, int value, String story) {
    	super(imageId, LuckType.LUCKY, story);
    	this.value = value;
    	this.allPlayers = null;
    }
    
    /**
     * Returns the amount each player should pay.
     * 
     * @return The value per player.
     */
    public int getValue() {
        return this.value;
    }
    
    /**
     * Sets the list of all players in the game.
     * 
     * @param allPlayers List of all players.
     */
    public void setAllPlayers(List<Player> allPlayers) {
        this.allPlayers = allPlayers;
    }
    
    /**
     * Executes the card effect: all other players pay the card holder.
     * 
     * @param cardHolder The player who drew the card.
     */
    @Override
    public boolean use(Player cardHolder) { 
        if (allPlayers == null || allPlayers.isEmpty()) {
            return false;
        }
        
        for (Player player : allPlayers) {
            if (!player.equals(cardHolder)) {
                player.pay(cardHolder, this.value);
            }
        }
        return true;
    }
}

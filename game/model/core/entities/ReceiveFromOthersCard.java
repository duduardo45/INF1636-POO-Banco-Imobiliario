package model.core.entities;

import java.util.List;

class ReceiveFromOthersCard extends AutomaticCard {
    private final int value;
    
    public ReceiveFromOthersCard(String imageId, int value, String story) {
    	super(imageId, LuckType.LUCKY, story);
    	this.value = value;
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
     * Executes the card effect: all other players pay the card holder.
     * 
     * @param cardHolder The player who drew the card.
     * @param allPlayers List of all players in the game.
     */
    @Override
    public boolean use(Player cardHolder) { 
        // TODO: Should get the list of players from somewhere else
        List<Player> allPlayers = Turn.getPlayers();
        for (Player player : allPlayers) {
            if (!player.equals(cardHolder)) {
                player.pay(cardHolder, this.value);
            }
        }
        return true;
    }
}

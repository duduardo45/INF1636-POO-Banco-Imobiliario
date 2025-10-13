package model.core.entities;

class Prison extends Space {
    
    public Prison(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public void event(Player player) {
        // If the player is not in prison, do nothing
        if (!player.isInPrison()) {
            return;
        }
        
        // Increments the counter of turns in prison
        player.incrementTurnsInPrison();
    }
    
    /**
     * Processes the attempt to get out of prison using double dice.
     * 
     * @param player The player trying to get out.
     * @param dice1 The value of the first die.
     * @param dice2 The value of the second die.
     * @return true if the player succeeded in getting out (double dice), false otherwise.
     */
    public boolean tryDoubleDice(Player player, int dice1, int dice2) {
        if (!player.canTryDoubleDice()) {
            return false;
        }
        
        if (dice1 == dice2) {
            player.releaseFromPrison();
            return true;
        }
        
        return false;
    }
    
    /**
     * Processes a player's turn in prison, including escape attempts.
     * 
     * @param player The player in prison.
     * @param dice1 The value of the first die.
     * @param dice2 The value of the second die.
     * @return true if the player succeeded in getting out of prison, false otherwise.
     */
    public boolean processPrisonTurn(Player player, int dice1, int dice2) {
        if (!player.isInPrison()) {
            return false;
        }
        
        // Increments the counter of turns in prison
        player.incrementTurnsInPrison();
        
        // Tries to get out with double dice
        if (tryDoubleDice(player, dice1, dice2)) {
            return true; // Succeeded in getting out
        }
        
        return false; // Still in prison
    }
    
    
    /**
     * Processes the use of a "Get Out of Prison" card.
     * 
     * @param player The player trying to use the card.
     * @return true if the card was used successfully, false otherwise.
     */
    public boolean useGetOutPrisonCard(Player player) {
        if (player.hasGetOutPrisonCard() && player.isInPrison()) {
            player.useGetOutPrisonCard();
            player.releaseFromPrison();
            return true;
        }
        return false;
    }
    
}

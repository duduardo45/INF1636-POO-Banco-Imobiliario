package model.core.entities;

class PayCard extends AutomaticCard {
    private final int value;
    
    public PayCard(int value, String story) {
        super(LuckType.MISFORTUNE, story);
        this.value = value;
    }
    
    /**
     * Returns the amount to be paid.
     * 
     * @return The value to pay.
     */
    public int getValue() {
        return this.value;
    }
    
    /**
     * Executes the card effect: player pays money to the bank.
     * 
     * @param player The player who drew the card.
     */
    @Override
    public boolean use(Player player) {
        player.debit(this.value);
        return true;
    }
}

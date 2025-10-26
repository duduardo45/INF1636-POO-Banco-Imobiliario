package model.core.entities;

class Start extends Space {
    private final int passBonus;
    
    public Start(String name, Space next) {
        super(name, next);
        this.passBonus = 200;
    }
    
    public Start(String name, Space next, int passBonus) {
        super(name, next);
        this.passBonus = passBonus;
    }
    
    @Override
    public void event(Player player) {
        // Player receives bonus for landing on or passing START
        player.credit(passBonus);
    }
    
    /**
     * Returns the bonus amount for passing START.
     * 
     * @return The pass bonus amount.
     */
    public int getPassBonus() {
        return this.passBonus;
    }
}

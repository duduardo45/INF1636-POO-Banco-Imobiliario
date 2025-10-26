package model.core.entities;

class Profit extends Space {
    private final int profitAmount;
    
    Profit(String name, Space next, int profitAmount) {
        super(name, next);
        this.profitAmount = profitAmount;
    }
    
    @Override
    public void event(Player player) {
        player.credit(profitAmount);
    }
}

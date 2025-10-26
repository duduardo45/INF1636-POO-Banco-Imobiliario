package model.core.entities;

class Tax extends Space {
    private final int taxAmount;
    
    Tax(String name, Space next, int taxAmount) {
        super(name, next);
        this.taxAmount = taxAmount;
    }
    
    @Override
    public void event(Player player) {
        player.debit(taxAmount);
    }
}

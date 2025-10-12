package game.model.core.spaces;

public abstract class Property extends Space {
    protected final int cost;
    protected Player owner;
    protected int currentRent;

    public Property(String name, int cost) { super(name); }

    public int getPrice() {}
    public Player getOwner() {}
    public void setOwner(Player owner) {}
    public boolean isOwned() {}
    
    /** Retorna o valor do aluguel atualmente aplicável para esta propriedade. */
    public int getCurrentRent() {
        return this.currentRent;
    }

    @Override
    public void event(Player player) {}
}
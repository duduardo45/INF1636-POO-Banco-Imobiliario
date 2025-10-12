package game.model.core.spaces;

import game.model.core.entities.Money;
import game.model.core.entities.Player;
import game.model.core.entities.Turn;

class Property extends Space {
    private Player owner;
    private String name;
    private int cost;

    abstract public void event();

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player newOwner) {
        this.owner = newOwner;
    }

}

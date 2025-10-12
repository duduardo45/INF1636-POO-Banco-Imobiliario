package model.core.entities.spaces;

import model.core.entities.Money;
import model.core.entities.Player;
import model.core.entities.Turn;

public abstract class Property extends Space {
    private Player owner;
    private int cost;

    abstract public void event();
}

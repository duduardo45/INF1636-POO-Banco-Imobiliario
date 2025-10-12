package game.model.core.spaces;

import game.model.core.entities.Money;
import game.model.core.entities.Player;
import game.model.core.entities.Turn;

public class Property extends Space {
    private Player owner;
    private int cost;

    abstract public void event();
}

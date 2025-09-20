package game.spaces;

import game.entities.Space;
import game.entities.Player;
import game.entities.Turn;
import game.entities.Money;

public class Property extends Space {
    private Player owner;
    private Money tax;

    public void event() {
        if (true) { // owner exists
            Turn.getPlayer().payTo(owner, tax);
        }
    }
}

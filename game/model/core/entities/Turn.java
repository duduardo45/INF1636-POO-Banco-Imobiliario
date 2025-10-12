package game.model.core.entities;

import java.util.LinkedList;

public class Turn {
    // singleton that holds the active player
    private static Player current_player;
    private static LinkedList<Player> player_order; // BACALHAU needs fix cause this doesn't work

    public static Player getPlayer() {
        return current_player;
    }
    public static void advance_turn() {
        current_player = player_order.iterator().next();
    }
}

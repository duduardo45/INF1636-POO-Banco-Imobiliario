package model.core.entities;

import java.util.List;

class Turn {
    // singleton that holds the active player
    private static Player current_player;
    private static List<Player> player_order; // BACALHAU needs fix cause this doesn't work

    public static Player getPlayer() {
        return current_player;
    }
    public static void advance_turn() {
        current_player = player_order.iterator().next();
    }
}

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
    public static void setPlayerOrder(List<Player> player_order) {
        Turn.player_order = player_order;
    }
    public static void setFirstPlayer(Player player) {
        current_player = player;
    }
    public static void removePlayer(Player player) {
        player_order.remove(player);
    }
    public static List<Player> getPlayers()  {
        return player_order;
    }
}

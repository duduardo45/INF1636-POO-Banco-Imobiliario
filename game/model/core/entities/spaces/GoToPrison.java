package model.core.entities.spaces;

import model.core.entities.Player;
import model.core.entities.PrisonReason;

public class GoToPrison extends Space {
    
    public GoToPrison() {
        super("Go to Prison");
    }
    
    /**
     * Executa a ação do espaço: envia o jogador para a prisão.
     * @param player O jogador que caiu no espaço
     */
    public void executeAction(Player player) {
        player.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
    }
    
    @Override
    public void event() {
        // A lógica de envio para a prisão será tratada pelo GameModel
        // que chamará enterPrison(playerId, PrisonReason.LANDING_ON_GO_TO_PRISON)
    }
}

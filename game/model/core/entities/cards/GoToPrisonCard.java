package model.core.entities.cards;

import model.core.entities.Player;
import model.core.entities.PrisonReason;

/**
 * Carta que envia o jogador diretamente para a prisão.
 */
public class GoToPrisonCard extends LuckCard {
    
    public GoToPrisonCard(String story) {
        super(LuckType.MISFORTUNE, story);
    }
    
    /**
     * Executa a ação da carta: envia o jogador para a prisão.
     * @param player O jogador que recebe o efeito da carta
     */
    public void executeAction(Player player) {
        player.enterPrison(PrisonReason.CARD_GO_TO_PRISON);
    }
    
    /**
     * Aplica o efeito da carta: envia o jogador para a prisão.
     * Este método será chamado pelo GameModel quando a carta for usada.
     */
    public void applyEffect() {
        // A lógica de envio para a prisão será tratada pelo GameModel
        // que chamará enterPrison(playerId, PrisonReason.CARD_GO_TO_PRISON)
    }
}

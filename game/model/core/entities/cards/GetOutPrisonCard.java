package model.core.entities.cards;

import model.core.entities.Player;

/**
 * Carta que permite ao jogador sair da prisão sem pagar fiança.
 */
public class GetOutPrisonCard extends LuckCard {
    private Player owner;
    
    public GetOutPrisonCard(String story, Player owner) {
    	super(LuckType.LUCKY, story);
    	this.owner = owner;
    }
    
    /**
     * Executa a ação da carta: permite ao jogador sair da prisão.
     * @param player O jogador que usa a carta
     */
    public void executeAction(Player player) {
        if (player != null && player.getCar().isInPrison() && player.hasGetOutOfPrisonCard()) {
            // Remove a carta do jogador
            player.setHasGetOutOfPrisonCard(false);
            // Sai da prisão
            player.leavePrison();
        }
    }
    
    /**
     * Verifica se a carta pode ser usada pelo jogador.
     * @param player O jogador que quer usar a carta
     * @return true se pode usar, false caso contrário
     */
    public boolean canUse(Player player) {
        return player != null && player.getCar().isInPrison() && player.hasGetOutOfPrisonCard();
    }
    
    /**
     * Aplica o efeito da carta: permite ao jogador sair da prisão.
     * @return true se a carta foi usada com sucesso, false caso contrário
     */
    public boolean applyEffect() {
        if (owner != null && owner.isInPrison() && owner.hasGetOutOfPrisonCard()) {
            // Remove a carta do jogador
            owner.setHasGetOutOfPrisonCard(false);
            // Sai da prisão
            owner.leavePrison();
            return true;
        }
        return false;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}

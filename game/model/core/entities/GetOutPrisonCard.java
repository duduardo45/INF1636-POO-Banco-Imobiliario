package model.core.entities;

class GetOutPrisonCard extends LuckCard {
    private Player owner;
    
    public GetOutPrisonCard(String story, Player owner) {
    	super(LuckType.LUCKY, story);
    	
    	this.owner = owner;
    }
    
    /**
     * Retorna o jogador que possui esta carta.
     * 
     * @return O jogador proprietário da carta.
     */
    public Player getOwner() {
        return this.owner;
    }
    
    /**
     * Define o novo proprietário da carta.
     * 
     * @param newOwner O novo jogador proprietário.
     */
    public void setOwner(Player newOwner) {
        this.owner = newOwner;
    }
    
    /**
     * Verifica se a carta pode ser usada pelo jogador especificado.
     * 
     * @param player O jogador tentando usar a carta.
     * @return true se o jogador é o proprietário e está na prisão, false caso contrário.
     */
    public boolean canBeUsedBy(Player player) {
        return this.owner != null && this.owner.equals(player) && player.isInPrison();
    }
    
    /**
     * Usa a carta para libertar o jogador da prisão.
     * 
     * @param player O jogador usando a carta.
     * @return true se a carta foi usada com sucesso, false caso contrário.
     */
    public boolean use(Player player) {
        if (canBeUsedBy(player)) {
            player.releaseFromPrison();
            // Remove a carta do jogador
            player.useGetOutPrisonCard();
            return true;
        }
        return false;
    }
}

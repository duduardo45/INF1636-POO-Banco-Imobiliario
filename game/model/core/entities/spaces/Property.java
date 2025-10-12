package game.model.core.entities.spaces;


private class Property {
    protected final int cost;
    protected Player owner;
    protected int currentRent;

    public Property(String name, int cost) { 
        super(name);
        this.cost = cost;
        this.owner = null; // Começa sem dono
    }

    public int getCost() { return this.cost; }
    public Player getOwner() { return this.owner; }
    public boolean isOwned() { return this.owner != null; }
    
    /**
     * Define ou altera o dono da propriedade.
     * @param owner O novo jogador dono, ou null para remover o dono.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    
    /**
     * Retorna o valor do aluguel atualmente aplicável para esta propriedade.
     * As subclasses (Place, Company) são responsáveis por calcular e atualizar
     * o atributo `currentRent`. Este método apenas o retorna.
     * @return O valor do aluguel corrente.
     */
    public int getCurrentRent() {
        return this.currentRent;
    }
    
    /**
     * Define o valor do aluguel atual da propriedade. Usado para testes.
     * @param rent O novo valor do aluguel.
     */
    public void setCurrentRent(int rent) {
        this.currentRent = rent;
    }

    /**
     * Lógica executada quando um jogador para na propriedade. Se a propriedade
     * tiver um dono (que não seja o próprio jogador e não esteja preso), o aluguel é cobrado.
     * @param player O jogador que parou na casa.
     */
    @Override
    public void event(Player player) {
        // Verifica se a propriedade tem um dono, se o dono não é o jogador atual,
        // e se o dono não está na prisão (regra comum).
        if (isOwned() && getOwner() != player && !getOwner().isInJail()) {
            int rentToPay = getCurrentRent();
            
            // O jogador paga o aluguel ao proprietário.
            player.pay(getOwner(), rentToPay);
        }
        // Nota: A lógica de "oferecer compra" se a propriedade não tem dono
        // ficaria a cargo de uma classe de controle de jogo (GameController),
        // que chamaria player.buyProperty() se o jogador aceitar.
    }
}
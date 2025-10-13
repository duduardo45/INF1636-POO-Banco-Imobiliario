package model.core.entities;

abstract class Property extends Space {
    protected final int cost;
    protected Player owner;
    protected int currentRent;

    public Property(String name, Space next, int cost) { 
        super(name, next);
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
     * Lógica executada quando um jogador para na propriedade.
     * Este método é chamado automaticamente pelo sistema de jogo.
     * A lógica específica de cobrança de aluguel deve ser tratada pelo GameController.
     */
    @Override
    public void event(Player player) {
        // A implementação específica será feita pelo GameController
        // que terá acesso ao jogador atual e poderá chamar handleRentPayment()
    	handleRentPayment(player);
    }
    
    /**
     * Lógica para cobrança de aluguel quando um jogador para na propriedade.
     * Se a propriedade tiver um dono (que não seja o próprio jogador e não esteja preso), 
     * o aluguel é cobrado.
     * @param player O jogador que parou na casa.
     */
    public void handleRentPayment(Player player) {
        // Verifica se a propriedade tem um dono, se o dono não é o jogador atual,
        // e se o dono não está na prisão (regra comum).
        if (isOwned() && getOwner() != player) {
            int rentToPay = getCurrentRent();
            
            // O jogador paga o aluguel ao proprietário.
            player.pay(getOwner(), rentToPay);
        }
        // Nota: A lógica de "oferecer compra" se a propriedade não tem dono
        // ficaria a cargo de uma classe de controle de jogo (GameController),
        // que chamaria player.buyProperty() se o jogador aceitar.
    }
}
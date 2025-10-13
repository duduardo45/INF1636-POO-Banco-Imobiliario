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
     * Verifica se há aluguel devido quando um jogador para na propriedade.
     * Regras corretas:
     * - Propriedade deve ter dono
     * - Dono não pode ser o próprio jogador
     * - Propriedade deve ter pelo menos 1 casa (regra desta iteração)
     * 
     * @param player O jogador que parou na casa.
     * @return true se há aluguel devido, false caso contrário.
     */
    public boolean isRentDue(Player player) {
        // Verifica se a propriedade tem um dono
        if (!isOwned()) {
            return false;
        }
        
        // Verifica se o dono não é o próprio jogador
        if (getOwner() == player) {
            return false;
        }
        
        // Regra desta iteração: só cobra se tiver pelo menos 1 casa
        return hasAtLeastOneHouse();
    }
    
    /**
     * Verifica se a propriedade tem pelo menos uma casa.
     * Implementação padrão retorna false (sem casas).
     * Subclasses (Place) devem sobrescrever este método.
     * 
     * @return true se tem pelo menos 1 casa, false caso contrário.
     */
    protected boolean hasAtLeastOneHouse() {
        return false; // Propriedades genéricas não têm casas
    }
    
    
    /**
     * Calcula o valor do aluguel para esta propriedade.
     * Implementação padrão retorna 0.
     * Subclasses devem sobrescrever este método.
     * 
     * @return O valor do aluguel calculado.
     */
    public int calculateRent() {
        return 0; // Propriedades genéricas não têm aluguel
    }
    
    /**
     * Processa o pagamento de aluguel.
     * Se há aluguel devido, debita do pagador e credita ao dono.
     * 
     * @param player O jogador que deve pagar o aluguel.
     * @return O valor pago (0 se não há aluguel devido).
     */
    public int payRent(Player player) {
        if (!isRentDue(player)) {
            return 0;
        }
        
        int rentAmount = calculateRent();
        
        // Transfere o dinheiro do pagador para o dono
        player.pay(getOwner(), rentAmount);
        
        return rentAmount;
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
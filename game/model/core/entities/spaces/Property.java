package model.core.entities.spaces;

import model.core.entities.Player;

/**
 * Classe abstrata que representa uma propriedade no jogo Banco Imobiliário.
 * Propriedades podem ser compradas e geram aluguel quando outros jogadores param nelas.
 */
public abstract class Property extends Space {
    protected final int cost;
    protected Player owner;
    protected Integer ownerId; // ID do dono para facilitar consultas
    protected int currentRent;

    public Property(String name, int cost) { 
        super(name);
        this.cost = cost;
        this.owner = null; // Começa sem dono
        this.ownerId = null;
    }

    public int getCost() { return this.cost; }
    public Player getOwner() { return this.owner; }
    public Integer getOwnerId() { return this.ownerId; }
    public boolean isOwned() { return this.owner != null && this.ownerId != null; }
    
    /**
     * Define ou altera o dono da propriedade.
     * @param owner O novo jogador dono, ou null para remover o dono.
     * @param ownerId ID do dono para facilitar consultas
     */
    public void setOwner(Player owner, Integer ownerId) {
        this.owner = owner;
        this.ownerId = ownerId;
    }
    
    /**
     * Remove o dono da propriedade (torna-a disponível para compra).
     */
    public void removeOwner() {
        this.owner = null;
        this.ownerId = null;
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
     * Calcula o valor do aluguel para esta propriedade.
     * Implementação específica nas subclasses (Company, Place).
     * @return O valor do aluguel calculado
     */
    public abstract int calculateRent();
    
    /**
     * Verifica se deve cobrar aluguel para esta propriedade.
     * Regra: só cobra se tem dono e (para Place) tem pelo menos 1 casa.
     * @return true se deve cobrar aluguel
     */
    public abstract boolean shouldChargeRent();

    /**
     * Lógica executada quando um jogador para na propriedade.
     * Este método é chamado automaticamente pelo sistema de jogo.
     * A lógica específica de cobrança de aluguel deve ser tratada pelo GameController.
     */
    @Override
    public void event() {
        // A implementação específica será feita pelo GameController
        // que terá acesso ao jogador atual e poderá chamar handleRentPayment()
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
        if (isOwned() && getOwner() != player && !getOwner().getCar().isInPrison()) {
            int rentToPay = getCurrentRent();
            
            // O jogador paga o aluguel ao proprietário.
            player.pay(getOwner(), rentToPay);
        }
        // Nota: A lógica de "oferecer compra" se a propriedade não tem dono
        // ficaria a cargo de uma classe de controle de jogo (GameController),
        // que chamaria player.buyProperty() se o jogador aceitar.
    }
}
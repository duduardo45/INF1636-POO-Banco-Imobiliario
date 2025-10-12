package model.core.entities;

import model.core.entities.spaces.Property;
import java.util.List;
import java.util.ArrayList;

class Bank {
    /** O dinheiro total que o banco possui. */
    private int treasury;
    /** Lista de propriedades que ainda não foram compradas por nenhum jogador. */
    private final List<Property> unownedProperties;
    
    /**
     * Retorna o saldo atual do tesouro do banco.
     * @return O valor atual do tesouro.
     */
    public int getTreasuryBalance() {
        return this.treasury;
    }

    public Bank(int initialTreasury, List<Property> allProperties) {
        // A implementação do construtor iria aqui...
        this.treasury = initialTreasury;
        this.unownedProperties = new ArrayList<>(allProperties);
    }

    /**
     * Adiciona uma quantia de dinheiro ao tesouro do banco.
     * 
     * @param amount A quantia a ser creditada. Deve ser positiva.
     */
    public void credit(int amount) {
        if (amount > 0) {
            this.treasury += amount;
        }
    }

    /**
     * Remove uma quantia de dinheiro do tesouro do banco.
     * 
     * @param amount A quantia a ser debitada.
     */
    public void debit(int amount) {
    }

    /**
     * Verifica se uma propriedade específica pertence ao banco (não tem dono).
     * 
     * @param property A propriedade a ser verificada.
     * @return true se a propriedade estiver na lista de não possuídas, false caso
     *         contrário.
     */
    public boolean isPropertyUnowned(Property property) {
        return this.unownedProperties.contains(property);
    }

    /**
     * Remove uma propriedade da lista de não possuídas (quando um jogador compra).
     * 
     * @param property A propriedade que foi comprada.
     */
    public void markPropertyAsOwned(Property property) {
        this.unownedProperties.remove(property);
    }

    /**
     * Adiciona uma propriedade de volta à lista de não possuídas (ex: falência para
     * o banco).
     * 
     * @param property A propriedade a ser retornada ao banco.
     */
    public void returnPropertyToBank(Property property) {
        this.unownedProperties.add(property);
    }
}
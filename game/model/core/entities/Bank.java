package game.model.core.entities;

public class Bank {
    /** O dinheiro total que o banco possui. */
    private int treasury;
    /** Lista de propriedades que ainda não foram compradas por nenhum jogador. */
    private final List<Property> unownedProperties;

    public Bank(int initialTreasury, List<Property> allProperties) {}

    public void credit(int amount) {}
    public void debit(int amount) {}
    
    /** Verifica se uma propriedade específica pertence ao banco (não tem dono). */
    public boolean isPropertyUnowned(Property property) {}
    
    /** Remove uma propriedade da lista de não possuídas (quando um jogador compra). */
    public void markPropertyAsOwned(Property property) {}
    
    /** Adiciona uma propriedade de volta à lista de não possuídas (ex: falência para o banco). */
    public void returnPropertyToBank(Property property) {}
}
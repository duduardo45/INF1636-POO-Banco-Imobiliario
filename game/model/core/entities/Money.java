package model.core.entities;

/**
 * Representa um valor monetário no jogo Banco Imobiliário.
 * Usa valores inteiros para simplicidade (ex: 1500 = R$ 1.500,00).
 */
public class Money {
    private int amount;
    
    public Money(int amount) {
        this.amount = amount;
    }
    
    public Money() {
        this(0);
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public void add(int amount) {
        this.amount += amount;
    }
    
    public void subtract(int amount) {
        this.amount -= amount;
    }
    
    public boolean hasEnough(int amount) {
        return this.amount >= amount;
    }
    
    @Override
    public String toString() {
        return "R$ " + amount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return amount == money.amount;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(amount);
    }
}
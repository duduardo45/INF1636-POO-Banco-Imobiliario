package model.core.entities.spaces;

import model.core.entities.Dice;

/**
 * Classe abstrata que representa uma empresa (companhia) no jogo.
 * O aluguel é calculado baseado na soma dos dados multiplicada por um fator.
 */
public abstract class Company extends Property {
    private final int baseRent;
    private final int multiplier; // Fator multiplicador para o aluguel
    private Dice dice; // Referência aos dados para calcular aluguel
    
    public Company(String name, int cost, int baseRent, int multiplier) {
        super(name, cost);
        this.baseRent = baseRent;
        this.multiplier = multiplier;
    }
    
    public Company(String name, int cost, int baseRent) {
        this(name, cost, baseRent, 4); // Fator padrão de 4x
    }
    
    public int getBaseRent() {
        return baseRent;
    }
    
    public int getMultiplier() {
        return multiplier;
    }
    
    public void setDice(Dice dice) {
        this.dice = dice;
    }
    
    @Override
    public int calculateRent() {
        if (dice == null) {
            return baseRent; // Se não tem dados, retorna aluguel base
        }
        
        // Aluguel = fator * soma dos dados
        int diceSum = dice.getLastDiceSum();
        int calculatedRent = multiplier * diceSum;
        
        // Atualiza o aluguel atual
        this.currentRent = calculatedRent;
        
        return calculatedRent;
    }
    
    @Override
    public boolean shouldChargeRent() {
        // Company sempre cobra aluguel se tem dono
        return isOwned();
    }
}

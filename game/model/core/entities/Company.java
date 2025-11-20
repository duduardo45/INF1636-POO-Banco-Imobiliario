package model.core.entities;

class Company extends Property {
    private final int base_rent;
    private int tempDiceRoll; // Variável temporária para armazenar os dados
    
    public Company(String name, Space next, int cost, int base_rent) {
    	super(name, next, cost);
    	this.base_rent = base_rent;
        
    	this.currentRent = base_rent; // Initialize current rent

    }
    
    /**
     * Define o valor dos dados para ser usado no cálculo do aluguel.
     * Chamado pelo Facade antes do evento.
     */
    public void setDiceRollForRent(int diceRoll) {
        this.tempDiceRoll = diceRoll;
    }

    /**
     * Checks if the property has at least one house.
     * Companies always have "house" (always charge fixed rate).
     * 
     * @return true (companies always charge).
     */
    @Override
    protected boolean hasAtLeastOneHouse() {
        return true; // Companies always charge fixed rate
    }
    
    /**
     * Returns the base rent of the company.
     * 
     * @return The base rent value.
     */
    public int getBaseRent() {
        return this.base_rent;
    }
    

    /**
     * Calculates the rent value for this company based on dice roll.
     * For companies, rent is typically base_rent * dice_value.
     * 
     * @param diceValue The sum of dice values rolled.
     * @return The calculated rent value.
     */
    public int calculateRent() {
        return this.base_rent * this.tempDiceRoll;
    }

    @Override
    public String event(Player player) {
        if (isOwned() && !getOwner().equals(player)) {
            int rent = calculateRent();
            player.pay(getOwner(), rent);
            return "Pagou aluguel de $" + rent + " para " + getOwner().getName();
        }
        return super.event(player);
    }
}

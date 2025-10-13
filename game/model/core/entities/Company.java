package model.core.entities;

class Company extends Property {
    private final int base_rent;
    
    public Company(String name, Space next, int cost, int base_rent) {
    	super(name, next, cost);
    	this.base_rent = base_rent;
    	this.currentRent = base_rent; // Inicializa o aluguel atual
    }
    
    /**
     * Verifica se a propriedade tem pelo menos uma casa.
     * Companhias sempre têm "casa" (sempre cobram taxa fixa).
     * 
     * @return true (companhias sempre cobram).
     */
    @Override
    protected boolean hasAtLeastOneHouse() {
        return true; // Companhias sempre cobram taxa fixa
    }
    
    /**
     * Retorna o aluguel base da companhia.
     * 
     * @return O valor do aluguel base.
     */
    public int getBaseRent() {
        return this.base_rent;
    }
    
    /**
     * Calcula o valor do aluguel para esta companhia.
     * Companhias cobram taxa fixa (base_rent).
     * 
     * @return O valor do aluguel calculado.
     */
    @Override
    public int calculateRent() {
        return this.base_rent;
    }
}

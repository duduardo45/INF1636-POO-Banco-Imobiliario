package model.core.entities;

abstract class Property extends Space {
    protected final int cost;
    protected Player owner;
    protected int currentRent;

    public Property(String name, Space next, int cost) { 
        super(name, next);
        this.cost = cost;
        this.owner = null; // Starts without owner
    }

    public int getCost() { return this.cost; }
    public Player getOwner() { return this.owner; }
    public boolean isOwned() { return this.owner != null; }
    
    /**
     * Sets or changes the property owner.
     * @param owner The new player owner, or null to remove the owner.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    
    /**
     * Returns the currently applicable rent value for this property.
     * Subclasses (Place, Company) are responsible for calculating and updating
     * the `currentRent` attribute. This method only returns it.
     * @return The current rent value.
     */
    public int getCurrentRent() {
        return this.currentRent;
    }  
     
    /**
     * Sets the current rent value of the property. Used for testing.
     * @param rent The new rent value.
     */
    public void setCurrentRent(int rent) {
        this.currentRent = rent;
    }

    /**
     * Logic executed when a player lands on the property.
     * This method is called automatically by the game system.
     * Specific rent collection logic should be handled by GameController.
     */
    @Override
    public void event(Player player) {
        // Specific implementation will be done by GameController
        // which will have access to the current player and can call handleRentPayment()
    	handleRentPayment(player);
    }
    
    /**
     * Checks if rent is due when a player lands on the property.
     * Correct rules:
     * - Property must have an owner
     * - Owner cannot be the player themselves
     * - Property must have at least 1 house (rule of this iteration)
     * 
     * @param player The player who landed on the space.
     * @return true if rent is due, false otherwise.
     */
    public boolean isRentDue(Player player) {
        // Checks if the property has an owner
        if (!isOwned()) {
            return false;
        }
        
        // Checks if the owner is not the player themselves
        if (getOwner() == player) {
            return false;
        }
        
        // Rule of this iteration: only charges if it has at least 1 house
        return hasAtLeastOneHouse();
    }
    
    /**
     * Checks if the property has at least one house.
     * Default implementation returns false (no houses).
     * Subclasses (Place) should override this method.
     * 
     * @return true if it has at least 1 house, false otherwise.
     */
    protected boolean hasAtLeastOneHouse() {
        return false; // Generic properties do not have houses
    }
    
    
    /**
     * Calculates the rent value for this property.
     * Default implementation returns 0.
     * Subclasses should override this method.
     * 
     * @return The calculated rent value.
     */
    public int calculateRent() {
        return 0; // Generic properties do not have rent
    }
    
    /**
     * Processes rent payment.
     * If rent is due, debits from payer and credits to owner.
     * 
     * @param player The player who must pay the rent.
     * @return The amount paid (0 if no rent is due).
     */
    public int payRent(Player player) {
        if (!isRentDue(player)) {
            return 0;
        }
        
        int rentAmount = calculateRent();
        
        // Transfers money from payer to owner
        player.pay(getOwner(), rentAmount);
        
        return rentAmount;
    }
    
    /**
     * Logic for rent collection when a player lands on the property.
     * If the property has an owner (who is not the player themselves and not in prison), 
     * rent is collected.
     * @param player The player who landed on the space.
     */
    public void handleRentPayment(Player player) {
        // Checks if the property has an owner, if the owner is not the current player,
        // and if the owner is not in prison (common rule).
        if (isOwned() && getOwner() != player) {
            int rentToPay = getCurrentRent();
            
            // The player pays rent to the owner.
            player.pay(getOwner(), rentToPay);
        }
        // Note: The logic for "offering purchase" if the property has no owner
        // would be handled by a game control class (GameController),
        // which would call player.buyProperty() if the player accepts.
    }
}
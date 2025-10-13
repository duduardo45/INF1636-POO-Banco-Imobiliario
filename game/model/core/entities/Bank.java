package model.core.entities;

import java.util.List;
import java.util.ArrayList;

class Bank {
    /** The total money the bank has. */
    private int treasury;
    /** List of properties that have not yet been purchased by any player. */
    private final List<Property> unownedProperties;
    
    /**
     * Returns the current balance of the bank's treasury.
     * @return The current treasury value.
     */
    public int getTreasuryBalance() {
        return this.treasury;
    }

    public Bank(int initialTreasury, List<Property> allProperties) {
        // Constructor implementation would go here...
        this.treasury = initialTreasury;
        this.unownedProperties = new ArrayList<>(allProperties);
    }

    /**
     * Adds an amount of money to the bank's treasury.
     * 
     * @param amount The amount to be credited. Must be positive.
     */
    public void credit(int amount) {
        if (amount > 0) {
            this.treasury += amount;
        }
    }

    /**
     * Removes an amount of money from the bank's treasury.
     * 
     * @param amount The amount to be debited.
     */
    public void debit(int amount) {
    }

    /**
     * Checks if a specific property belongs to the bank (has no owner).
     * 
     * @param property The property to be checked.
     * @return true if the property is in the unowned list, false otherwise.
     */
    public boolean isPropertyUnowned(Property property) {
        return this.unownedProperties.contains(property);
    }

    /**
     * Removes a property from the unowned list (when a player buys it).
     * 
     * @param property The property that was purchased.
     */
    public void markPropertyAsOwned(Property property) {
        this.unownedProperties.remove(property);
    }

    /**
     * Adds a property back to the unowned list (e.g.: bankruptcy to
     * the bank).
     * 
     * @param property The property to be returned to the bank.
     */
    public void returnPropertyToBank(Property property) {
        this.unownedProperties.add(property);
    }
}
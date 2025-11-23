package model.core.entities;

import java.util.List;
import java.util.ArrayList;

class Player {
    private final String name;
    private int balance;
    private final Car car;
    private final List<Property> ownedProperties;
    private boolean inPrison;
    private int turnsInPrison;
    private List<GetOutPrisonCard> getOutPrisonCards;
    private int consecutiveDoubles;

    public Player(String name, String carColor, Car ownCar, int initialBalance) {
        this.name = name;
        this.balance = initialBalance;
        this.car = ownCar;
        this.ownedProperties = new ArrayList<>();
        this.inPrison = false;
        this.turnsInPrison = 0;
        this.getOutPrisonCards = new ArrayList<>();
        this.consecutiveDoubles = 0;
    }

    public String getName() {
        return this.name;
    }

    public Car getCar() {
        return this.car;
    }

    /**
     * Returns the current monetary balance of the player.
     * 
     * @return The balance value.
     */
    public int getBalance() {
        return this.balance;
    }

    /**
     * Adds an amount to the player's balance.
     * 
     * @param amount The amount to be credited. Must be positive.
     */
    public void credit(int amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    /**
     * Subtracts an amount from the player's balance.
     * Does not check if the player has sufficient balance; this logic
     * is usually handled by a service or game controller.
     * 
     * @param amount The amount to be debited. Must be positive.
     */
    public void debit(int amount) {
        if (amount > 0) {
            this.balance -= amount;
        }
    }

    /**
     * Transfers an amount from the current player to a receiving player.
     * 
     * @param receiver The player who will receive the money.
     * @param amount   The amount to be transferred.
     */
    public void pay(Player receiver, int amount) {
        this.debit(amount);
        receiver.credit(amount);
    }

    /**
     * Associates a property with the player and debits its purchase value.
     * 
     * @param property The property to be purchased.
     */
    public void buyProperty(Property property) {
        this.debit(property.getCost());
        this.ownedProperties.add(property);
        property.setOwner(this);
    }

   /**
     * Removes a property from the player's ownership list.
     * Note: Financial credit is handled externally (e.g., by the Facade).
     * * @param property The property to be sold/removed.
     */
    public void sellProperty(Property property) {
        if (this.ownedProperties.contains(property)) {
            this.ownedProperties.remove(property);
        }
    }
    /**
     * Checks if the player has properties that can be sold/mortgaged.
     * 
     * @return true if the player has at least one property.
     */
    public boolean hasLiquidAssets() {
        return !this.ownedProperties.isEmpty();
    }

    /**
     * Returns the list of player's properties.
     * 
     * @return A new list containing the properties to avoid external
     *         modification.
     */
    public List<Property> getLiquidAssets() {
        return new ArrayList<>(this.ownedProperties);
    }

    /**
     * Sells a property back to the bank for half its purchase price.
     * @param asset The property to be liquidated.
     */
    public void liquidate(Property asset, Bank bank) {
        if (this.ownedProperties.contains(asset)) {
            int sellPrice = asset.getCost() / 2; // TODO: change to 90%
            this.credit(sellPrice);
            this.ownedProperties.remove(asset);
            asset.setOwner(null);
            bank.returnPropertyToBank(asset);
        }
    }

    /**
     * Zeros the balance and removes ownership of all player's properties.
     */
    public void declareBankruptcy(Bank bank) {
        this.balance = 0;
        // Converts the list to a stream to avoid ConcurrentModificationException
        // while removing ownership.
        new ArrayList<>(this.ownedProperties).forEach(prop -> {
            prop.setOwner(null);
            bank.returnPropertyToBank(prop);
        });
        this.ownedProperties.clear();
    }

    /**
     * Checks if the player is currently in prison.
     * 
     * @return true if the player is in prison, false otherwise.
     */
    public boolean isInPrison() {
        return this.inPrison;
    }

    /**
     * Returns the number of turns the player has spent in prison.
     * 
     * @return The number of turns in prison.
     */
    public int getTurnsInPrison() {
        return this.turnsInPrison;
    }

    /**
     * Sends the player to prison, setting their position and state.
     * 
     * @param prisonSpace The prison space to send the player to.
     */
    public void sendToPrison(Space prisonSpace) {
        this.inPrison = true;
        this.turnsInPrison = 0;
        this.consecutiveDoubles = 0;
        this.car.setInPrison(true);
        this.car.setPosition(prisonSpace);
    }

    /**
     * Increments the counter of turns in prison.
     */
    public void incrementTurnsInPrison() {
        if (this.inPrison) {
            this.turnsInPrison++;
        }
    }

    /**
     * Releases the player from prison, resetting their state.
     */
    public void releaseFromPrison() {
        this.inPrison = false;
        this.turnsInPrison = 0;
        this.consecutiveDoubles = 0;
        this.car.setInPrison(false);
    }

    /**
     * Checks if the player has a "Get Out of Prison" card.
     * 
     * @return true if the player has at least one card, false otherwise.
     */
    public boolean hasGetOutPrisonCard() {
        return !this.getOutPrisonCards.isEmpty();
    }

    /**
     * Adiciona uma carta "Saia da Prisão" ao jogador.
     * 
     * @param card The card to be added.
     */
    public void receiveGetOutPrisonCard(GetOutPrisonCard card) {
        this.getOutPrisonCards.add(card);
        card.setOwner(this);
    }

    /**
     * Removes and returns the first "Get Out of Prison" card from the player.
     * 
     * @return The removed card, or null if the player doesn't have one.
     */
    public GetOutPrisonCard useGetOutPrisonCard() {
        if (getOutPrisonCards.isEmpty()) {
            return null;
        }
        return getOutPrisonCards.remove(0);
    }
    
    /**
     * Returns the number of "Get Out of Prison" cards the player has.
     * 
     * @return The number of cards.
     */
    public int getGetOutPrisonCardCount() {
        return this.getOutPrisonCards.size();
    }


    /**
     * Checks if the player can try to get out of prison by rolling dice (double).
     * 
     * @return true if the player is in prison, false otherwise.
     */
    public boolean canTryDoubleDice() {
        return this.inPrison;
    }

    /**
     * Returns the number of consecutive doubles the player has rolled.
     * 
     * @return The number of consecutive doubles.
     */
    public int getConsecutiveDoubles() {
        return this.consecutiveDoubles;
    }

    /**
     * Processes the result of a dice roll, checking if it's a double and if should go to prison.
     * 
     * @param dice1 The value of the first die.
     * @param dice2 The value of the second die.
     * @return true if the player should go to prison (3 consecutive doubles), false otherwise.
     */
    public boolean processDiceRoll(int dice1, int dice2) {
        if (dice1 == dice2) {
            this.consecutiveDoubles++;
            // If rolled 3 consecutive doubles, must go to prison
            if (this.consecutiveDoubles >= 3) {
                this.consecutiveDoubles = 0; // Reset counter
                return true; // Must go to prison (caller should call sendToPrison with prison space)
            }
        } else {
            // If not a double, reset the counter
            this.consecutiveDoubles = 0;
        }
        return false; // Does not go to prison
    }

    /**
     * Resets the counter of consecutive doubles.
     */
    public void resetConsecutiveDoubles() {
        this.consecutiveDoubles = 0;
    }

    /**
     * Checks if this player is equal to another object.
     * 
     * @param obj The object to be compared.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return name.equals(player.name);
    }

    /**
     * Returns the player's hash code based on the name.
     * 
     * @return The player's hash code.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    // ===== METHODS FOR SAVE/LOAD FUNCTIONALITY =====
    
    /**
     * Sets the balance directly (for loading saved games)
     * 
     * @param balance The balance to set
     */
    void setBalance(int balance) {
        this.balance = balance;
    }
    
    /**
     * Sets prison state directly (for loading saved games)
     * 
     * @param inPrison Whether player is in prison
     * @param turnsInPrison Number of turns spent in prison
     */
    void setPrisonState(boolean inPrison, int turnsInPrison) {
        this.inPrison = inPrison;
        this.turnsInPrison = turnsInPrison;
        this.car.setInPrison(inPrison);
    }
    
    /**
     * Sets consecutive doubles counter (for loading saved games)
     * 
     * @param consecutiveDoubles The consecutive doubles count
     */
    void setConsecutiveDoubles(int consecutiveDoubles) {
        this.consecutiveDoubles = consecutiveDoubles;
    }
    
    /**
     * Adds a property to the player without debiting (for loading saved games)
     * 
     * @param property The property to add
     */
    void addPropertyWithoutPayment(Property property) {
        if (!this.ownedProperties.contains(property)) {
            this.ownedProperties.add(property);
            property.setOwner(this);
        }
    }
    
    /**
     * Adds multiple GetOutPrisonCards (for loading saved games)
     * 
     * @param count Number of cards to add
     */
    void addGetOutPrisonCards(int count) {
        for (int i = 0; i < count; i++) {
            GetOutPrisonCard card = new GetOutPrisonCard("loaded_card", "Saída livre da prisão.", this);
            this.getOutPrisonCards.add(card);
        }
    }
}
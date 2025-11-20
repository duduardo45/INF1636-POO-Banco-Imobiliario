package model.core.entities;

import java.util.HashMap;
import java.util.Map;


enum Building {
    HOUSE,
    HOTEL
}

class Place extends Property {
    
    private final int base_rent;
    private final int house_price;
    private final int hotel_price;
    private final int hotel_rent;
    private final Map<Integer, Integer> house_rent;
    private Map<Building, Integer> buildings;
    private int current_rent; // How will we update this value when a house or hotel is built? -> updateCurrentRent()

    public Place (String name, int cost, Space next, int base_rent, int house_price, int hotel_price, int hotel_rent, Map<Integer, Integer> house_rent) {
        super(name,next, cost);
        this.base_rent = base_rent;
        this.house_price = house_price;
        this.hotel_price = hotel_price;
        this.hotel_rent = hotel_rent;
        this.house_rent = house_rent;
        current_rent = base_rent; // Initially, the rent is the base rent

        buildings = new HashMap<>();    
        buildings.put(Building.HOUSE, 0);
        buildings.put(Building.HOTEL, 0);

    }
    
    public int getTotalValue() {
        // Pega o custo base da classe pai (Property)
        int baseCost = super.getCost(); 
        
        // Calcula o valor das casas construídas
        int housesValue = getNumOfHouses() * this.house_price;
        
        // Calcula o valor do hotel construído
        int hotelsValue = getNumOfHotels() * this.hotel_price;
        
        return baseCost + housesValue + hotelsValue;
    }

    
    public int getBaseRent() {
        return base_rent;
    }

    public int getHousePrice() {
        return house_price;
    }

    public int getHotelPrice() {
        return hotel_price;
    }

    public int getHotelRent() {
        return hotel_rent;
    }

    public int getCurrentRent() {
        return current_rent;
    }

    public int getRentForHouses(int numHouses) {
        if (numHouses <= 0 || numHouses > 4) {
            return 0;
        }
        return house_rent.getOrDefault(numHouses, 0);
    }

    @Override
    protected boolean hasAtLeastOneHouse() {
        return getNumOfHouses() > 0 || getNumOfHotels() > 0;
    }

    /**
     * Calculates the total rent based on the formula:
     * Total Rent = Base Rent + (Rent Per House * Num Houses) + Hotel Rent
     * * Vb (Base Rent) = 10% of property cost
     * Vc (Rent per House) = 15% of property cost
     * Vh (Hotel Rent) = 30% of property cost
     */
    @Override
    public int calculateRent() {
        // 1. Get the property cost (Valor do território)
        int propertyCost = super.getCost(); 
        
        // Vb: Base rent value (10% of property cost)
        int baseRent = (int) (propertyCost * 0.10);
        
        // Vc: Rent value for a single house (15% of property cost)
        int rentPerHouse = (int) (propertyCost * 0.15);
        
        // n: Number of houses currently on the property
        int numberOfHouses = getNumOfHouses();
        
        int hotelRent = 0;
        if (getNumOfHotels() > 0) {
            hotelRent = this.hotel_rent;
        }
        
        // Va: Final Calculation
        return baseRent + (rentPerHouse * numberOfHouses) + hotelRent;
    }

    private void updateCurrentRent() {
        // Updates the cached rent value for display purposes
        this.current_rent = calculateRent();
    }

    public int getNumOfHouses() {
        Integer housesCount = buildings.get(Building.HOUSE);
        return housesCount == null ? 0 : housesCount;
    }

    public int getNumOfHotels() {
        Integer hotelsCount = buildings.get(Building.HOTEL);
        return hotelsCount == null ? 0 : hotelsCount;
    }

    public boolean canBuildHouse() {
        // If the player has less than 4 houses at this property, they can build a house
        int housesCount = getNumOfHouses();

        return housesCount < 4; 
    }
    
    public boolean canBuildHotel() {
        // If the player has at least one house at this property, they can build a hotel
        int housesCount = getNumOfHouses();
        int hotelsCount = getNumOfHotels();

        if (housesCount < 1 || hotelsCount == 1) {
            return false;
        } 
        else {
            return true;
        }        
    }


    public void buildHouse() {
        // Assuming that the canBuildHouse method has already been called and returned true in the controller
        if (!canBuildHouse()) {
            throw new IllegalStateException("Cannot build more houses on this property");
        }
        else{
        int housesCount = getNumOfHouses();
        buildings.put(Building.HOUSE, housesCount + 1);
        updateCurrentRent();
        }        
    }    

    public void buildHotel() {
        // Assuming the same as the buildHouse for the CanBuildHotel method.
        if (!canBuildHotel()) {
            throw new IllegalStateException("Cannot build a hotel on this property");
        }
        else {
        buildings.put(Building.HOTEL, 1);
        updateCurrentRent();
        }
    }

}
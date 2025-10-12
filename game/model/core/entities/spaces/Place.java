package model.core.entities.spaces;

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

    public Place (String name, int cost, int base_rent, int house_price, int hotel_price, int hotel_rent, Map<Integer, Integer> house_rent) {
        super(name, cost);
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

    public int getHousePrice() {
        return house_price;
    }

    public int getHotelPrice() {
        return hotel_price;
    }
    
    public int getCurrentRent() {
        return current_rent;
    }

    private void updateCurrentRent() {
        // Updates the current rent based on the number of houses and hotels built - is call
        int housesCount = getNumOfHouses();
        int hotelsCount = getNumOfHotels();

        int currentHouseRent = (housesCount > 0) ? house_rent.get(housesCount) : 0;
        int currentHotelRent = (hotelsCount > 0) ? hotel_rent : 0;

        current_rent = currentHouseRent + currentHotelRent;
    }

    private int getNumOfHouses() {
        Integer housesCount = buildings.get(Building.HOUSE);
        return housesCount == null ? 0 : housesCount;
    }

    private int getNumOfHotels() {
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
        // The responsibility of decreasing the player's money is in the controller class
        int housesCount = getNumOfHouses();
        buildings.put(Building.HOUSE, housesCount + 1);
        updateCurrentRent();
    }    

    public void buildHotel() {
        // Assuming the same as the buildHouse for the CanBuildHotel method.
        buildings.put(Building.HOTEL, 1);
        updateCurrentRent();
    }

	@Override
	public void event() {
		// TODO Implement event
		
	}


}
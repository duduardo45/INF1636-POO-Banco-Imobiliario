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

    public Place (String name, Space next, int cost, int base_rent, int house_price, int hotel_price, int hotel_rent, Map<Integer, Integer> house_rent) {
        super(name, next, cost);
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

    
    public int getCurrentRent() {
        return current_rent;
    }

    private void updateCurrentRent() {
        // Updates the current rent based on the number of houses and hotels built
        // Segue a mesma lógica do calculateRent()
        int housesCount = getNumOfHouses();
        int hotelsCount = getNumOfHotels();

        // Se tem hotel, aluguel é o valor do hotel
        if (hotelsCount > 0) {
            current_rent = this.hotel_rent;
        }
        // Se tem casas, aluguel é baseado no número de casas
        else if (housesCount > 0) {
            current_rent = house_rent.getOrDefault(housesCount, 0);
        }
        // Sem casas nem hotéis, não cobra aluguel
        else {
            current_rent = 0;
        }
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
	public void event(Player player) {
		// Chama o método handleRentPayment da classe pai
		handleRentPayment(player);
	}
	
	/**
	 * Verifica se a propriedade tem pelo menos uma casa.
	 * Sobrescreve o método da classe pai.
	 * 
	 * @return true se tem pelo menos 1 casa, false caso contrário.
	 */
	@Override
	protected boolean hasAtLeastOneHouse() {
		return getNumOfHouses() > 0 || getNumOfHotels() > 0;
	}
	
	/**
	 * Calcula o valor do aluguel baseado no número de casas e hotéis.
	 * Regras corretas:
	 * - 0 casas: 0 (não cobra aluguel)
	 * - 1+ casas: valor da tabela house_rent[casas]
	 * - 1 hotel: valor de hotel_rent
	 * 
	 * @return O valor do aluguel calculado.
	 */
	@Override
	public int calculateRent() {
		int housesCount = getNumOfHouses();
		int hotelsCount = getNumOfHotels();
		
		// Se tem hotel, aluguel é o valor do hotel
		if (hotelsCount > 0) {
			return this.hotel_rent;
		}
		
		// Se tem casas, aluguel é baseado no número de casas
		if (housesCount > 0) {
			return house_rent.getOrDefault(housesCount, 0);
		}
		
		// Sem casas nem hotéis, não cobra aluguel
		return 0;
	}
	
	/**
	 * Retorna o número de casas construídas.
	 * 
	 * @return O número de casas.
	 */
	public int getNumOfHouses() {
		Integer housesCount = buildings.get(Building.HOUSE);
		return housesCount == null ? 0 : housesCount;
	}
	
	/**
	 * Retorna o número de hotéis construídos.
	 * 
	 * @return O número de hotéis.
	 */
	public int getNumOfHotels() {
		Integer hotelsCount = buildings.get(Building.HOTEL);
		return hotelsCount == null ? 0 : hotelsCount;
	}
	
	/**
	 * Retorna o aluguel para um número específico de casas.
	 * 
	 * @param housesCount O número de casas.
	 * @return O valor do aluguel para esse número de casas.
	 */
	public int getRentForHouses(int housesCount) {
		return house_rent.getOrDefault(housesCount, 0);
	}
	
	/**
	 * Retorna o aluguel base do lugar.
	 * 
	 * @return O valor do aluguel base.
	 */
	public int getBaseRent() {
		return this.base_rent;
	}
	
	/**
	 * Retorna o preço de uma casa.
	 * 
	 * @return O preço de uma casa.
	 */
	public int getHousePrice() {
		return this.house_price;
	}
	
	/**
	 * Retorna o preço de um hotel.
	 * 
	 * @return O preço de um hotel.
	 */
	public int getHotelPrice() {
		return this.hotel_price;
	}
	
	/**
	 * Retorna o aluguel de um hotel.
	 * 
	 * @return O aluguel de um hotel.
	 */
	public int getHotelRent() {
		return this.hotel_rent;
	}


}
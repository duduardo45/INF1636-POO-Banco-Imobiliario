package model.core.entities.spaces;

import java.util.Map;

/**
 * Representa um lugar (território) no jogo onde podem ser construídas casas e hotéis.
 * O aluguel é calculado baseado no número de construções.
 */
public class Place extends Property {
    
    private final int baseRent;
    private final int housePrice;
    private final int hotelPrice;
    private final int hotelRent;
    private final Map<Integer, Integer> houseRentTable; // Tabela de aluguel por número de casas
    private Building building;

    public Place(String name, int cost, int baseRent, int housePrice, int hotelPrice, int hotelRent, Map<Integer, Integer> houseRentTable) {
        super(name, cost);
        this.baseRent = baseRent;
        this.housePrice = housePrice;
        this.hotelPrice = hotelPrice;
        this.hotelRent = hotelRent;
        this.houseRentTable = houseRentTable;
        this.building = new Building();
        this.currentRent = baseRent; // Inicialmente, o aluguel é o aluguel base
    }

    public int getHousePrice() {
        return housePrice;
    }

    public int getHotelPrice() {
        return hotelPrice;
    }
    
    public Building getBuilding() {
        return building;
    }
    
    public int getBaseRent() {
        return baseRent;
    }
    
    public int getHotelRent() {
        return hotelRent;
    }
    
    public Map<Integer, Integer> getHouseRentTable() {
        return houseRentTable;
    }

    @Override
    public int calculateRent() {
        int calculatedRent;
        
        if (building.getHotels() > 0) {
            // Se tem hotel, aluguel é o valor do hotel
            calculatedRent = hotelRent;
        } else if (building.getHouses() > 0) {
            // Se tem casas, consulta a tabela de aluguel
            calculatedRent = houseRentTable.getOrDefault(building.getHouses(), baseRent);
        } else {
            // Se não tem construções, aluguel é o valor base
            calculatedRent = baseRent;
        }
        
        // Atualiza o aluguel atual
        this.currentRent = calculatedRent;
        
        return calculatedRent;
    }
    
    @Override
    public boolean shouldChargeRent() {
        // Place só cobra aluguel se tem dono E tem pelo menos 1 casa (regra da iteração)
        return isOwned() && building.hasAtLeastOneHouse();
    }
    
    /**
     * Adiciona uma casa à propriedade.
     * @return true se conseguiu adicionar
     */
    public boolean addHouse() {
        boolean success = building.addHouse();
        if (success) {
            calculateRent(); // Recalcula o aluguel
        }
        return success;
    }
    
    /**
     * Adiciona um hotel à propriedade.
     * @return true se conseguiu adicionar
     */
    public boolean addHotel() {
        boolean success = building.addHotel();
        if (success) {
            calculateRent(); // Recalcula o aluguel
        }
        return success;
    }
    
    /**
     * Verifica se pode construir uma casa.
     * @return true se pode construir casa
     */
    public boolean canBuildHouse() {
        return building.getHouses() < 4 && building.getHotels() == 0;
    }
    
    /**
     * Verifica se pode construir um hotel.
     * @return true se pode construir hotel
     */
    public boolean canBuildHotel() {
        return building.getHouses() == 4 && building.getHotels() == 0;
    }

    @Override
    public void event() {
        // A lógica específica será tratada pelo GameModel
        // que chamará os métodos de cálculo e pagamento de aluguel
    }
}
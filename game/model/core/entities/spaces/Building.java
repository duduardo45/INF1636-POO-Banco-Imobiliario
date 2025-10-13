package model.core.entities.spaces;

/**
 * Representa as construções (casas e hotéis) em uma propriedade Place.
 * Controla o número de casas e hotéis para cálculo de aluguel.
 */
public class Building {
    private int houses;
    private int hotels;
    
    public Building() {
        this.houses = 0;
        this.hotels = 0;
    }
    
    public int getHouses() {
        return houses;
    }
    
    public void setHouses(int houses) {
        this.houses = Math.max(0, houses);
    }
    
    public int getHotels() {
        return hotels;
    }
    
    public void setHotels(int hotels) {
        this.hotels = Math.max(0, hotels);
    }
    
    /**
     * Adiciona uma casa à propriedade.
     * @return true se conseguiu adicionar, false se já tem hotel
     */
    public boolean addHouse() {
        if (hotels > 0) {
            return false; // Não pode ter casas se já tem hotel
        }
        houses++;
        return true;
    }
    
    /**
     * Adiciona um hotel à propriedade.
     * Remove todas as casas (regra do jogo).
     * @return true se conseguiu adicionar
     */
    public boolean addHotel() {
        if (houses < 4) {
            return false; // Precisa de 4 casas para construir hotel
        }
        houses = 0;
        hotels = 1;
        return true;
    }
    
    /**
     * Verifica se a propriedade tem pelo menos uma casa (incluindo hotel).
     * @return true se tem 1+ casas ou 1+ hotéis
     */
    public boolean hasAtLeastOneHouse() {
        return houses > 0 || hotels > 0;
    }
    
    /**
     * Retorna o total de construções (casas + hotéis).
     * @return número total de construções
     */
    public int getTotalBuildings() {
        return houses + hotels;
    }
    
    @Override
    public String toString() {
        if (hotels > 0) {
            return hotels + " hotel(is)";
        } else if (houses > 0) {
            return houses + " casa(s)";
        } else {
            return "terreno vazio";
        }
    }
}

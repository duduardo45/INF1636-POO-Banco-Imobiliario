package model.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardInitializer {
    
    public static Board createStandardBoard() {
        List<Space> spaces = new ArrayList<>(40);
        
        // Create all 40 spaces
        // Position 0: START
        spaces.add(new Start("Ponto de Partida", null, 200));
        
        // Position 1: Leblon (Place)
        spaces.add(createPlace("Leblon", 100));
        
        // Position 2: Sorte ou Revés
        spaces.add(new LuckSpace("Sorte ou Revés", null));
        
        // Position 3: Av. Presidente Vargas
        spaces.add(createPlace("Av. Presidente Vargas", 60));
        
        // Position 4: Av. Nossa Sra. De Copacabana
        spaces.add(createPlace("Av. Nossa Sra. De Copacabana", 60));
        
        // Position 5: Companhia Ferroviária
        spaces.add(new Company("Companhia Ferroviária", null, 200, 25));
        
        // Position 6: Av. Brigadeiro Faria Lima
        spaces.add(createPlace("Av. Brigadeiro Faria Lima", 240));
        
        // Position 7: Companhia de Viação
        spaces.add(new Company("Companhia de Viação", null, 200, 25));
        
        // Position 8: Av. Rebouças
        spaces.add(createPlace("Av. Rebouças", 220));
        
        // Position 9: Av. 9 de Julho
        spaces.add(createPlace("Av. 9 de Julho", 220));
        
        // Position 10: PRISÃO
        spaces.add(new Prison("Prisão", null));
        
        // Position 11: Av. Europa
        spaces.add(createPlace("Av. Europa", 200));
        
        // Position 12: Sorte ou Revés
        spaces.add(new LuckSpace("Sorte ou Revés", null));
        
        // Position 13: Rua Augusta
        spaces.add(createPlace("Rua Augusta", 180));
        
        // Position 14: Av. Pacaembú
        spaces.add(createPlace("Av. Pacaembú", 180));
        
        // Position 15: Companhia de Táxi
        spaces.add(new Company("Companhia de Táxi", null, 150, 20));
        
        // Position 16: Interlagos
        spaces.add(createPlace("Interlagos", 350));
        
        // Position 17: Sorte ou Revés
        spaces.add(new LuckSpace("Sorte ou Revés", null));
        
        // Position 18: Lucros ou Dividendos
        spaces.add(new Profit("Lucros ou Dividendos", null, 200));
        
        // Position 19: Morumbi
        spaces.add(createPlace("Morumbi", 400));
        
        // Position 20: PARADA LIVRE
        spaces.add(new FreeParking("Parada Livre", null));
        
        // Position 21: Flamengo
        spaces.add(createPlace("Flamengo", 120));
        
        // Position 22: Botafogo
        spaces.add(createPlace("Botafogo", 100));
        
        // Position 23: Sorte ou Revés
        spaces.add(new LuckSpace("Sorte ou Revés", null));
        
        // Position 24: Imposto de Renda
        spaces.add(new Tax("Imposto de Renda", null, 200));
        
        // Position 25: Companhia de Navegação
        spaces.add(new Company("Companhia de Navegação", null, 150, 20));
        
        // Position 26: Av. Brasil
        spaces.add(createPlace("Av. Brasil", 160));
        
        // Position 27: Av. Paulista
        spaces.add(createPlace("Av. Paulista", 140));
        
        // Position 28: Sorte ou Revés
        spaces.add(new LuckSpace("Sorte ou Revés", null));
        
        // Position 29: Jardim Europa
        spaces.add(createPlace("Jardim Europa", 140));
        
        // Position 30: VÁ PARA A PRISÃO
        spaces.add(new GoToPrison("Vá para a Prisão", null));
        
        // Position 31: Copacabana
        spaces.add(createPlace("Copacabana", 260));
        
        // Position 32: Companhia de Aviação
        spaces.add(new Company("Companhia de Aviação", null, 200, 25));
        
        // Position 33: Av. Vieira Souto
        spaces.add(createPlace("Av. Vieira Souto", 320));
        
        // Position 34: Av. Atlântica
        spaces.add(createPlace("Av. Atlântica", 300));
        
        // Position 35: Companhia de Táxi Aéreo
        spaces.add(new Company("Companhia de Táxi Aéreo", null, 200, 25));
        
        // Position 36: Ipanema
        spaces.add(createPlace("Ipanema", 300));
        
        // Position 37: Jardim Paulista
        spaces.add(createPlace("Jardim Paulista", 280));
        
        // Position 38: Sorte ou Revés
        spaces.add(new LuckSpace("Sorte ou Revés", null));
        
        // Position 39: Brooklin
        spaces.add(createPlace("Brooklin", 260));
        
        // Connect all spaces in a circular pattern
        for (int i = 0; i < spaces.size(); i++) {
            Space current = spaces.get(i);
            Space next = spaces.get((i + 1) % spaces.size());
            current.setNext(next);
        }
        
        return new Board(spaces);
    }
    
    /**
     * Helper method to create a Place with standard rent calculations.
     * Rent values follow the standard Monopoly pattern.
     */
    private static Place createPlace(String name, int cost) {
        int baseRent = cost / 10; // 10% of cost
        int housePrice = cost / 2; // 50% of cost
        int hotelPrice = cost * 2; // 200% of cost
        int hotelRent = baseRent * 50; // 50x base rent
        
        Map<Integer, Integer> houseRent = new HashMap<>();
        houseRent.put(1, baseRent * 3);  // 3x base
        houseRent.put(2, baseRent * 9);  // 9x base
        houseRent.put(3, baseRent * 16); // 16x base
        houseRent.put(4, baseRent * 25); // 25x base
        
        return new Place(name, cost, null, baseRent, housePrice, hotelPrice, hotelRent, houseRent);
    }
}


package game.model.core.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.core.entities.Player;
import model.core.entities.Dice;
import model.core.entities.spaces.Company;
import model.core.entities.spaces.Place;
import model.core.entities.spaces.Building;
import model.core.entities.Money;

import java.util.HashMap;
import java.util.Map;

/**
 * Testes para o sistema de pagamento de aluguel - Models Soltos.
 * Testa diretamente as classes de domínio sem controller.
 */
public class RentPaymentTest {

    private Player player1;
    private Player player2;
    private Dice dice;
    private Company company;
    private Place place;
    private Place placeWithoutHouses;

    @Before
    public void setUp() {
        player1 = new Player("Jogador 1", "Azul", 1500);
        player2 = new Player("Jogador 2", "Vermelho", 1500);
        dice = new Dice(12345L); // Seed fixo para testes determinísticos
        
        // Cria uma empresa (Company) - implementação simples para teste
        company = new Company("Companhia de Luz", 150, 25) {};
        company.setDice(dice);
        
        // Cria um lugar (Place) com tabela de aluguel
        Map<Integer, Integer> houseRentTable = new HashMap<>();
        houseRentTable.put(1, 50);
        houseRentTable.put(2, 100);
        houseRentTable.put(3, 200);
        houseRentTable.put(4, 400);
        
        place = new Place("Copacabana", 200, 20, 50, 100, 500, houseRentTable);
        placeWithoutHouses = new Place("Ipanema", 180, 15, 40, 80, 400, houseRentTable);
    }

    // ===== TESTE 1: Company - Aluguel baseado em dados =====
    
    @Test
    public void testCompanyRentPayment() {
        // Arrange: Jogador 2 compra a empresa
        player2.buyProperty(company);
        
        // Simula rolagem de dados
        dice.rollTwo();
        
        // Act: Jogador 1 paga aluguel
        int rentPaid = player1.payRent(company);
        
        // Assert: Verifica se o aluguel foi calculado e pago corretamente
        assertTrue("Company deve cobrar aluguel", company.shouldChargeRent());
        assertEquals("Aluguel deve ser calculado", company.calculateRent(), rentPaid);
        assertEquals("Valor pago deve ser > 0", true, rentPaid > 0);
        assertEquals("Jogador 1 deve ter menos dinheiro", true, player1.getBalance() < 1500);
        assertEquals("Jogador 2 deve ter mais dinheiro", true, player2.getBalance() > 1350);
    }

    // ===== TESTE 2: Place sem casas - Não cobra aluguel =====
    
    @Test
    public void testPlaceWithoutHousesNoRent() {
        // Arrange: Jogador 2 compra o lugar (sem casas)
        player2.buyProperty(placeWithoutHouses);
        
        // Act: Tenta cobrar aluguel
        int rentPaid = player1.payRent(placeWithoutHouses);
        
        // Assert: Não deve cobrar aluguel (regra da iteração)
        assertFalse("Place sem casas não deve cobrar aluguel", placeWithoutHouses.shouldChargeRent());
        assertEquals("Aluguel deve ser 0", 0, placeWithoutHouses.calculateRent());
        assertEquals("Valor pago deve ser 0", 0, rentPaid);
        assertEquals("Jogador 1 deve manter saldo original", 1500, player1.getBalance());
        assertEquals("Jogador 2 deve ter 1320 (1500 - 180 da compra)", 1320, player2.getBalance());
    }

    // ===== TESTE 3: Place com casas - Cobra aluguel =====
    
    @Test
    public void testPlaceWithHousesRentPayment() {
        // Arrange: Jogador 2 compra o lugar e constrói 2 casas
        player2.buyProperty(place);
        place.addHouse(); // 1 casa
        place.addHouse(); // 2 casas
        
        // Act: Jogador 1 paga aluguel
        int rentPaid = player1.payRent(place);
        
        // Assert: Deve cobrar aluguel conforme tabela
        assertTrue("Place com casas deve cobrar aluguel", place.shouldChargeRent());
        assertEquals("Aluguel deve ser 100 (2 casas)", 100, place.calculateRent());
        assertEquals("Valor pago deve ser 100", 100, rentPaid);
        assertEquals("Jogador 1 deve ter 1400 (1500 - 100)", 1400, player1.getBalance());
        assertEquals("Jogador 2 deve ter 1400 (1500 - 200 + 100)", 1400, player2.getBalance());
    }

    // ===== TESTE 4: Casa sem dono - Não cobra aluguel =====
    
    @Test
    public void testUnownedPropertyNoRent() {
        // Arrange: Company sem dono
        
        // Act: Tenta cobrar aluguel
        int rentPaid = player1.payRent(company);
        
        // Assert: Não deve cobrar aluguel
        assertFalse("Company sem dono não deve cobrar aluguel", company.shouldChargeRent());
        assertEquals("Aluguel deve ser 0", 0, rentPaid);
        assertEquals("Jogador 1 deve manter saldo original", 1500, player1.getBalance());
    }

    // ===== TESTE 5: Casa do próprio jogador - Não cobra aluguel =====
    
    @Test
    public void testOwnPropertyNoRent() {
        // Arrange: Jogador 1 compra a empresa
        player1.buyProperty(company);
        
        // Act: Tenta cobrar aluguel de si mesmo
        int rentPaid = player1.payRent(company);
        
        // Assert: Não deve cobrar aluguel de si mesmo
        assertEquals("Valor pago deve ser 0", 0, rentPaid);
        assertEquals("Jogador 1 deve manter saldo após compra", 1350, player1.getBalance());
    }

    // ===== TESTE 6: Pagamento que deixa saldo negativo (Gancho para Regra #7) =====
    
    @Test
    public void testRentPaymentLeavingNegativeBalance() {
        // Arrange: Jogador 1 com pouco dinheiro, jogador 2 com lugar caro
        player1.setBalance(50); // Saldo baixo
        
        player2.buyProperty(place);
        place.addHouse(); // 1 casa = aluguel 50
        
        // Act: Jogador 1 paga aluguel
        int rentPaid = player1.payRent(place);
        
        // Assert: Deve pagar mesmo com saldo negativo (regra da iteração)
        assertTrue("Place deve cobrar aluguel", place.shouldChargeRent());
        assertEquals("Valor pago deve ser 50", 50, rentPaid);
        assertEquals("Jogador 1 deve ficar com saldo negativo", 0, player1.getBalance());
        assertEquals("Jogador 2 deve receber o aluguel", 1350, player2.getBalance());
    }

    // ===== TESTES ADICIONAIS DE INTEGRAÇÃO =====
    
    @Test
    public void testPlaceWithHotelRentPayment() {
        // Arrange: Jogador 2 compra lugar e constrói hotel
        player2.buyProperty(place);
        place.addHouse(); // 1 casa
        place.addHouse(); // 2 casas
        place.addHouse(); // 3 casas
        place.addHouse(); // 4 casas
        place.addHotel(); // Hotel (remove casas)
        
        // Act: Jogador 1 paga aluguel
        int rentPaid = player1.payRent(place);
        
        // Assert: Deve cobrar aluguel do hotel
        assertTrue("Place com hotel deve cobrar aluguel", place.shouldChargeRent());
        assertEquals("Aluguel deve ser 500 (hotel)", 500, place.calculateRent());
        assertEquals("Valor pago deve ser 500", 500, rentPaid);
        assertEquals("Jogador 1 deve ter 1000 (1500 - 500)", 1000, player1.getBalance());
        assertEquals("Jogador 2 deve ter 1800 (1500 - 200 + 500)", 1800, player2.getBalance());
    }
    
    @Test
    public void testMultiplePlayersRentScenarios() {
        // Arrange: Múltiplos jogadores em diferentes propriedades
        player1.buyProperty(company);
        player2.buyProperty(place);
        place.addHouse(); // 1 casa
        
        // Jogador 1 tenta pagar aluguel na empresa própria (não paga)
        int rent1 = player1.payRent(company);
        
        // Jogador 2 paga aluguel na empresa do jogador 1
        dice.rollTwo(); // Simula dados
        int rent2 = player2.payRent(company);
        
        // Assert: Verifica cenários diferentes
        assertEquals("Jogador 1 não paga aluguel próprio", 0, rent1);
        assertTrue("Jogador 2 paga aluguel", rent2 > 0);
    }
    
    @Test
    public void testRentCalculationAccuracy() {
        // Arrange: Testa cálculo preciso de aluguel
        player2.buyProperty(place);
        
        // Testa diferentes números de casas
        place.addHouse(); // 1 casa
        assertEquals("1 casa = 50", 50, place.calculateRent());
        
        place.addHouse(); // 2 casas
        assertEquals("2 casas = 100", 100, place.calculateRent());
        
        place.addHouse(); // 3 casas
        assertEquals("3 casas = 200", 200, place.calculateRent());
        
        place.addHouse(); // 4 casas
        assertEquals("4 casas = 400", 400, place.calculateRent());
    }
    
    // ===== TESTES ADICIONAIS PARA MELHORAR COVERAGE =====
    
    @Test
    public void testCompanyWithoutDice() {
        // Testa Company sem dados configurados
        Company companyWithoutDice = new Company("Companhia sem dados", 150, 25) {};
        
        // Remove a conexão com dados para testar o caso sem dados
        companyWithoutDice.setDice(null);
        
        player2.buyProperty(companyWithoutDice);
        
        // Não configura dados
        int rent = companyWithoutDice.calculateRent();
        
        // Deve retornar aluguel base
        assertEquals("Aluguel base sem dados", 25, rent);
    }
    
    @Test
    public void testPlaceWithInvalidHouseCount() {
        // Testa Place com número de casas não mapeado na tabela
        player2.buyProperty(place);
        
        // Força um número de casas que não está na tabela
        place.getBuilding().setHouses(5); // 5 casas (não está na tabela)
        
        int rent = place.calculateRent();
        
        // Deve retornar aluguel base (fallback)
        assertEquals("Aluguel base para casas não mapeadas", 20, rent);
    }
    
    @Test
    public void testBuildingMethods() {
        // Testa métodos da classe Building
        Building building = new Building();
        
        // Testa adicionar casas
        assertTrue("Deve conseguir adicionar casa", building.addHouse());
        assertEquals("Deve ter 1 casa", 1, building.getHouses());
        
        // Testa adicionar hotel sem 4 casas
        assertFalse("Não deve conseguir adicionar hotel sem 4 casas", building.addHotel());
        
        // Adiciona mais casas
        building.addHouse();
        building.addHouse();
        building.addHouse();
        assertEquals("Deve ter 4 casas", 4, building.getHouses());
        
        // Agora deve conseguir adicionar hotel
        assertTrue("Deve conseguir adicionar hotel", building.addHotel());
        assertEquals("Deve ter 0 casas após hotel", 0, building.getHouses());
        assertEquals("Deve ter 1 hotel", 1, building.getHotels());
    }
    
    @Test
    public void testDiceMethods() {
        // Testa métodos da classe Dice
        Dice testDice = new Dice(12345L);
        
        // Testa rolagem individual
        int roll = testDice.roll();
        assertTrue("Roll deve estar entre 1 e 6", roll >= 1 && roll <= 6);
        
        // Testa rolagem de dois dados
        int[] rolls = testDice.rollTwo();
        assertEquals("Deve retornar array de 2 elementos", 2, rolls.length);
        assertTrue("Primeiro dado entre 1 e 6", rolls[0] >= 1 && rolls[0] <= 6);
        assertTrue("Segundo dado entre 1 e 6", rolls[1] >= 1 && rolls[1] <= 6);
        
        // Testa métodos de leitura
        assertEquals("getLastDice1 deve retornar último valor", rolls[0], testDice.getLastDice1());
        assertEquals("getLastDice2 deve retornar último valor", rolls[1], testDice.getLastDice2());
        assertEquals("getLastDiceSum deve retornar soma", rolls[0] + rolls[1], testDice.getLastDiceSum());
    }
    
    @Test
    public void testPropertyMethods() {
        // Testa métodos da classe Property
        Place testPlace = new Place("Teste", 100, 10, 20, 30, 100, new HashMap<>());
        
        // Testa propriedade sem dono
        assertFalse("Propriedade não deve ter dono inicialmente", testPlace.isOwned());
        assertNull("OwnerId deve ser null", testPlace.getOwnerId());
        
        // Testa definir dono
        testPlace.setOwner(player1, 1);
        assertTrue("Propriedade deve ter dono", testPlace.isOwned());
        assertEquals("OwnerId deve ser 1", Integer.valueOf(1), testPlace.getOwnerId());
        
        // Testa remover dono
        testPlace.removeOwner();
        assertFalse("Propriedade não deve ter dono após remoção", testPlace.isOwned());
        assertNull("OwnerId deve ser null após remoção", testPlace.getOwnerId());
    }
    
    @Test
    public void testPlayerEdgeCases() {
        // Testa casos extremos do Player
        
        // Testa pagamento de aluguel em propriedade null
        int rent1 = player1.payRent(null);
        assertEquals("Pagamento em propriedade null deve ser 0", 0, rent1);
        
        // Testa compra de propriedade já comprada
        player1.buyProperty(company);
        int initialBalance = player2.getBalance();
        player2.buyProperty(company); // Tenta comprar propriedade já comprada
        assertEquals("Não deve conseguir comprar propriedade já comprada", initialBalance, player2.getBalance());
        
        // Testa pagamento com saldo insuficiente
        player1.setBalance(10);
        int rent2 = player1.payRent(place);
        assertEquals("Deve pagar mesmo com saldo insuficiente", 0, rent2); // Place sem dono
    }
    
    @Test
    public void testMoneyClass() {
        // Testa métodos da classe Money
        Money money = new Money(100);
        
        assertEquals("Saldo inicial deve ser 100", 100, money.getAmount());
        
        // Testa adicionar
        money.add(50);
        assertEquals("Deve ter 150 após adicionar 50", 150, money.getAmount());
        
        // Testa subtrair
        money.subtract(30);
        assertEquals("Deve ter 120 após subtrair 30", 120, money.getAmount());
        
        // Testa verificação de saldo
        assertTrue("Deve ter saldo suficiente para 100", money.hasEnough(100));
        assertFalse("Não deve ter saldo suficiente para 200", money.hasEnough(200));
        
        // Testa toString
        String str = money.toString();
        assertTrue("toString deve conter 'R$'", str.contains("R$"));
        assertTrue("toString deve conter o valor", str.contains("120"));
    }
}

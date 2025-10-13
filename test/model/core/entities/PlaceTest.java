package model.core.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PlaceTest {
    private Place place;
    private Player player1;
    private Player player2;
    private Map<Integer, Integer> houseRentTable;
    
    @Before
    public void setUp() {
        // Creates rent table for houses
        houseRentTable = new HashMap<>();
        houseRentTable.put(1, 50);   // 1 house = 50 rent
        houseRentTable.put(2, 100);  // 2 houses = 100 rent
        houseRentTable.put(3, 200);  // 3 houses = 200 rent
        houseRentTable.put(4, 400);  // 4 houses = 400 rent
        
        // Creates Place property
        place = new Place("Test Place", 200, null, 20, 50, 200, 500, houseRentTable);
        
        // Creates players
        player1 = new Player("Player 1", "Blue", new Car("Blue", null), 1000);
        player2 = new Player("Player 2", "Red", new Car("Red", null), 1000);
    }
    
    // ========== TESTS FOR CONSTRUCTOR AND BASIC GETTERS ==========
    
    @Test
    public void testConstructor() {
        assertEquals("Test Place", place.getName());
        assertEquals(200, place.getCost());
        assertEquals(20, place.getBaseRent());
        assertEquals(50, place.getHousePrice());
        assertEquals(200, place.getHotelPrice());
        assertEquals(500, place.getHotelRent());
        assertEquals(0, place.getNumOfHouses());
        assertEquals(0, place.getNumOfHotels());
    }
    
    @Test
    public void testGetBaseRent() {
        assertEquals("Should return base rent", 20, place.getBaseRent());
    }
    
    @Test
    public void testGetHousePrice() {
        assertEquals("Should return house price", 50, place.getHousePrice());
    }
    
    @Test
    public void testGetHotelPrice() {
        assertEquals("Should return hotel price", 200, place.getHotelPrice());
    }
    
    @Test
    public void testGetHotelRent() {
        assertEquals("Should return hotel rent", 500, place.getHotelRent());
    }
    
    @Test
    public void testGetCurrentRent() {
        // Inicialmente deve retornar base_rent
        assertEquals("Should return base rent initially", 20, place.getCurrentRent());
        
        // After building 1 house
        place.buildHouse();
        assertEquals("Should return rent for 1 house", 50, place.getCurrentRent());
        
        // After building 2 houses
        place.buildHouse();
        assertEquals("Should return rent for 2 houses", 100, place.getCurrentRent());
        
        // After building hotel
        place.buildHouse();
        place.buildHouse();
        place.buildHotel();
        assertEquals("Should return house + hotel rent", 900, place.getCurrentRent()); // 400 + 500
    }
    
    // ========== TESTS FOR hasAtLeastOneHouse() ==========
    
    @Test
    public void testHasAtLeastOneHouseWithZeroHouses() {
        // 0 houses, 0 hotels
        assertFalse("Should not have at least one house with 0 houses and 0 hotels", 
                   place.hasAtLeastOneHouse());
    }
    
    @Test
    public void testHasAtLeastOneHouseWithOneHouse() {
        place.buildHouse();
        assertTrue("Should have at least one house with 1 house", 
                  place.hasAtLeastOneHouse());
    }
    
    @Test
    public void testHasAtLeastOneHouseWithHotel() {
        place.buildHouse(); // Build a house first
        place.buildHotel();
        assertTrue("Should have at least one house with 1 hotel", 
                  place.hasAtLeastOneHouse());
    }
    
    // ========== TESTS FOR calculateRent() ==========
    
    @Test
    public void testCalculateRentWithZeroHouses() {
        // 0 houses, 0 hotels
        assertEquals("Should return 0 with 0 houses and 0 hotels", 0, place.calculateRent());
    }
    
    @Test
    public void testCalculateRentWithOneHouse() {
        place.buildHouse();
        assertEquals("Should return rent for 1 house", 50, place.calculateRent());
    }
    
    @Test
    public void testCalculateRentWithTwoHouses() {
        place.buildHouse();
        place.buildHouse();
        assertEquals("Should return rent for 2 houses", 100, place.calculateRent());
    }
    
    @Test
    public void testCalculateRentWithThreeHouses() {
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        assertEquals("Should return rent for 3 houses", 200, place.calculateRent());
    }
    
    @Test
    public void testCalculateRentWithFourHouses() {
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        assertEquals("Should return rent for 4 houses", 400, place.calculateRent());
    }
    
    @Test
    public void testCalculateRentWithHotel() {
        // Builds 4 houses first
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        
        // Builds hotel
        place.buildHotel();
        assertEquals("Should return house + hotel rent", 900, place.calculateRent()); // 400 + 500
    }
    
    // ========== TESTS FOR isRentDue() ==========
    
    @Test
    public void testIsRentDueWithNoOwner() {
        // Propriedade sem dono
        assertFalse("Should not be rent due when property has no owner", 
                   place.isRentDue(player1));
    }
    
    @Test
    public void testIsRentDueWithVisitorAsOwner() {
        // Visitor is the owner themselves
        place.setOwner(player1);
        assertFalse("Should not be rent due when visitor is the owner", 
                   place.isRentDue(player1));
    }
    
    @Test
    public void testIsRentDueWithZeroHouses() {
        // Diferente dono mas sem casas
        place.setOwner(player2);
        assertFalse("Should not be rent due when property has 0 houses", 
                   place.isRentDue(player1));
    }
    
    @Test
    public void testIsRentDueWithOneHouse() {
        // Diferente dono com 1 casa
        place.setOwner(player2);
        place.buildHouse();
        assertTrue("Should be rent due when property has 1 house", 
                  place.isRentDue(player1));
    }
    
    @Test
    public void testIsRentDueWithHotel() {
        // Diferente dono com hotel
        place.setOwner(player2);
        place.buildHouse(); // Build a house first
        place.buildHotel();
        assertTrue("Should be rent due when property has hotel", 
                  place.isRentDue(player1));
    }
    
    // ========== TESTS FOR payRent() ==========
    
    @Test
    public void testPayRentWithNoOwner() {
        // Propriedade sem dono
        int player1InitialBalance = player1.getBalance();
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay 0 when property has no owner", 0, paidAmount);
        assertEquals("Player balance should not change", player1InitialBalance, player1.getBalance());
    }
    
    @Test
    public void testPayRentWithVisitorAsOwner() {
        // Visitor is the owner themselves
        place.setOwner(player1);
        int player1InitialBalance = player1.getBalance();
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay 0 when visitor is the owner", 0, paidAmount);
        assertEquals("Player balance should not change", player1InitialBalance, player1.getBalance());
    }
    
    @Test
    public void testPayRentWithZeroHouses() {
        // Diferente dono mas sem casas
        place.setOwner(player2);
        int player1InitialBalance = player1.getBalance();
        int player2InitialBalance = player2.getBalance();
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay 0 when property has 0 houses", 0, paidAmount);
        assertEquals("Player1 balance should not change", player1InitialBalance, player1.getBalance());
        assertEquals("Player2 balance should not change", player2InitialBalance, player2.getBalance());
    }
    
    @Test
    public void testPayRentWithOneHouse() {
        // Diferente dono com 1 casa
        place.setOwner(player2);
        place.buildHouse();
        int player1InitialBalance = player1.getBalance();
        int player2InitialBalance = player2.getBalance();
        int expectedRent = 50; // 1 casa = 50
        
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay rent for 1 house", expectedRent, paidAmount);
        assertEquals("Player1 should be debited", player1InitialBalance - expectedRent, player1.getBalance());
        assertEquals("Player2 should be credited", player2InitialBalance + expectedRent, player2.getBalance());
    }
    
    @Test
    public void testPayRentWithFourHouses() {
        // Diferente dono com 4 casas
        place.setOwner(player2);
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        int player1InitialBalance = player1.getBalance();
        int player2InitialBalance = player2.getBalance();
        int expectedRent = 400; // 4 casas = 400
        
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay rent for 4 houses", expectedRent, paidAmount);
        assertEquals("Player1 should be debited", player1InitialBalance - expectedRent, player1.getBalance());
        assertEquals("Player2 should be credited", player2InitialBalance + expectedRent, player2.getBalance());
    }
    
    @Test
    public void testPayRentWithHotel() {
        // Diferente dono com hotel
        place.setOwner(player2);
        place.buildHouse(); // Build a house first
        place.buildHotel();
        int player1InitialBalance = player1.getBalance();
        int player2InitialBalance = player2.getBalance();
        int expectedRent = 550; // house + hotel = 50 + 500
        
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay hotel rent", expectedRent, paidAmount);
        assertEquals("Player1 should be debited", player1InitialBalance - expectedRent, player1.getBalance());
        assertEquals("Player2 should be credited", player2InitialBalance + expectedRent, player2.getBalance());
    }
    
    @Test
    public void testPayRentWithInsufficientFunds() {
        // Visitante sem saldo suficiente
        place.setOwner(player2);
        place.buildHouse();
        player1.debit(950); // Deixa player1 com apenas 50
        int player1InitialBalance = player1.getBalance(); // 50
        int player2InitialBalance = player2.getBalance();
        int expectedRent = 50; // 1 casa = 50
        
        int paidAmount = place.payRent(player1);
        
        assertEquals("Should pay rent even with insufficient funds", expectedRent, paidAmount);
        assertEquals("Player1 should have 0 balance", player1InitialBalance - expectedRent, player1.getBalance());
        assertEquals("Player2 should be credited", player2InitialBalance + expectedRent, player2.getBalance());
    }
    
    // ========== TESTS FOR getRentForHouses() ==========
    
    @Test
    public void testGetRentForHouses() {
        assertEquals("Should return rent for 1 house", 50, place.getRentForHouses(1));
        assertEquals("Should return rent for 2 houses", 100, place.getRentForHouses(2));
        assertEquals("Should return rent for 3 houses", 200, place.getRentForHouses(3));
        assertEquals("Should return rent for 4 houses", 400, place.getRentForHouses(4));
        assertEquals("Should return 0 for 0 houses", 0, place.getRentForHouses(0));
        assertEquals("Should return 0 for invalid number", 0, place.getRentForHouses(5));
    }
    
    // ========== TESTS FOR getNumOfHouses() AND getNumOfHotels() ==========
    
    @Test
    public void testGetNumOfHousesInitially() {
        assertEquals("Should start with 0 houses", 0, place.getNumOfHouses());
    }
    
    @Test
    public void testGetNumOfHotelsInitially() {
        assertEquals("Should start with 0 hotels", 0, place.getNumOfHotels());
    }
    
    @Test
    public void testGetNumOfHousesAfterBuilding() {
        place.buildHouse();
        assertEquals("Should have 1 house after building one", 1, place.getNumOfHouses());
        
        place.buildHouse();
        assertEquals("Should have 2 houses after building two", 2, place.getNumOfHouses());
    }
    
    @Test
    public void testGetNumOfHotelsAfterBuilding() {
        place.buildHouse(); // Build a house first
        place.buildHotel();
        assertEquals("Should have 1 hotel after building one", 1, place.getNumOfHotels());
    }
    
    // ========== TESTS FOR canBuildHouse() ==========
    
    @Test
    public void testCanBuildHouseWithZeroHouses() {
        // Pode construir casa quando tem 0 casas
        assertTrue("Should be able to build house with 0 houses", place.canBuildHouse());
    }
    
    @Test
    public void testCanBuildHouseWithOneHouse() {
        place.buildHouse();
        assertTrue("Should be able to build house with 1 house", place.canBuildHouse());
    }
    
    @Test
    public void testCanBuildHouseWithTwoHouses() {
        place.buildHouse();
        place.buildHouse();
        assertTrue("Should be able to build house with 2 houses", place.canBuildHouse());
    }
    
    @Test
    public void testCanBuildHouseWithThreeHouses() {
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        assertTrue("Should be able to build house with 3 houses", place.canBuildHouse());
    }
    
    @Test
    public void testCanBuildHouseWithFourHouses() {
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        assertFalse("Should NOT be able to build house with 4 houses", place.canBuildHouse());
    }
    
    @Test
    public void testCanBuildHouseWithHotel() {
        // Builds 4 houses and then hotel
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        place.buildHouse();
        place.buildHotel();
        assertFalse("Should NOT be able to build house when hotel exists", place.canBuildHouse());
    }
    
    // ========== TESTS FOR event() ==========
    
    @Test
    public void testEventDelegatesToHandleRentPayment() {
        // Testa se event() delega para handleRentPayment
        place.setOwner(player2);
        place.buildHouse();
        int player1InitialBalance = player1.getBalance();
        int player2InitialBalance = player2.getBalance();
        int expectedRent = 50; // 1 casa = 50
        
        place.event(player1);
        
        assertEquals("Player1 should be debited", player1InitialBalance - expectedRent, player1.getBalance());
        assertEquals("Player2 should be credited", player2InitialBalance + expectedRent, player2.getBalance());
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testMultipleRentPayments() {
        // Multiple payments
        place.setOwner(player2);
        place.buildHouse();
        int player1InitialBalance = player1.getBalance();
        int player2InitialBalance = player2.getBalance();
        int expectedRent = 50; // 1 casa = 50
        
        // Primeiro pagamento
        int paid1 = place.payRent(player1);
        assertEquals("First payment should be successful", expectedRent, paid1);
        
        // Segundo pagamento
        int paid2 = place.payRent(player1);
        assertEquals("Second payment should be successful", expectedRent, paid2);
        
        // Verifica saldos finais
        assertEquals("Player1 should have paid twice", player1InitialBalance - (2 * expectedRent), player1.getBalance());
        assertEquals("Player2 should have received twice", player2InitialBalance + (2 * expectedRent), player2.getBalance());
    }
    
    @Test
    public void testRentCalculationAccuracy() {
        // Verifies precise calculation for each number of houses
        assertEquals("0 casas = 0", 0, place.calculateRent());
        
        place.buildHouse();
        assertEquals("1 casa = 50", 50, place.calculateRent());
        
        place.buildHouse();
        assertEquals("2 casas = 100", 100, place.calculateRent());
        
        place.buildHouse();
        assertEquals("3 casas = 200", 200, place.calculateRent());
        
        place.buildHouse();
        assertEquals("4 casas = 400", 400, place.calculateRent());
        
        place.buildHotel();
        assertEquals("House + Hotel = 900", 900, place.calculateRent()); // 400 + 500
    }
}
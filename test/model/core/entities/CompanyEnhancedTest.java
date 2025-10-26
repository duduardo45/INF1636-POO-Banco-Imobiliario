package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;



public class CompanyEnhancedTest {
    
    private Company company;
    private Space nextSpace;
    private Player owner;
    private Player visitor;
    private Car ownerCar, visitorCar;
    
    @Before
    public void setUp() {
        nextSpace = new Start("Next", null);
        company = new Company("Electric Company", nextSpace, 150, 4);
        
        Space startSpace = new Start("Start", null);
        ownerCar = new Car("Red", startSpace);
        visitorCar = new Car("Blue", startSpace);
        
        owner = new Player("Owner", "Red", ownerCar, 1500);
        visitor = new Player("Visitor", "Blue", visitorCar, 1500);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Electric Company", company.getName());
        assertEquals(nextSpace, company.getNext());
        assertEquals(150, company.getCost());
        assertEquals(4, company.getBaseRent());
        assertEquals(4, company.getCurrentRent()); // Should be initialized to base rent
    }
    
    @Test
    public void testHasAtLeastOneHouse() {
        // Companies always have "house" (always charge)
        assertTrue(company.hasAtLeastOneHouse());
    }
    
    @Test
    public void testCalculateRentWithoutDice() {
        assertEquals(4, company.calculateRent());
    }
    
    @Test
    public void testCalculateRentWithDice() {
        assertEquals(28, company.calculateRent(7)); // 4 * 7
        assertEquals(40, company.calculateRent(10)); // 4 * 10
        assertEquals(4, company.calculateRent(1)); // 4 * 1
        assertEquals(0, company.calculateRent(0)); // 4 * 0
    }
    
    @Test
    public void testCalculateRentWithNegativeDice() {
        assertEquals(-20, company.calculateRent(-5)); // 4 * -5
    }
    
    @Test
    public void testCalculateRentWithLargeDice() {
        assertEquals(48, company.calculateRent(12)); // 4 * 12 (double sixes)
    }
    
    @Test
    public void testRentCalculationConsistency() {
        // Both methods should return the same for base calculation
        assertEquals(company.calculateRent(), company.calculateRent(1));
    }
    
    @Test
    public void testCompanyWithDifferentBaseRent() {
        Company waterWorks = new Company("Water Works", null, 150, 6);
        
        assertEquals(6, waterWorks.calculateRent());
        assertEquals(42, waterWorks.calculateRent(7)); // 6 * 7
        assertEquals(60, waterWorks.calculateRent(10)); // 6 * 10
    }
    
    @Test
    public void testCompanyWithZeroBaseRent() {
        Company freeCompany = new Company("Free Company", null, 0, 0);
        
        assertEquals(0, freeCompany.calculateRent());
        assertEquals(0, freeCompany.calculateRent(7)); // 0 * 7
        assertEquals(0, freeCompany.calculateRent(10)); // 0 * 10
    }
    
    @Test
    public void testPropertyInheritanceBehavior() {
        // Test that Company properly inherits from Property
        assertNull(company.getOwner());
        assertFalse(company.isOwned());
        
        company.setOwner(owner);
        assertEquals(owner, company.getOwner());
        assertTrue(company.isOwned());
    }
    
    @Test
    public void testRentPaymentScenario() {
        company.setOwner(owner);
        
        int ownerInitialBalance = owner.getBalance();
        int visitorInitialBalance = visitor.getBalance();
        
        // Simulate rent payment using base rent
        int rentAmount = company.calculateRent();
        visitor.pay(owner, rentAmount);
        
        assertEquals(ownerInitialBalance + rentAmount, owner.getBalance());
        assertEquals(visitorInitialBalance - rentAmount, visitor.getBalance());
    }
    
    @Test
    public void testRentPaymentWithDiceScenario() {
        company.setOwner(owner);
        
        int ownerInitialBalance = owner.getBalance();
        int visitorInitialBalance = visitor.getBalance();
        int diceRoll = 8;
        
        // Simulate rent payment using dice-based rent
        int rentAmount = company.calculateRent(diceRoll);
        visitor.pay(owner, rentAmount);
        
        assertEquals(ownerInitialBalance + rentAmount, owner.getBalance());
        assertEquals(visitorInitialBalance - rentAmount, visitor.getBalance());
        assertEquals(32, rentAmount); // 4 * 8
    }
    
    @Test
    public void testIsRentDue() {
        // Company has no owner
        assertFalse(company.isRentDue(visitor));
        
        // Set owner
        company.setOwner(owner);
        
        // Visitor should pay rent
        assertTrue(company.isRentDue(visitor));
        
        // Owner should not pay rent to themselves
        assertFalse(company.isRentDue(owner));
    }
    
    @Test
    public void testPayRent() {
        company.setOwner(owner);
        
        int ownerInitialBalance = owner.getBalance();
        int visitorInitialBalance = visitor.getBalance();
        
        int paidAmount = company.payRent(visitor);
        
        assertEquals(4, paidAmount); // Base rent
        assertEquals(ownerInitialBalance + 4, owner.getBalance());
        assertEquals(visitorInitialBalance - 4, visitor.getBalance());
    }
    
    @Test
    public void testPayRentWhenNotDue() {
        // No owner set
        int visitorInitialBalance = visitor.getBalance();
        
        int paidAmount = company.payRent(visitor);
        
        assertEquals(0, paidAmount);
        assertEquals(visitorInitialBalance, visitor.getBalance()); // No change
    }
    
    @Test
    public void testCompanyNoHousesRule() {
        // Companies should not have houses/hotels like regular properties
        // This is implicit in the design - companies always charge rent
        assertTrue(company.hasAtLeastOneHouse()); // Always true for companies
        
        // Companies don't have building methods like Place does
        // This test verifies the design principle that companies are different from places
    }
}
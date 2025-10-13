package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PropertyTest {
    private Property property;
    private Player owner;
    private Player visitor;
    
    @Before
    public void setUp() {
        property = new Company("Test Property", null, 200, 200);;
        owner = new Player("Owner", "Blue", null, 1000);
        visitor = new Player("Visitor", "Red", null, 1000);
    }
    
    @Test
    public void testGetCost() {
        assertEquals(200, property.getCost());
    }
    
    @Test
    public void testGetOwner() {
        assertNull("New property should have no owner", property.getOwner());
        property.setOwner(owner);
        assertEquals(owner, property.getOwner());
    }
    
    @Test
    public void testIsOwned() {
        assertFalse("New property should not be owned", property.isOwned());
        property.setOwner(owner);
        assertTrue("Property should be owned after setting owner", property.isOwned());
    }
    
    @Test
    public void testSetOwner() {
        property.setOwner(owner);
        assertEquals(owner, property.getOwner());
        property.setOwner(null);
        assertNull("Owner should be null after setting to null", property.getOwner());
    }
    
    @Test
    public void testGetCurrentRent() {
        property.setCurrentRent(100);
        assertEquals(100, property.getCurrentRent());
    }
    
    @Test
    public void testSetCurrentRent() {
        property.setCurrentRent(150);
        assertEquals(150, property.getCurrentRent());
    }
    
    @Test
    public void testEventWithOwner() {
        property.setOwner(owner);
        // Company sempre cobra base_rent (200), não o valor de setCurrentRent
        int expectedRent = 200; // base_rent da Company
        int visitorInitialBalance = visitor.getBalance();
        int ownerInitialBalance = owner.getBalance();
        
        property.event(visitor);
        
        assertEquals(visitorInitialBalance - expectedRent, visitor.getBalance());
        assertEquals(ownerInitialBalance + expectedRent, owner.getBalance());
    }
    
    @Test
    public void testEventWithNoOwner() {
        int visitorInitialBalance = visitor.getBalance();
        property.event(visitor);
        assertEquals("Visitor's balance should not change when property has no owner",
                    visitorInitialBalance, visitor.getBalance());
    }
    
    @Test
    public void testEventWithVisitorAsOwner() {
        property.setOwner(visitor);
        int visitorInitialBalance = visitor.getBalance();
        
        property.event(visitor);
        
        assertEquals("Owner should not pay rent to themselves",
                    visitorInitialBalance, visitor.getBalance());
    }
    
    // ========== TESTES PARA ALUGUEL ==========
    
    @Test
    public void testIsRentDueWithNoOwner() {
        // Propriedade sem dono
        assertFalse("Should not be rent due when property has no owner", 
                   property.isRentDue(visitor));
    }
    
    @Test
    public void testIsRentDueWithVisitorAsOwner() {
        // Visitante é o próprio dono
        property.setOwner(visitor);
        assertFalse("Should not be rent due when visitor is the owner", 
                   property.isRentDue(visitor));
    }
    
    @Test
    public void testIsRentDueWithDifferentOwner() {
        // Diferente dono (Company sempre tem "casa")
        property.setOwner(owner);
        assertTrue("Should be rent due when property has different owner", 
                  property.isRentDue(visitor));
    }
    
    @Test
    public void testHasAtLeastOneHouseDefault() {
        // Testa implementação padrão (Company sempre retorna true)
        assertTrue("Company should always have at least one house", 
                  property.hasAtLeastOneHouse());
    }
    
    @Test
    public void testCalculateRentDefault() {
        // Testa implementação padrão (Company retorna base_rent)
        assertEquals("Company should return base rent", 200, property.calculateRent());
    }
    
    @Test
    public void testPayRentWithNoOwner() {
        // Propriedade sem dono
        int visitorInitialBalance = visitor.getBalance();
        int paidAmount = property.payRent(visitor);
        
        assertEquals("Should pay 0 when property has no owner", 0, paidAmount);
        assertEquals("Visitor balance should not change", visitorInitialBalance, visitor.getBalance());
    }
    
    @Test
    public void testPayRentWithVisitorAsOwner() {
        // Visitante é o próprio dono
        property.setOwner(visitor);
        int visitorInitialBalance = visitor.getBalance();
        int paidAmount = property.payRent(visitor);
        
        assertEquals("Should pay 0 when visitor is the owner", 0, paidAmount);
        assertEquals("Visitor balance should not change", visitorInitialBalance, visitor.getBalance());
    }
    
    @Test
    public void testPayRentWithDifferentOwner() {
        // Diferente dono (Company sempre cobra)
        property.setOwner(owner);
        int visitorInitialBalance = visitor.getBalance();
        int ownerInitialBalance = owner.getBalance();
        int expectedRent = 200; // base_rent da Company
        
        int paidAmount = property.payRent(visitor);
        
        assertEquals("Should pay the calculated rent", expectedRent, paidAmount);
        assertEquals("Visitor should be debited", visitorInitialBalance - expectedRent, visitor.getBalance());
        assertEquals("Owner should be credited", ownerInitialBalance + expectedRent, owner.getBalance());
    }
    
    @Test
    public void testPayRentWithInsufficientFunds() {
        // Visitante sem saldo suficiente
        property.setOwner(owner);
        visitor.debit(950); // Deixa visitor com apenas 50
        int visitorInitialBalance = visitor.getBalance(); // 50
        int ownerInitialBalance = owner.getBalance();
        int expectedRent = 200; // base_rent da Company
        
        int paidAmount = property.payRent(visitor);
        
        assertEquals("Should pay the calculated rent even with insufficient funds", expectedRent, paidAmount);
        assertEquals("Visitor should have negative balance", visitorInitialBalance - expectedRent, visitor.getBalance());
        assertEquals("Owner should be credited", ownerInitialBalance + expectedRent, owner.getBalance());
    }
    
    @Test
    public void testHandleRentPaymentDelegatesToPayRent() {
        // Testa se handleRentPayment delega para payRent
        property.setOwner(owner);
        int visitorInitialBalance = visitor.getBalance();
        int ownerInitialBalance = owner.getBalance();
        int expectedRent = 200; // base_rent da Company
        
        property.handleRentPayment(visitor);
        
        assertEquals("Visitor should be debited", visitorInitialBalance - expectedRent, visitor.getBalance());
        assertEquals("Owner should be credited", ownerInitialBalance + expectedRent, owner.getBalance());
    }
    
    @Test
    public void testMultipleRentPayments() {
        // Múltiplos pagamentos
        property.setOwner(owner);
        int visitorInitialBalance = visitor.getBalance();
        int ownerInitialBalance = owner.getBalance();
        int expectedRent = 200; // base_rent da Company
        
        // Primeiro pagamento
        int paid1 = property.payRent(visitor);
        assertEquals("First payment should be successful", expectedRent, paid1);
        
        // Segundo pagamento
        int paid2 = property.payRent(visitor);
        assertEquals("Second payment should be successful", expectedRent, paid2);
        
        // Verifica saldos finais
        assertEquals("Visitor should have paid twice", visitorInitialBalance - (2 * expectedRent), visitor.getBalance());
        assertEquals("Owner should have received twice", ownerInitialBalance + (2 * expectedRent), owner.getBalance());
    }
}
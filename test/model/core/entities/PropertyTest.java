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
        property.setCurrentRent(50);
        int visitorInitialBalance = visitor.getBalance();
        int ownerInitialBalance = owner.getBalance();
        
        property.event(visitor);
        
        assertEquals(visitorInitialBalance - 50, visitor.getBalance());
        assertEquals(ownerInitialBalance + 50, owner.getBalance());
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
}
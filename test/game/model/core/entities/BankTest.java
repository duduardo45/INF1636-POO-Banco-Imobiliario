package game.model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import game.model.core.entities.spaces.Property;

public class BankTest {
    private Bank bank;
    private List<Property> properties;
    private Property property;
    
    @Before
    public void setUp() {
        properties = new ArrayList<>();
        property = new Property("Test Property", 200);
        properties.add(property);
        bank = new Bank(1000, properties);
    }
    
    @Test
    public void testCredit() {
        bank.credit(500);
        assertEquals(1500, bank.getTreasuryBalance());
    }
    
    @Test
    public void testCreditWithNegativeAmount() {
        bank.credit(-100);
        assertEquals(1000, bank.getTreasuryBalance())
    }
    
    @Test
    public void testIsPropertyUnowned() {
        assertTrue("New property should be unowned", bank.isPropertyUnowned(property));
    }
    
    @Test
    public void testMarkPropertyAsOwned() {
        bank.markPropertyAsOwned(property);
        assertFalse("Property should not be unowned after marking as owned", 
                   bank.isPropertyUnowned(property));
    }
    
    @Test
    public void testReturnPropertyToBank() {
        bank.markPropertyAsOwned(property);
        bank.returnPropertyToBank(property);
        assertTrue("Property should be unowned after returning to bank", 
                  bank.isPropertyUnowned(property));
    }
}
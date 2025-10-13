package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

public class PlayerTest {
    private Player player;
    private Player otherPlayer;
    private Property property;
    
    @Before
    public void setUp() {
        player = new Player("Test Player", "Blue", 1000);
        otherPlayer = new Player("Other Player", "Red", 1000);
        property = new Company("Test Property", null, 200, 200);
    }
    
    @Test
    public void testGetName() {
        assertEquals("Test Player", player.getName());
    }
    
    @Test
    public void testGetBalance() {
        assertEquals(1000, player.getBalance());
    }
    
    @Test
    public void testCredit() {
        player.credit(500);
        assertEquals(1500, player.getBalance());
    }
    
    @Test
    public void testCreditWithNegativeAmount() {
        player.credit(-100);
        assertEquals("Balance should not change with negative credit", 
                    1000, player.getBalance());
    }
    
    @Test
    public void testDebit() {
        player.debit(300);
        assertEquals(700, player.getBalance());
    }
    
    @Test
    public void testDebitWithNegativeAmount() {
        player.debit(-100);
        assertEquals("Balance should not change with negative debit", 
                    1000, player.getBalance());
    }
    
    @Test
    public void testPay() {
        player.pay(otherPlayer, 300);
        assertEquals(700, player.getBalance());
        assertEquals(1300, otherPlayer.getBalance());
    }
    
    @Test
    public void testBuyProperty() {
        player.buyProperty(property);
        assertEquals(800, player.getBalance());
        assertEquals(player, property.getOwner());
        assertTrue(player.getLiquidAssets().contains(property));
    }
    
    @Test
    public void testHasLiquidAssets() {
        assertFalse("New player should not have liquid assets", 
                   player.hasLiquidAssets());
        player.buyProperty(property);
        assertTrue("Player should have liquid assets after buying property", 
                  player.hasLiquidAssets());
    }
    
    @Test
    public void testGetLiquidAssets() {
        player.buyProperty(property);
        List<Property> assets = player.getLiquidAssets();
        assertTrue(assets.contains(property));
        assertEquals(1, assets.size());
    }
    
    @Test
    public void testLiquidate() {
        Bank bank = new Bank(1000, new ArrayList<>());
        player.buyProperty(property);
        player.liquidate(property, bank);
        assertEquals(900, player.getBalance()); // Initial 1000 - 200 + 100 (half price)
        assertNull(property.getOwner());
        assertFalse(player.getLiquidAssets().contains(property));
        assertTrue(bank.isPropertyUnowned(property));
    }
    
    @Test
    public void testDeclareBankruptcy() {
        Bank bank = new Bank(1000, new ArrayList<>());
        player.buyProperty(property);
        player.declareBankruptcy(bank);
        
        assertEquals(0, player.getBalance());
        assertNull(property.getOwner());
        assertFalse(player.hasLiquidAssets());
        assertTrue(bank.isPropertyUnowned(property));
    }
}
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
    
    // ========== TESTES PARA LÓGICA DE PRISÃO ==========
    
    @Test
    public void testIsInPrisonInitially() {
        assertFalse("Player should not be in prison initially", player.isInPrison());
    }
    
    @Test
    public void testGetTurnsInPrisonInitially() {
        assertEquals("Player should have 0 turns in prison initially", 0, player.getTurnsInPrison());
    }
    
    @Test
    public void testSendToPrison() {
        player.sendToPrison();
        
        assertTrue("Player should be in prison after sendToPrison()", player.isInPrison());
        assertEquals("Player should have 0 turns in prison after being sent", 0, player.getTurnsInPrison());
        assertTrue("Player's car should be marked as in prison", player.getCar().isInPrison());
    }
    
    @Test
    public void testIncrementTurnsInPrison() {
        player.sendToPrison();
        
        player.incrementTurnsInPrison();
        assertEquals("Player should have 1 turn in prison", 1, player.getTurnsInPrison());
        
        player.incrementTurnsInPrison();
        assertEquals("Player should have 2 turns in prison", 2, player.getTurnsInPrison());
    }
    
    @Test
    public void testIncrementTurnsInPrisonWhenNotInPrison() {
        // Player not in prison
        player.incrementTurnsInPrison();
        assertEquals("Turns should not increment when not in prison", 0, player.getTurnsInPrison());
    }
    
    @Test
    public void testReleaseFromPrison() {
        player.sendToPrison();
        player.incrementTurnsInPrison();
        player.incrementTurnsInPrison();
        
        player.releaseFromPrison();
        
        assertFalse("Player should not be in prison after release", player.isInPrison());
        assertEquals("Player should have 0 turns in prison after release", 0, player.getTurnsInPrison());
        assertFalse("Player's car should not be marked as in prison", player.getCar().isInPrison());
    }
    
    @Test
    public void testHasGetOutPrisonCardInitially() {
        assertFalse("Player should not have get out prison card initially", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testReceiveGetOutPrisonCard() {
        GetOutPrisonCard card = new GetOutPrisonCard("Test story", player);
        
        player.receiveGetOutPrisonCard(card);
        
        assertTrue("Player should have get out prison card after receiving", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseGetOutPrisonCard() {
        GetOutPrisonCard card = new GetOutPrisonCard("Test story", player);
        player.receiveGetOutPrisonCard(card);
        
        GetOutPrisonCard usedCard = player.useGetOutPrisonCard();
        
        assertEquals("Should return the same card that was received", card, usedCard);
        assertFalse("Player should not have get out prison card after using", player.hasGetOutPrisonCard());
    }
    
    @Test
    public void testUseGetOutPrisonCardWhenNotHaving() {
        GetOutPrisonCard usedCard = player.useGetOutPrisonCard();
        
        assertNull("Should return null when player doesn't have card", usedCard);
    }
    
    @Test
    public void testCanTryDoubleDiceWhenNotInPrison() {
        assertFalse("Player should not be able to try double dice when not in prison", 
                   player.canTryDoubleDice());
    }
    
    @Test
    public void testCanTryDoubleDiceWhenInPrison() {
        player.sendToPrison();
        
        assertTrue("Player should be able to try double dice when in prison", 
                  player.canTryDoubleDice());
    }
    
    @Test
    public void testGetConsecutiveDoublesInitially() {
        assertEquals("Player should have 0 consecutive doubles initially", 0, player.getConsecutiveDoubles());
    }
    
    @Test
    public void testProcessDiceRollWithDoubles() {
        // First double
        boolean shouldGoToPrison = player.processDiceRoll(3, 3);
        assertFalse("Should not go to prison after first double", shouldGoToPrison);
        assertEquals("Should have 1 consecutive double", 1, player.getConsecutiveDoubles());
        
        // Second double
        shouldGoToPrison = player.processDiceRoll(4, 4);
        assertFalse("Should not go to prison after second double", shouldGoToPrison);
        assertEquals("Should have 2 consecutive doubles", 2, player.getConsecutiveDoubles());
        
        // Third double - should go to prison
        shouldGoToPrison = player.processDiceRoll(5, 5);
        assertTrue("Should go to prison after third consecutive double", shouldGoToPrison);
        assertTrue("Player should be in prison", player.isInPrison());
        assertEquals("Consecutive doubles should reset to 0", 0, player.getConsecutiveDoubles());
    }
    
    @Test
    public void testProcessDiceRollWithNonDoubles() {
        // First double
        player.processDiceRoll(3, 3);
        assertEquals("Should have 1 consecutive double", 1, player.getConsecutiveDoubles());
        
        // Non-double - should reset counter
        boolean shouldGoToPrison = player.processDiceRoll(3, 4);
        assertFalse("Should not go to prison", shouldGoToPrison);
        assertEquals("Consecutive doubles should reset to 0", 0, player.getConsecutiveDoubles());
    }
    
    @Test
    public void testProcessDiceRollWithMixedResults() {
        // Double, non-double, double, double, double
        player.processDiceRoll(1, 1); // 1 consecutive
        player.processDiceRoll(2, 3); // reset to 0
        player.processDiceRoll(4, 4); // 1 consecutive
        player.processDiceRoll(5, 5); // 2 consecutive
        boolean shouldGoToPrison = player.processDiceRoll(6, 6); // 3 consecutive - should go to prison
        
        assertTrue("Should go to prison after third consecutive double", shouldGoToPrison);
        assertTrue("Player should be in prison", player.isInPrison());
    }
    
    @Test
    public void testResetConsecutiveDoubles() {
        player.processDiceRoll(3, 3);
        player.processDiceRoll(4, 4);
        assertEquals("Should have 2 consecutive doubles", 2, player.getConsecutiveDoubles());
        
        player.resetConsecutiveDoubles();
        assertEquals("Should have 0 consecutive doubles after reset", 0, player.getConsecutiveDoubles());
    }
    
    @Test
    public void testEquals() {
        Player samePlayer = new Player("Test Player", "Green", 500);
        Player differentPlayer = new Player("Different Player", "Blue", 1000);
        
        assertTrue("Players with same name should be equal", player.equals(samePlayer));
        assertFalse("Players with different names should not be equal", player.equals(differentPlayer));
        assertFalse("Player should not be equal to null", player.equals(null));
        assertFalse("Player should not be equal to different object type", player.equals("Test Player"));
    }
    
    @Test
    public void testHashCode() {
        Player samePlayer = new Player("Test Player", "Green", 500);
        Player differentPlayer = new Player("Different Player", "Blue", 1000);
        
        assertEquals("Players with same name should have same hash code", 
                    player.hashCode(), samePlayer.hashCode());
        assertNotEquals("Players with different names should have different hash codes", 
                       player.hashCode(), differentPlayer.hashCode());
    }
}
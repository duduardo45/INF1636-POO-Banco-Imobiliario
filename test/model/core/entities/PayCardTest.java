package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;



public class PayCardTest {
    
    private PayCard payCard;
    private Player player;
    private Car car;
    private Space startSpace;
    
    @Before
    public void setUp() {
        startSpace = new Start("Start", null);
        car = new Car("Red", startSpace);
        player = new Player("TestPlayer", "Red", car, 1000);
        payCard = new PayCard(150, "Pay traffic fine");
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Pay traffic fine", payCard.getStory());
        assertEquals(LuckType.MISFORTUNE, payCard.getType());
        assertEquals(150, payCard.getValue());
    }
    
    @Test
    public void testUse() {
        int initialBalance = player.getBalance();
        boolean result = payCard.use(player);
        
        assertTrue(result);
        assertEquals(initialBalance - 150, player.getBalance());
    }
    
    @Test
    public void testUseWithZeroValue() {
        PayCard zeroCard = new PayCard(0, "Nothing happens");
        int initialBalance = player.getBalance();
        boolean result = zeroCard.use(player);
        
        assertTrue(result);
        assertEquals(initialBalance, player.getBalance()); // No change
    }
    
    @Test
    public void testUseWithNegativeValue() {
        PayCard negativeCard = new PayCard(-100, "Negative test");
        int initialBalance = player.getBalance();
        boolean result = negativeCard.use(player);
        
        assertTrue(result);
        assertEquals(initialBalance, player.getBalance()); // No change due to debit validation
    }
    
    @Test
    public void testOnDraw() {
        int initialBalance = player.getBalance();
        boolean result = payCard.onDraw(player);
        
        assertTrue(result);
        assertEquals(initialBalance - 150, player.getBalance());
    }
    
    @Test
    public void testUseCanMakeBalanceNegative() {
        Player poorPlayer = new Player("PoorPlayer", "Blue", car, 50);
        boolean result = payCard.use(poorPlayer);
        
        assertTrue(result);
        assertEquals(-100, poorPlayer.getBalance()); // Balance can go negative
    }
    
    @Test
    public void testMultipleUses() {
        int initialBalance = player.getBalance();
        
        payCard.use(player);
        payCard.use(player);
        payCard.use(player);
        
        assertEquals(initialBalance - 450, player.getBalance());
    }
}
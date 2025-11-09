package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;



public class ReceiveCardTest {
    
    private ReceiveCard receiveCard;
    private Player player;
    private Car car;
    private Space startSpace;
    
    @Before
    public void setUp() {
        startSpace = new Start("Start", null);
        car = new Car("Red", startSpace);
        player = new Player("TestPlayer", "Red", car, 1000);
        receiveCard = new ReceiveCard("You won the lottery!", 200);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("You won the lottery!", receiveCard.getStory());
        assertEquals(LuckType.LUCKY, receiveCard.getType());
        assertEquals(200, receiveCard.getValue());
    }
    
    @Test
    public void testUse() {
        int initialBalance = player.getBalance();
        boolean result = receiveCard.use(player);
        
        assertTrue(result);
        assertEquals(initialBalance + 200, player.getBalance());
    }
    
    @Test
    public void testUseWithZeroValue() {
        ReceiveCard zeroCard = new ReceiveCard("Nothing happens", 0);
        int initialBalance = player.getBalance();
        boolean result = zeroCard.use(player);
        
        assertTrue(result);
        assertEquals(initialBalance, player.getBalance()); // No change
    }
    
    @Test
    public void testUseWithNegativeValue() {
        ReceiveCard negativeCard = new ReceiveCard("Negative test", -100);
        int initialBalance = player.getBalance();
        boolean result = negativeCard.use(player);
        
        assertTrue(result);
        assertEquals(initialBalance, player.getBalance()); // No change due to credit validation
    }
    
    @Test
    public void testOnDraw() {
        int initialBalance = player.getBalance();
        boolean result = receiveCard.onDraw(player);
        
        assertTrue(result);
        assertEquals(initialBalance + 200, player.getBalance());
    }
    
    @Test
    public void testMultipleUses() {
        int initialBalance = player.getBalance();
        
        receiveCard.use(player);
        receiveCard.use(player);
        receiveCard.use(player);
        
        assertEquals(initialBalance + 600, player.getBalance());
    }
}
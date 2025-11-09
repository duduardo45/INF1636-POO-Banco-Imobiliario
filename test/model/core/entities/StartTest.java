package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;



public class StartTest {
    
    private Start startSpace;
    private Start customStartSpace;
    private Player player;
    private Car car;
    private Space nextSpace;
    
    @Before
    public void setUp() {
        nextSpace = new Prison("Prison", null);
        startSpace = new Start("GO", nextSpace);
        customStartSpace = new Start("Custom Start", nextSpace, 300);
        
        car = new Car("Red", startSpace);
        player = new Player("TestPlayer", "Red", car, 1000);
    }
    
    @Test
    public void testDefaultConstructor() {
        assertEquals("GO", startSpace.getName());
        assertEquals(nextSpace, startSpace.getNext());
        assertEquals(200, startSpace.getPassBonus()); // Default bonus
    }
    
    @Test
    public void testCustomConstructor() {
        assertEquals("Custom Start", customStartSpace.getName());
        assertEquals(nextSpace, customStartSpace.getNext());
        assertEquals(300, customStartSpace.getPassBonus()); // Custom bonus
    }
    
    @Test
    public void testEvent() {
        int initialBalance = player.getBalance();
        
        startSpace.event(player);
        
        assertEquals(initialBalance + 200, player.getBalance());
    }
    
    @Test
    public void testEventWithCustomBonus() {
        int initialBalance = player.getBalance();
        
        customStartSpace.event(player);
        
        assertEquals(initialBalance + 300, player.getBalance());
    }
    
    @Test
    public void testEventMultipleTimes() {
        int initialBalance = player.getBalance();
        
        startSpace.event(player);
        startSpace.event(player);
        startSpace.event(player);
        
        assertEquals(initialBalance + 600, player.getBalance()); // 3 * 200
    }
    
    @Test
    public void testEventWithZeroBonus() {
        Start zeroBonusStart = new Start("Zero Start", null, 0);
        int initialBalance = player.getBalance();
        
        zeroBonusStart.event(player);
        
        assertEquals(initialBalance, player.getBalance()); // No change
    }
    
    @Test
    public void testEventWithNegativeBonus() {
        Start negativeBonusStart = new Start("Negative Start", null, -100);
        int initialBalance = player.getBalance();
        
        negativeBonusStart.event(player);
        
        assertEquals(initialBalance, player.getBalance()); // No change due to credit validation
    }
    
    @Test
    public void testGetPassBonus() {
        assertEquals(200, startSpace.getPassBonus());
        assertEquals(300, customStartSpace.getPassBonus());
        
        Start zeroStart = new Start("Zero", null, 0);
        assertEquals(0, zeroStart.getPassBonus());
        
        Start negativeStart = new Start("Negative", null, -50);
        assertEquals(-50, negativeStart.getPassBonus());
    }
    
    @Test
    public void testInheritanceFromSpace() {
        // Test that Start properly inherits from Space
        assertTrue(startSpace instanceof Space);
        
        // Test Space methods
        assertEquals("GO", startSpace.getName());
        assertEquals(nextSpace, startSpace.getNext());
        
        // Test setNext
        Space newNext = new Company("Test Company", null, 100, 10);
        startSpace.setNext(newNext);
        assertEquals(newNext, startSpace.getNext());
    }
    
    @Test
    public void testEventWithDifferentPlayers() {
        Player player2 = new Player("Player2", "Blue", car, 500);
        Player player3 = new Player("Player3", "Green", car, 2000);
        
        int balance1 = player.getBalance();
        int balance2 = player2.getBalance();
        int balance3 = player3.getBalance();
        
        startSpace.event(player);
        startSpace.event(player2);
        startSpace.event(player3);
        
        assertEquals(balance1 + 200, player.getBalance());
        assertEquals(balance2 + 200, player2.getBalance());
        assertEquals(balance3 + 200, player3.getBalance());
    }
    
    @Test
    public void testEventWithPoorPlayer() {
        Player poorPlayer = new Player("Poor", "Yellow", car, 50);
        int initialBalance = poorPlayer.getBalance();
        
        startSpace.event(poorPlayer);
        
        assertEquals(initialBalance + 200, poorPlayer.getBalance());
        assertEquals(250, poorPlayer.getBalance());
    }
    
    @Test
    public void testEventWithRichPlayer() {
        Player richPlayer = new Player("Rich", "Gold", car, 10000);
        int initialBalance = richPlayer.getBalance();
        
        startSpace.event(richPlayer);
        
        assertEquals(initialBalance + 200, richPlayer.getBalance());
        assertEquals(10200, richPlayer.getBalance());
    }
    
    @Test
    public void testStartSpaceChaining() {
        // Test that multiple start spaces can be chained
        Start start1 = new Start("Start1", null, 100);
        Start start2 = new Start("Start2", null, 150);
        Start start3 = new Start("Start3", null, 200);
        
        start1.setNext(start2);
        start2.setNext(start3);
        start3.setNext(start1); // Circular
        
        assertEquals(start2, start1.getNext());
        assertEquals(start3, start2.getNext());
        assertEquals(start1, start3.getNext());
    }
    
    @Test
    public void testLargePassBonus() {
        Start largeStart = new Start("Large", null, 1000000);
        int initialBalance = player.getBalance();
        
        largeStart.event(player);
        
        assertEquals(initialBalance + 1000000, player.getBalance());
    }
}
package model.core.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


import java.util.List;

public class BoardTest {
    
    private Board board;
    
    @Before
    public void setUp() {
        board = new Board();
    }
    
    @Test
    public void testConstructorInitializesBoard() {
        assertNotNull(board);
        assertEquals(5, board.getBoardSize()); // Should have 5 spaces as per initialization
        assertNotNull(board.getStartSpace());
        assertNotNull(board.getPrisonSpace());
    }
    
    @Test
    public void testGetStartSpace() {
        Space startSpace = board.getStartSpace();
        
        assertNotNull(startSpace);
        assertTrue(startSpace instanceof Start);
        assertEquals("Início", startSpace.getName());
    }
    
    @Test
    public void testGetPrisonSpace() {
        Prison prisonSpace = board.getPrisonSpace();
        
        assertNotNull(prisonSpace);
        assertTrue(prisonSpace instanceof Prison);
        assertEquals("Prisão", prisonSpace.getName());
    }
    
    @Test
    public void testGetSpace() {
        // Test valid positions
        Space space0 = board.getSpace(0);
        assertNotNull(space0);
        assertTrue(space0 instanceof Start);
        
        Space space1 = board.getSpace(1);
        assertNotNull(space1);
        assertTrue(space1 instanceof Place);
        assertEquals("Copacabana", space1.getName());
        
        Space space2 = board.getSpace(2);
        assertNotNull(space2);
        assertTrue(space2 instanceof Company);
        assertEquals("Companhia de Luz", space2.getName());
        
        // Test invalid positions
        assertNull(board.getSpace(-1));
        assertNull(board.getSpace(10));
        assertNull(board.getSpace(board.getBoardSize()));
    }
    
    @Test
    public void testGetPosition() {
        Space startSpace = board.getStartSpace();
        assertEquals(0, board.getPosition(startSpace));
        
        Space space1 = board.getSpace(1);
        assertEquals(1, board.getPosition(space1));
        
        Space space2 = board.getSpace(2);
        assertEquals(2, board.getPosition(space2));
        
        // Test with space not on board
        Space externalSpace = new Start("External", null);
        assertEquals(-1, board.getPosition(externalSpace));
        
        // Test with null
        assertEquals(-1, board.getPosition(null));
    }
    
    @Test
    public void testGetAllSpaces() {
        List<Space> spaces = board.getAllSpaces();
        
        assertNotNull(spaces);
        assertEquals(5, spaces.size());
        
        // Verify it's a defensive copy
        int originalSize = spaces.size();
        spaces.clear();
        assertEquals(5, board.getBoardSize()); // Original should be unchanged
        assertEquals(originalSize, board.getAllSpaces().size());
    }
    
    @Test
    public void testBoardCircularStructure() {
        // Test that spaces are connected in a circular pattern
        Space start = board.getStartSpace();
        Space current = start;
        
        // Traverse the entire board
        for (int i = 0; i < board.getBoardSize(); i++) {
            assertNotNull(current);
            current = current.getNext();
        }
        
        // Should be back to start
        assertEquals(start, current);
    }
    
    @Test
    public void testBoardContainsExpectedSpaces() {
        List<Space> spaces = board.getAllSpaces();
        
        boolean hasStart = false;
        boolean hasPlace = false;
        boolean hasCompany = false;
        boolean hasPrison = false;
        boolean hasGoToPrison = false;
        
        for (Space space : spaces) {
            if (space instanceof Start) hasStart = true;
            if (space instanceof Place) hasPlace = true;
            if (space instanceof Company) hasCompany = true;
            if (space instanceof Prison) hasPrison = true;
            if (space instanceof GoToPrison) hasGoToPrison = true;
        }
        
        assertTrue("Board should contain Start space", hasStart);
        assertTrue("Board should contain Place space", hasPlace);
        assertTrue("Board should contain Company space", hasCompany);
        assertTrue("Board should contain Prison space", hasPrison);
        assertTrue("Board should contain GoToPrison space", hasGoToPrison);
    }
    
    @Test
    public void testBoardSpaceNames() {
        assertEquals("Início", board.getSpace(0).getName());
        assertEquals("Copacabana", board.getSpace(1).getName());
        assertEquals("Companhia de Luz", board.getSpace(2).getName());
        assertEquals("Prisão", board.getSpace(3).getName());
        assertEquals("Vá para Prisão", board.getSpace(4).getName());
    }
    
    @Test
    public void testBoardSpaceProperties() {
        // Test Place properties
        Space placeSpace = board.getSpace(1);
        assertTrue(placeSpace instanceof Place);
        Place place = (Place) placeSpace;
        assertEquals(100, place.getCost());
        
        // Test Company properties
        Space companySpace = board.getSpace(2);
        assertTrue(companySpace instanceof Company);
        Company company = (Company) companySpace;
        assertEquals(150, company.getCost());
        assertEquals(25, company.getBaseRent());
    }
    
    @Test
    public void testBoardIntegrationWithSpaces() {
        // Test that spaces can trigger events
        Space startSpace = board.getStartSpace();
        Car car = new Car("Red", startSpace);
        Player player = new Player("TestPlayer", "Red", car, 1000);
        
        int initialBalance = player.getBalance();
        startSpace.event(player);
        
        // Start space should give bonus
        assertTrue(player.getBalance() > initialBalance);
    }
    
    @Test
    public void testBoardConsistency() {
        // Test that the board maintains consistency
        assertEquals(board.getBoardSize(), board.getAllSpaces().size());
        
        // Test that all positions are valid
        for (int i = 0; i < board.getBoardSize(); i++) {
            Space space = board.getSpace(i);
            assertNotNull(space);
            assertEquals(i, board.getPosition(space));
        }
    }
    
    @Test
    public void testEmptyBoardScenario() {
        // This tests the defensive programming in the methods
        // Since we can't create an actually empty board due to initialization,
        // we test the null-safety of the methods
        
        assertNotNull(board.getStartSpace());
        assertNotNull(board.getPrisonSpace());
        assertTrue(board.getBoardSize() > 0);
        assertFalse(board.getAllSpaces().isEmpty());
    }
}
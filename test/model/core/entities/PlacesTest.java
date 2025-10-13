package model.core.entities.spaces;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class PlaceTest {

    private static final int DEFAULT_TIMEOUT = 2000;// Defined since our teacher Ivan said it's a good practice

    private Place leblon;

    //Must be done before each test so the tests can modify the state of the object
    @Before
    public void setUp() {
        Map<Integer, Integer> leblonHouseRents = new HashMap<>();
        leblonHouseRents.put(1, 30);
        leblonHouseRents.put(2, 90);
        leblonHouseRents.put(3, 270);
        leblonHouseRents.put(4, 400);
        Space next = null; // Placeholder

        leblon = new Place("leblon", 100, next, 6, 50, 50, 100, leblonHouseRents);
    }

    //Tests for "get" functions

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetHousePrice() {
        assertEquals("House price should be 50", 50, leblon.getHousePrice());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetHotelPrice() {
        assertEquals("Hotel price should be 50", 50, leblon.getHotelPrice());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetCurrentRent() {
        assertEquals("Current rent should be initial base rent", 6, leblon.getCurrentRent());
    }

    //Tests for buildHouse, buildHotel, getNumOfHouses, getNumOfHotels and, by consequence and indirectly, updateCurrentRent (which is private)

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetNumOfHouses_initiallyZero() {
        assertEquals("Initially, there should be zero houses", 0, leblon.getNumOfHouses());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGetNumOfHotels_initiallyZero() {
        assertEquals("Initially, there should be zero hotels", 0, leblon.getNumOfHotels());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildHouse_getCurrentRent_and_getNumOfHouses_singleHouse() {
        leblon.buildHouse();

        assertEquals("Counts one house", 1, leblon.getNumOfHouses());
        assertEquals("Asserts that the rent is updated to the value of 1 house", 30, leblon.getCurrentRent());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildHouse_getCurrentRent_and_getNumOfHouses_multipleHouses() {
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();

        assertEquals("Counts three houses", 4, leblon.getNumOfHouses());
        assertEquals("Asserts that the rent is updated to the value of 4 houses", 400, leblon.getCurrentRent());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildHotel_getCurrentRent_and_getNumOfHotels() {
        leblon.buildHouse();
        leblon.buildHotel();
        assertEquals("Counts one hotel", 1, leblon.getNumOfHotels());
        assertEquals("Asserts that the rent is updated to the value of 1 hotel and 1 house", 130, leblon.getCurrentRent());

    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildHotel_getCurrentRent_and_getNumOfHotels_multipleHouses() {
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHotel();
        assertEquals("Counts one hotel", 1, leblon.getNumOfHotels());
        assertEquals("Asserts that the rent is updated to the value of 1 hotel and 2 houses", 190, leblon.getCurrentRent());
    }

    @Test(expected = IllegalStateException.class, timeout = DEFAULT_TIMEOUT)    
    public void testBuildHotel_getCurrentRent_and_getNumOfHotels_withoutHouses() {
        leblon.buildHotel();
    }

    @Test(expected = IllegalStateException.class, timeout = DEFAULT_TIMEOUT)   
    public void testBuildHouse_beyondLimit() {
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();
    }

    //Tests for canBuildHouse and canBuildHotel

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testCanBuildHouse_WhenPropertyIsEmpty() {
        assertTrue("The property is empty, so a house can be built", leblon.canBuildHouse());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testCanBuildHouse_WithFourHouses() {
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();
        leblon.buildHouse();

        assertFalse("The property has 4 houses, so a fifth house cannot be built", leblon.canBuildHouse());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testCanBuildHotel_NoHouses(){
        assertFalse("The property has no houses, so a hotel cannot be built", leblon.canBuildHotel());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testCanBuildHotel_OneHouse(){
        leblon.buildHouse();
        assertTrue("The property has one house, so a hotel can be built", leblon.canBuildHotel());
    }
    
}
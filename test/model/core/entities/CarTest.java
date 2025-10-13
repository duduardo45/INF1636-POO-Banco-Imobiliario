package model.core.entities;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CarTest {

    private static final int DEFAULT_TIMEOUT = 2000;// Defined since our teacher Ivan said it's a good practice
    private Car testCar;
    private Space startSpace;
    private Space secondSpace;
    private Space thirdSpace;

    @Before
    public void setUp() {
    	thirdSpace = new Company("Third", null, 10, 10);
    	secondSpace = new Company("Second", thirdSpace, 20, 20);
        startSpace = new Company("Start", secondSpace, 30, 30);
        // Linking spaces in a circular manner for testing
        thirdSpace.setNext(startSpace);
        
        testCar = new Car("testColor", startSpace);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testAdvancePosition_oneStep_and_GetPosition() {
        testCar.advancePosition(1);
        assertEquals("Car should be on the second space", secondSpace, testCar.getPosition());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testAdvancePosition_multipleSteps_and_GetPosition() {
        testCar.advancePosition(2);
        assertEquals("Car should be on the third space", thirdSpace, testCar.getPosition());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testAdvancePosition_wrapAround_and_GetPosition() {
        testCar.advancePosition(3);
        assertEquals("Car should wrap around to the start space", startSpace, testCar.getPosition());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testIsInPrison_initiallyFalse() {
        assertFalse("Car should not be in prison initially", testCar.isInPrison());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testSetInPrison_true() {
        testCar.setInPrison(true);
        assertTrue("Car should be in prison after setting to true", testCar.isInPrison());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testSetPosition_and_GetPosition() {
        testCar.setPosition(thirdSpace);
        assertEquals("Car should be on the third space after setting position", thirdSpace, testCar.getPosition());
    }
}
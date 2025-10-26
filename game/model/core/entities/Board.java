package model.core.entities;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Board {
    private List<Space> spaces;
    // private List<Car> cars;
    
    public Board() {
        this.spaces = new ArrayList<>();
        // this.cars = new ArrayList<>();
        initializeBoard();
    }
    
    /**
     * Initializes the board with spaces in a circular pattern.
     */
    private void initializeBoard() {
        // Create spaces - this is a simplified version
        // In a real implementation, this would be loaded from configuration
        
        // Start space
        Start startSpace = new Start("Início", null);
        spaces.add(startSpace);
        
        // Add some example properties and special spaces
        Map<Integer, Integer> houseRentMap = new HashMap<>();
        houseRentMap.put(1, 10);
        houseRentMap.put(2, 30);
        houseRentMap.put(3, 90);
        houseRentMap.put(4, 160);
        spaces.add(new Place("Copacabana", 100, null, 50, 50, 200, 250, houseRentMap));
        spaces.add(new Company("Companhia de Luz", null, 150, 25));
        spaces.add(new Prison("Prisão", null));
        spaces.add(new GoToPrison("Vá para Prisão", null));
        
        // Connect spaces in a circular pattern
        for (int i = 0; i < spaces.size(); i++) {
            Space current = spaces.get(i);
            Space next = spaces.get((i + 1) % spaces.size());
            current.setNext(next);
        }
    }
    
    /**
     * Returns the space at the given position.
     * 
     * @param position The position on the board (0-based).
     * @return The space at that position.
     */
    public Space getSpace(int position) {
        if (position >= 0 && position < spaces.size()) {
            return spaces.get(position);
        }
        return null;
    }
    
    /**
     * Returns the position of a given space.
     * 
     * @param space The space to find.
     * @return The position of the space, or -1 if not found.
     */
    public int getPosition(Space space) {
        return spaces.indexOf(space);
    }
    
    // /**
    //  * Moves a car by the specified number of spaces.
    //  * 
    //  * @param car The car to move.
    //  * @param steps The number of steps to move.
    //  * @return The new space where the car landed.
    //  */
    // public Space moveCar(Car car, int steps) {
    //     int currentPosition = getPosition(car.getPosition());
    //     if (currentPosition == -1) {
    //         return null; // Car not found on board
    //     }
        
    //     int newPosition = (currentPosition + steps) % spaces.size();
    //     Space newSpace = spaces.get(newPosition);
    //     car.setPosition(newSpace);
        
    //     return newSpace;
    // }
    
    // /**
    //  * Adds a car to the board at the start position.
    //  * 
    //  * @param car The car to add.
    //  */
    // public void addCar(Car car) {
    //     if (!cars.contains(car)) {
    //         cars.add(car);
    //         // Place car at start position
    //         if (!spaces.isEmpty()) {
    //             car.setPosition(spaces.get(0));
    //         }
    //     }
    // }
    
    // /**
    //  * Removes a car from the board.
    //  * 
    //  * @param car The car to remove.
    //  */
    // public void removeCar(Car car) {
    //     cars.remove(car);
    // }
    
    /**
     * Returns the start space of the board.
     * 
     * @return The start space.
     */
    public Space getStartSpace() {
        return spaces.isEmpty() ? null : spaces.get(0);
    }
    
    /**
     * Finds the prison space on the board.
     * 
     * @return The prison space, or null if not found.
     */
    public Prison getPrisonSpace() {
        for (Space space : spaces) {
            if (space instanceof Prison) {
                return (Prison) space;
            }
        }
        return null;
    }
    
    /**
     * Returns the total number of spaces on the board.
     * 
     * @return The board size.
     */
    public int getBoardSize() {
        return spaces.size();
    }
    
    /**
     * Returns a copy of all spaces on the board.
     * 
     * @return A new list containing all spaces.
     */
    public List<Space> getAllSpaces() {
        return new ArrayList<>(spaces);
    }
    
    // /**
    //  * Returns a copy of all cars on the board.
    //  * 
    //  * @return A new list containing all cars.
    //  */
    // public List<Car> getAllCars() {
    //     return new ArrayList<>(cars);
    // }
}

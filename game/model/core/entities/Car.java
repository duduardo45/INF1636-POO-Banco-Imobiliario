package model.core.entities;

class Car {
    private Space position;
    private boolean in_prison;
    private String color;

    public Car(String color, Space startPosition) {
        this.color = color;
        this.position = startPosition;
        this.in_prison = false; // Initially, the car is not in prison
    }

    public void advancePosition(int steps) {
        // move the car forward by 'steps'(according to the dice roll) spaces
        for (int i = 0; i < steps; i++) {
            position = position.getNext();
        }
    }
    
    public boolean isInPrison() {
        // since all the atributes are private, we need a getter for this instead of accessing it directly
        return in_prison;
    }

    public void setInPrison(boolean status) {
        in_prison = status;
    }

    public Space getPosition() {
        return position;
    }  

    public void setPosition(Space newPosition) {
        //useful for the "Go to Prison" space, for example
        position = newPosition;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String newColor) {
        color = newColor;
    }
}

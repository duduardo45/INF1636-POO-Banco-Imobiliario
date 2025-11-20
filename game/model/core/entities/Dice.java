package model.core.entities;

import java.util.Random;

class Dice {
    public int roll() {
    	Random rand = new Random();
    	int min = 1;
    	int max = 6;
    	
        return rand.nextInt((max - min) + 1) + min;
    }
    public int rollFixed(int value) {
        if (value < 1) value = 1;
        if (value > 6) value = 6;
        
        return value;
    }
    
}

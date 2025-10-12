package model.core.entities;

import java.util.Random;

public class Dice {
    private int roll() {
    	Random rand = new Random();
    	int min = 1;
    	int max = 6;
    	
        return rand.nextInt((max - min) + 1) + min;
    }
}

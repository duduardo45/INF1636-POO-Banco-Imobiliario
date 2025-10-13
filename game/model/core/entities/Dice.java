package model.core.entities;

import java.util.Random;

public class Dice {
    private Random rand;
    private int lastDice1;
    private int lastDice2;
    
    public Dice() {
        this.rand = new Random();
        this.lastDice1 = 0;
        this.lastDice2 = 0;
    }
    
    public Dice(long seed) {
        this.rand = new Random(seed);
        this.lastDice1 = 0;
        this.lastDice2 = 0;
    }
    
    public int roll() {
    	int min = 1;
    	int max = 6;
        return rand.nextInt((max - min) + 1) + min;
    }
    
    /**
     * Rola dois dados e retorna os valores.
     * @return array com dois valores [dado1, dado2]
     */
    public int[] rollTwo() {
        lastDice1 = roll();
        lastDice2 = roll();
        return new int[]{lastDice1, lastDice2};
    }
    
    /**
     * Retorna o valor do primeiro dado do último lançamento.
     * @return valor do primeiro dado
     */
    public int getLastDice1() {
        return lastDice1;
    }
    
    /**
     * Retorna o valor do segundo dado do último lançamento.
     * @return valor do segundo dado
     */
    public int getLastDice2() {
        return lastDice2;
    }
    
    /**
     * Retorna a soma dos dois dados do último lançamento.
     * @return soma dos dados
     */
    public int getLastDiceSum() {
        return lastDice1 + lastDice2;
    }
    
    /**
     * Verifica se dois valores de dados são iguais (doubles).
     * @param d1 valor do primeiro dado
     * @param d2 valor do segundo dado
     * @return true se os valores são iguais, false caso contrário
     */
    public static boolean isDouble(int d1, int d2) {
        return d1 == d2;
    }
    
    /**
     * Rola dois dados e verifica se são doubles.
     * @return true se os dados são iguais, false caso contrário
     */
    public boolean rollDouble() {
        int[] result = rollTwo();
        return isDouble(result[0], result[1]);
    }
}

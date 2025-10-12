package model.core.entities;

import java.util.List;
import java.util.ArrayList;

import model.core.entities.cards.LuckCard;
import model.core.entities.spaces.Property;

public class Player {
    private final String name;
    private int balance;
    private final Car car;
    private final List<Property> ownedProperties;

    public Player(String name, String carColor, int initialBalance) {
        // A implementação do construtor iria aqui...
        this.name = name;
        this.balance = initialBalance;
        this.car = new Car(carColor);
        this.ownedProperties = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public Car getCar() {
        return this.car;
    }

    /**
     * Retorna o saldo monetário atual do jogador.
     * 
     * @return O valor do saldo.
     */
    public int getBalance() {
        return this.balance;
    }

    /**
     * Adiciona uma quantia ao saldo do jogador.
     * 
     * @param amount A quantia a ser creditada. Deve ser positiva.
     */
    public void credit(int amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    /**
     * Subtrai uma quantia do saldo do jogador.
     * Não verifica se o jogador tem saldo suficiente; essa lógica
     * geralmente é tratada por um serviço ou controlador de jogo.
     * 
     * @param amount A quantia a ser debitada. Deve ser positiva.
     */
    public void debit(int amount) {
        if (amount > 0) {
            this.balance -= amount;
        }
    }

    /**
     * Transfere uma quantia do jogador atual para um jogador recebedor.
     * 
     * @param receiver O jogador que receberá o dinheiro.
     * @param amount   A quantia a ser transferida.
     */
    public void pay(Player receiver, int amount) {
        this.debit(amount);
        receiver.credit(amount);
    }

    /**
     * Associa uma propriedade ao jogador e debita o valor de sua compra.
     * 
     * @param property A propriedade a ser comprada.
     */
    public void buyProperty(Property property) {
        this.debit(property.getCost());
        this.ownedProperties.add(property);
        property.setOwner(this);
    }

    public void sellProperty(Property property) {
    }

    /**
     * Verifica se o jogador possui propriedades que podem ser vendidas/hipotecadas.
     * 
     * @return true se o jogador possuir pelo menos uma propriedade.
     */
    public boolean hasLiquidAssets() {
        return !this.ownedProperties.isEmpty();
    }

    /**
     * Retorna a lista de propriedades do jogador.
     * 
     * @return Uma nova lista contendo as propriedades para evitar modificação
     *         externa.
     */
    public List<Property> getLiquidAssets() {
        return new ArrayList<>(this.ownedProperties);
    }

    /**
     * Vende uma propriedade de volta ao banco pela metade de seu preço de compra.
     * @param asset A propriedade a ser liquidada.
     */
    public void liquidate(Property asset, Bank bank) {
        if (this.ownedProperties.contains(asset)) {
            int sellPrice = asset.getPrice() / 2; // BACALHAU trocar para ser 90%
            this.credit(sellPrice);
            this.ownedProperties.remove(asset);
            asset.setOwner(null);
            bank.returnPropertyToBank(asset);
        }
    }

    /**
     * Zera o saldo e remove a posse de todas as propriedades do jogador.
     */
    public void declareBankruptcy(Bank bank) {
        this.balance = 0;
        // Transforma a lista em um stream para evitar ConcurrentModificationException
        // enquanto removemos a posse.
        new ArrayList<>(this.ownedProperties).forEach(prop -> {
            prop.setOwner(null);
            bank.returnPropertyToBank(prop);
        });
        this.ownedProperties.clear();
    }
}
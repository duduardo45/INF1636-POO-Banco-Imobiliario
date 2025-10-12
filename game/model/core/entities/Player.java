package game.model.core.entities;

import java.util.ArrayList;
import java.util.List;

import game.model.core.entities.cards.LuckCard;
import game.model.core.entities.spaces.Property;

public class Player {
    private final String name;
    private int balance;
    private final Car car;
    private final List<Property> ownedProperties;

    // Construtor e outros métodos existentes...
    public Player(String name, String carColor, int initialBalance) {
        this.name = name;
        this.balance = initialBalance;
        this.car = new Car(carColor);
        this.ownedProperties = new ArrayList<>();
    }

    public String getName() {}
    public Car getCar() {}


    /**
     * Retorna o saldo atual do jogador.
     * @return O valor do saldo.
     */
    public int getBalance() {
        return this.balance;
    }

    /**
     * Deduz um valor do saldo do jogador.
     * Assume que a verificação se o jogador pode pagar já foi feita.
     * @param amount O valor a ser debitado.
     */
    public void debit(int amount) {
        if (amount > 0) {
            this.balance -= amount;
        }
    }

    /**
     * Adiciona um valor ao saldo do jogador.
     * @param amount O valor a ser creditado.
     */
    public void credit(int amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    /**
     * Transfere uma quantia do jogador atual para outro jogador.
     * @param receiver O jogador que receberá o dinheiro.
     * @param amount O valor a ser pago.
     */
    public void pay(Player receiver, int amount) {
        this.debit(amount);
        receiver.credit(amount);
    }

    /**
     * Realiza a compra de uma propriedade.
     * Deduz o preço do saldo, adiciona a propriedade à lista do jogador
     * e define o jogador como o novo dono.
     * @param property A propriedade a ser comprada.
     */
    public void buyProperty(Property property) {
        this.debit(property.getPrice());
        this.ownedProperties.add(property);
        property.setOwner(this);
    }

    /**
     * Verifica se o jogador possui ativos (propriedades) que podem ser liquidados.
     * @return true se o jogador possuir pelo menos uma propriedade, false caso contrário.
     */
    public boolean hasLiquidAssets() {
        return !this.ownedProperties.isEmpty();
    }

    /**
     * Retorna uma lista de todas as propriedades do jogador.
     * @return Uma nova lista contendo as propriedades do jogador.
     */
    public List<Property> getLiquidAssets() {
        // Retorna uma cópia para evitar modificação externa da lista original.
        return new ArrayList<>(this.ownedProperties);
    }

    /**
     * Vende uma propriedade de volta para o banco (liquidação).
     * O jogador recebe metade do valor original, a propriedade é removida de sua posse
     * e o dono da propriedade é setado para null (representando o banco).
     * @param asset A propriedade a ser liquidada.
     */
    public void liquidate(Property asset) {
        if (this.ownedProperties.contains(asset)) {
            // Regra comum: vende pela metade do preço de compra.
            int salePrice = asset.getPrice() / 2;
            this.credit(salePrice);
            this.ownedProperties.remove(asset);
            asset.setOwner(null); 
        }
    }

    /**
     * Inicia o processo de falência do jogador.
     * Todas as propriedades retornam ao estado sem dono, a lista de propriedades
     * é esvaziada e o saldo é zerado. A lógica de transferir para um credor
     * seria gerenciada por uma classe de controle de jogo.
     */
    public void declareBankruptcy() {
        for (Property property : this.ownedProperties) {
            property.setOwner(null); // Retorna a propriedade ao "banco"
        }
        this.ownedProperties.clear();
        this.balance = 0;
        // A lógica de remover o jogador do jogo ficaria em uma classe de controle.
    }
    
    // Getters e outros métodos não implementados aqui...
}
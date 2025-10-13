package model.core.entities;

import java.util.List;
import java.util.ArrayList;

import model.core.entities.cards.LuckCard;
import model.core.entities.spaces.Property;
import java.util.List;

public class Player {
    private final String name;
    private int balance;
    private final Car car;
    private final List<Property> ownedProperties;
    private final List<LuckCard> cards;
    
    // Campos relacionados à prisão
    private int prisonTurns;
    private boolean hasGetOutOfPrisonCard;
    private int consecutiveDoubles;
    private PrisonReason prisonReason;

    public Player(String name, String carColor, int initialBalance) {
        // A implementação do construtor iria aqui...
        this.name = name;
        this.balance = initialBalance;
        this.car = new Car(carColor);
        this.ownedProperties = new ArrayList<>();
        this.cards = new ArrayList<>();
        
        // Inicializar campos de prisão
        this.prisonTurns = 0;
        this.hasGetOutOfPrisonCard = false;
        this.consecutiveDoubles = 0;
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
     * Define o saldo monetário do jogador.
     * 
     * @param balance O novo valor do saldo.
     */
    public void setBalance(int balance) {
        this.balance = balance;
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
     * @param receiver O jogador que receberá o dinheiro (pode ser null para pagar ao banco).
     * @param amount   A quantia a ser transferida.
     */
    public void pay(Player receiver, int amount) {
        this.debit(amount);
        if (receiver != null) {
            receiver.credit(amount);
        }
        // Se receiver for null, o dinheiro vai para o banco (não fazemos nada)
    }
    
    /**
     * Paga aluguel para o dono de uma propriedade.
     * 
     * @param property A propriedade onde o jogador está
     * @return O valor efetivamente pago
     */
    public int payRent(Property property) {
        if (property == null || !property.shouldChargeRent() || 
            property.getOwner() == this || property.getOwner() == null) {
            return 0;
        }
        
        int rentAmount = property.calculateRent();
        this.pay(property.getOwner(), rentAmount);
        return rentAmount;
    }

    /**
     * Associa uma propriedade ao jogador e debita o valor de sua compra.
     * 
     * @param property A propriedade a ser comprada.
     */
    public void buyProperty(Property property) {
        this.debit(property.getCost());
        this.ownedProperties.add(property);
        // Usa o hashCode do jogador como ID simples para models soltos
        property.setOwner(this, this.hashCode());
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
     * 
     * @param asset A propriedade a ser liquidada.
     */
    public void liquidate(Property asset) {
        if (this.ownedProperties.contains(asset)) {
            int sellPrice = asset.getCost() / 2; // BACALHAU trocar para ser 90%
            this.credit(sellPrice);
            this.ownedProperties.remove(asset);
            asset.removeOwner();
            // Um GameController seria responsável por chamar
            // bank.returnPropertyToBank(asset).
        }
    }

    /**
     * Zera o saldo e remove a posse de todas as propriedades do jogador.
     */
    public void declareBankruptcy() {
        this.balance = 0;
        // Transforma a lista em um stream para evitar ConcurrentModificationException
        // enquanto removemos a posse.
        new ArrayList<>(this.ownedProperties).forEach(prop -> {
            prop.removeOwner();
            // O GameController decidiria se a propriedade vai para o credor ou para o
            // banco.
        });
        this.ownedProperties.clear();
    }
    
    // Métodos relacionados à prisão
    
    public boolean isInPrison() {
        return this.car.isInPrison();
    }
    
    public int getPrisonTurns() {
        return prisonTurns;
    }
    
    public void setPrisonTurns(int prisonTurns) {
        this.prisonTurns = prisonTurns;
    }
    
    public boolean hasGetOutOfPrisonCard() {
        return hasGetOutOfPrisonCard;
    }
    
    public void setHasGetOutOfPrisonCard(boolean hasGetOutOfPrisonCard) {
        this.hasGetOutOfPrisonCard = hasGetOutOfPrisonCard;
    }
    
    public int getConsecutiveDoubles() {
        return consecutiveDoubles;
    }
    
    public void setConsecutiveDoubles(int consecutiveDoubles) {
        this.consecutiveDoubles = consecutiveDoubles;
    }
    
    public PrisonReason getPrisonReason() {
        return prisonReason;
    }
    
    /**
     * Coloca o jogador na prisão com um motivo específico.
     * @param reason O motivo de entrada na prisão
     */
    public void enterPrison(PrisonReason reason) {
        this.car.setInPrison(true);
        this.prisonReason = reason;
        this.prisonTurns = 0;
        this.consecutiveDoubles = 0; // Reset contador de duplas
    }
    
    /**
     * Remove o jogador da prisão.
     */
    public void leavePrison() {
        this.car.setInPrison(false);
        this.prisonReason = null;
        this.prisonTurns = 0;
    }
    
    /**
     * Incrementa o número de turnos na prisão.
     */
    public void incrementPrisonTurns() {
        this.prisonTurns++;
    }
    
    /**
     * Incrementa o contador de duplas consecutivas.
     */
    public void incrementConsecutiveDoubles() {
        this.consecutiveDoubles++;
    }
    
    /**
     * Adiciona uma carta à mão do jogador.
     * @param card A carta a ser adicionada
     */
    public void addCard(LuckCard card) {
        this.cards.add(card);
    }
    
    /**
     * Remove uma carta da mão do jogador.
     * @param card A carta a ser removida
     */
    public void removeCard(LuckCard card) {
        this.cards.remove(card);
    }
    
    /**
     * Verifica se o jogador tem uma carta específica.
     * @param card A carta a ser verificada
     * @return true se o jogador tem a carta
     */
    public boolean hasCard(LuckCard card) {
        return this.cards.contains(card);
    }
}
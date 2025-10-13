package model.core.entities;

import java.util.List;
import java.util.ArrayList;

class Player {
    private final String name;
    private int balance;
    private final Car car;
    private final List<Property> ownedProperties;
    private boolean inPrison;
    private int turnsInPrison;
    private GetOutPrisonCard getOutPrisonCard;
    private int consecutiveDoubles;

    public Player(String name, String carColor, Car ownCar, int initialBalance) {
        // A implementação do construtor iria aqui...
        this.name = name;
        this.balance = initialBalance;
        this.car = ownCar;
        this.ownedProperties = new ArrayList<>();
        this.inPrison = false;
        this.turnsInPrison = 0;
        this.getOutPrisonCard = null;
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
            int sellPrice = asset.getCost() / 2; // BACALHAU trocar para ser 90%
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

    /**
     * Verifica se o jogador está atualmente na prisão.
     * 
     * @return true se o jogador estiver na prisão, false caso contrário.
     */
    public boolean isInPrison() {
        return this.inPrison;
    }

    /**
     * Retorna o número de turnos que o jogador passou na prisão.
     * 
     * @return O número de turnos na prisão.
     */
    public int getTurnsInPrison() {
        return this.turnsInPrison;
    }

    /**
     * Envia o jogador para a prisão, definindo sua posição e estado.
     */
    public void sendToPrison() {
        this.inPrison = true;
        this.turnsInPrison = 0;
        this.consecutiveDoubles = 0;
        this.car.setInPrison(true);
        // A posição do carro será definida pelo tabuleiro para a casa da prisão
    }

    /**
     * Incrementa o contador de turnos na prisão.
     */
    public void incrementTurnsInPrison() {
        if (this.inPrison) {
            this.turnsInPrison++;
        }
    }

    /**
     * Liberta o jogador da prisão, resetando seu estado.
     */
    public void releaseFromPrison() {
        this.inPrison = false;
        this.turnsInPrison = 0;
        this.consecutiveDoubles = 0;
        this.car.setInPrison(false);
    }

    /**
     * Verifica se o jogador possui uma carta "Saia da Prisão".
     * 
     * @return true se o jogador tiver a carta, false caso contrário.
     */
    public boolean hasGetOutPrisonCard() {
        return this.getOutPrisonCard != null;
    }

    /**
     * Adiciona uma carta "Saia da Prisão" ao jogador.
     * 
     * @param card A carta a ser adicionada.
     */
    public void receiveGetOutPrisonCard(GetOutPrisonCard card) {
        this.getOutPrisonCard = card;
    }

    /**
     * Remove e retorna a carta "Saia da Prisão" do jogador.
     * 
     * @return A carta removida, ou null se o jogador não tiver uma.
     */
    public GetOutPrisonCard useGetOutPrisonCard() {
        GetOutPrisonCard card = this.getOutPrisonCard;
        this.getOutPrisonCard = null;
        return card;
    }


    /**
     * Verifica se o jogador pode tentar sair da prisão jogando os dados (duplo).
     * 
     * @return true se o jogador estiver na prisão, false caso contrário.
     */
    public boolean canTryDoubleDice() {
        return this.inPrison;
    }

    /**
     * Retorna o número de dados duplos consecutivos que o jogador rolou.
     * 
     * @return O número de duplos consecutivos.
     */
    public int getConsecutiveDoubles() {
        return this.consecutiveDoubles;
    }

    /**
     * Processa o resultado de uma jogada de dados, verificando se é duplo e se deve ir para prisão.
     * 
     * @param dice1 O valor do primeiro dado.
     * @param dice2 O valor do segundo dado.
     * @return true se o jogador deve ir para prisão (3 duplos consecutivos), false caso contrário.
     */
    public boolean processDiceRoll(int dice1, int dice2) {
        if (dice1 == dice2) {
            this.consecutiveDoubles++;
            // Se rolou 3 duplos consecutivos, deve ir para prisão
            if (this.consecutiveDoubles >= 3) {
                this.sendToPrison();
                this.consecutiveDoubles = 0; // Reset contador
                return true; // Deve ir para prisão
            }
        } else {
            // Se não é duplo, reseta o contador
            this.consecutiveDoubles = 0;
        }
        return false; // Não vai para prisão
    }

    /**
     * Reseta o contador de dados duplos consecutivos.
     */
    public void resetConsecutiveDoubles() {
        this.consecutiveDoubles = 0;
    }

    /**
     * Verifica se este jogador é igual a outro objeto.
     * 
     * @param obj O objeto a ser comparado.
     * @return true se os objetos são iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return name.equals(player.name);
    }

    /**
     * Retorna o hash code do jogador baseado no nome.
     * 
     * @return O hash code do jogador.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
// =======================================================================
// ENUMS E TIPOS AUXILIARES
// =======================================================================

/**
 * Define o tipo de uma construção em uma propriedade do tipo Place.
 */
private enum Building {
    HOUSE,
    HOTEL
}

/**
 * Define o tipo de uma carta de Sorte ou Revés.
 */
private enum LuckType {
    FORTUNE,  // Sorte
    MISFORTUNE // Revés
}

/**
 * Define as fases possíveis de um turno de jogador.
 */
private enum TurnPhase {
    PRE_ROLL,      // Antes de rolar os dados
    POST_ROLL,     // Depois de rolar, antes de executar a ação da casa
    ACTION_PHASE,  // Durante a interação com a casa (compra, pagamento)
    TURN_ENDED     // Turno finalizado
}

// =======================================================================
// PACOTE: game.model.core.entities
// =======================================================================

public class Bank {
    /** O dinheiro total que o banco possui. */
    private int treasury;
    /** Lista de propriedades que ainda não foram compradas por nenhum jogador. */
    private final List<Property> unownedProperties;

    public Bank(int initialTreasury, List<Property> allProperties) {}

    public void credit(int amount) {}
    public void debit(int amount) {}
    
    /** Verifica se uma propriedade específica pertence ao banco (não tem dono). */
    public boolean isPropertyUnowned(Property property) {}
    
    /** Remove uma propriedade da lista de não possuídas (quando um jogador compra). */
    public void markPropertyAsOwned(Property property) {}
    
    /** Adiciona uma propriedade de volta à lista de não possuídas (ex: falência para o banco). */
    public void returnPropertyToBank(Property property) {}
}

public class Board {
    /** Lista de todas as casas do tabuleiro em ordem. */
    private final List<Space> spaces;
    private final int prisonPosition;

    public Board(List<Space> spaces, int prisonPosition) {}

    public Space getSpaceAt(int position) {}
    public int getPrisonPosition() {}
    public int getTotalSpaces() {}
}

public class Car {
    /** A posição atual do peão no tabuleiro (índice na lista de casas). */
    private int currentPosition;
    /** Cor ou identificador único do peão. */
    private final String color;

    public Car(String color) {}

    public int getCurrentPosition() {}
    public void moveTo(int newPosition) {}
    /** Avança o peão, tratando o loop do tabuleiro. */
    public void advance(int steps, int boardSize) {}
}

public class Dice {
    private int lastRoll1;
    private int lastRoll2;

    public Dice() {}

    /** Rola os dois dados e armazena os resultados. */
    public void roll() {}
    public int getLastRoll1() {}
    public int getLastRoll2() {}
    public int getTotal() {}
    /** Verifica se a última rolagem resultou em valores iguais. */
    public boolean wasDouble() {}
}

public class LuckDeck {
    /** A pilha de cartas para serem sorteadas. */
    private Deque<LuckCard> cards;

    public LuckDeck(List<LuckCard> cards) {}

    public void shuffle() {}
    public LuckCard drawCard() {}
    public void returnCard(LuckCard card) {}
}

public class Player {
    private final String name;
    private int balance;
    private final Car car;
    private final List<Property> ownedProperties;
    private final List<GetOutPrisonCard> jailCards;
    private boolean isInJail;
    private int turnsInJail;
    private int consecutiveDoubles;

    public Player(String name, String carColor, int initialBalance) {}

    public String getName() {}
    public int getBalance() {}
    public Car getCar() {}
    public boolean isInJail() {}
    public int getTurnsInJail() {}

    public void credit(int amount) {}
    public void debit(int amount) {}
    public void pay(Player receiver, int amount) {}

    public void buyProperty(Property property) {}
    public void sellProperty(Property property) {}

    public void goToJail() {}
    public void leaveJail() {}
    public void incrementTurnsInJail() {}
    
    public void addGetOutOfJailCard(GetOutPrisonCard card) {}
    public GetOutPrisonCard useGetOutOfJailCard() {}
    public boolean hasGetOutOfJailCard() {}

    public boolean hasLiquidAssets() {}
    public List<Property> getLiquidAssets() {}
    public void liquidate(Property asset) {}
    public void declareBankruptcy() {}
}

public class Turn {
    private final List<Player> playerOrder;
    private int currentPlayerIndex;
    private TurnPhase currentPhase;

    public Turn(List<Player> players) {}

    public Player getCurrentPlayer() {}
    public void advanceToNextPlayer() {}

    public TurnPhase getCurrentPhase() {}
    public void setPhase(TurnPhase newPhase) {}
}

// =======================================================================
// PACOTE: game.model.core.entities.cards
// =======================================================================

public abstract class LuckCard {
    protected final String description;
    protected final LuckType luckType;

    public LuckCard(String description, LuckType type) {}

    public String getDescription() {}
    public LuckType getLuckType() {}
    public abstract void execute(Player currentPlayer, Turn turn, Bank bank);
}

public class GetOutPrisonCard extends LuckCard {
    public GetOutPrisonCard(String description) { super(description, LuckType.FORTUNE); }
    @Override
    public void execute(Player currentPlayer, Turn turn, Bank bank) {}
}

public class GoToPrisonCard extends LuckCard {
    public GoToPrisonCard(String description) { super(description, LuckType.MISFORTUNE); }
    @Override
    public void execute(Player currentPlayer, Turn turn, Bank bank) {}
}

// ---- BACALHAU ABAIXO ----
public class PayCard extends LuckCard {
    private double amountToPay;

    public PayCard(String description, double amount) { super(description); }
    @Override
    public void execute(Player player, GameController gameController) {}
}

public class ReceiveCard extends LuckCard {
    private double amountToReceive;

    public ReceiveCard(String description, double amount) { super(description); }
    @Override
    public void execute(Player player, GameController gameController) {}
}

public class ReceiveFromOthersCard extends LuckCard {
    private double amountPerPlayer;

    public ReceiveFromOthersCard(String description, double amount) { super(description); }
    @Override
    public void execute(Player player, GameController gameController) {}
}
// ---- BACALHAU ACIMA ----

// =======================================================================
// PACOTE: game.model.core.entities.spaces
// =======================================================================

public abstract class Space {
    protected final String name;

    public Space(String name) {}
    public String getName() {}

    /** Método chamado quando um jogador para nesta casa. */
    public abstract void event(Player player);
}

public abstract class Property extends Space {
    protected final int price;
    protected Player owner;
    protected int currentRent;

    public Property(String name, int price) { super(name); }

    public int getPrice() {}
    public Player getOwner() {}
    public void setOwner(Player owner) {}
    public boolean isOwned() {}
    
    /** Retorna o valor do aluguel atualmente aplicável para esta propriedade. */
    public int getCurrentRent() {
        return this.currentRent;
    }

    @Override
    public void event(Player player) {}
}

public class Company extends Property {
    public Company(String name, int price) { super(name, price); }
    
    /** * Para Companhias, o aluguel é dinâmico. Este método atualiza e define
     * o `currentRent` baseado no resultado dos dados, antes de ser cobrado.
     */
    public void updateRent(Dice dice) {}
}

public class Place extends Property {
    private final PlaceGroup group;
    private final int housePrice;
    private final int hotelPrice;
    private final int hotelRentBonus;
    private final Map<Integer, Integer> houseRentMap; // <num_casas, valor_aluguel_total>
    private final Map<Building, Integer> buildings;

    public Place(String name, int price, PlaceGroup group, /*...outros params...*/ Map<Integer, Integer> houseRentMap) {
        super(name, price);
        this.buildings = new HashMap<>();
        this.buildings.put(Building.HOUSE, 0);
        this.buildings.put(Building.HOTEL, 0);
        // inicializar outros campos...
        updateCurrentRent(); // Define o aluguel base inicial
    }
    }

    public PlaceGroup getGroup() {}
    public int getHouseCount() { return this.buildings.get(Building.HOUSE); }
    public boolean hasHotel() { return this.buildings.get(Building.HOTEL) > 0; }
    public boolean buildHouse() { /* Lógica para adicionar casa e chamar updateCurrentRent() */ return true; }
    public boolean buildHotel() { /* Lógica para adicionar hotel e chamar updateCurrentRent() */ return true; }
    
    /** * Atualiza o valor do aluguel corrente com base na quantidade de casas/hotel.
     * Este método é chamado internamente sempre que uma construção é feita/vendida.
     */
    private void updateCurrentRent() {
        int rent = 0;
        rent = houseRentMap.get(getHouseCount());
        if (hasHotel()) {
            // Lógica para aluguel com hotel
            rent += hotelRentBonus; 
        }
        this.currentRent = rent;
    }
}

public class GoToPrison extends Space {
    public GoToPrison(String name) { super(name); }
    @Override
    public void event(Player player) {}
}

public class Prison extends Space {
    public Prison(String name) { super(name); }
    @Override
    public void event(Player player) { /* Apenas visita */ }
}

public class Start extends Space {
    private final int salary;

    public Start(String name, int salary) { super(name); }
    @Override
    public void event(Player player) {}
    public int getSalary() { return salary; }
}

// Nota: As classes Chance, Tax, e Profit do diagrama inicial podem ser implementadas
// de forma similar às outras subclasses de Space, ou suas lógicas podem ser
// encapsuladas dentro das cartas de Sorte/Cofre. A estrutura acima é flexível
// para ambas as abordagens.
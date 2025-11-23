package model.core.entities;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class LuckDeck {
    private List<LuckCard> deck;
    private Prison prisonSpace;
    private List<Player> allPlayers;
    private boolean initialized;
    
    public LuckDeck() {
        this.deck = new ArrayList<>();
        this.prisonSpace = null;
        this.allPlayers = null;
        this.initialized = false;
    }
    
    /**
     * Sets the prison space reference for GoToPrisonCard.
     * 
     * @param prisonSpace The prison space.
     */
    public void setPrisonSpace(Prison prisonSpace) {
        this.prisonSpace = prisonSpace;
        initializeDeckIfReady();
    }
    
    /**
     * Sets the list of all players for ReceiveFromOthersCard.
     * 
     * @param allPlayers List of all players.
     */
    public void setAllPlayers(List<Player> allPlayers) {
        this.allPlayers = allPlayers;
        initializeDeckIfReady();
    }
    
    /**
     * Initializes the deck only if both prison space and players list are set.
     */
    private void initializeDeckIfReady() {
        if (!initialized && prisonSpace != null && allPlayers != null) {
            initializeDeck();
            initialized = true;
        }
    }
    
    /**
     * Initializes the deck with standard luck cards.
     */
    private void initializeDeck() {
        // Clear existing deck
        this.deck.clear();
        
        // Add some example cards - in a real game these would be loaded from configuration
        this.deck.add(new ReceiveCard("chance1", 25, "A prefeitura mandou abrir uma nova avenida, para o que desapropriou vários prédios. Em consequência seu terreno valorizou. Receba 25"));
        this.deck.add(new ReceiveCard("chance2", 150, "Houve um assalto à sua loja, mas você estava segurado. Receba 150"));
        this.deck.add(new ReceiveCard("chance3", 80, "Um amigo tinha lhe pedido um empréstimo e se esqueceu de devolver. Ele acaba de se lembrar. Receba 80"));
        this.deck.add(new ReceiveCard("chance4", 200, "Você está com sorte. Suas ações na Bolsa de Valores estão em alta. Receba 200"));
        this.deck.add(new ReceiveCard("chance5", 50, "Você trocou seu carro usado com um amigo e ainda saiu lucrando. Receba 50"));
        this.deck.add(new ReceiveCard("chance6", 50, "Você acaba de receber uma parcela do seu 13° salário. Receba 50"));
        this.deck.add(new ReceiveCard("chance7", 100, "Você tirou o primeiro lugar no Torneio de Tênis do seu clube. Parabéns! Receba 100"));
        this.deck.add(new ReceiveCard("chance8", 100, "O seu cachorro policial tirou 1° prêmio na exposição do Kennel Club. Receba 100"));
        this.deck.add(new GetOutPrisonCard("chance9", "Saída livre da prisão.", null)); // Owner will be set when drawn
        
        // Create and configure ReceiveFromOthersCard
        ReceiveFromOthersCard receiveCard = new ReceiveFromOthersCard("chance11", 50, "Você apostou com os parceiros deste jogo e ganhou. Cada um lhe paga 50.");
        if (this.allPlayers != null) {
            receiveCard.setAllPlayers(this.allPlayers);
        }
        this.deck.add(receiveCard);
        this.deck.add(new ReceiveCard("chance12", 45, "Você saiu de férias e se hospedou na casa de um amigo. Você economizou o hotel. Receba 45"));
        this.deck.add(new ReceiveCard("chance13", 100, "Inesperadamente você recebeu uma herança que já estava esquecida. Receba 100"));
        this.deck.add(new ReceiveCard("chance14", 100, "Você foi promovido a diretor da sua empresa. Receba 100"));
        this.deck.add(new ReceiveCard("chance15", 20, "Você jogou na Loteria Esportiva com um grupo de amigos. Ganharam! Receba 20"));
        this.deck.add(new PayCard("chance16", 15, "Um amigo pediu-lhe um empréstimo. Você não pode recusar. Pague 15."));
        this.deck.add(new PayCard("chance17", 25, "Você vai casar e está comprando um apartamento novo. Pague 25."));
        this.deck.add(new PayCard("chance18", 45, "O médico lhe recomendou repouso num bom hotel de montanha. Pague 45."));
        this.deck.add(new PayCard("chance19", 30, "Você achou interessante assistir a estréia da temporada de ballet. Compre os ingressos. Pague 30."));
        this.deck.add(new PayCard("chance20", 100, "Parabéns! Você convidou seus amigos para festejar o aniversário. Pague 100."));
        this.deck.add(new PayCard("chance21", 100, "Você é papai outra vez! Despezas de maternidade. Pague 100."));
        this.deck.add(new PayCard("chance22", 40, "Papai os livros do ano passado não servem mais, preciso de livros novos. Pague 40."));
        this.deck.add(new GoToPrisonCard("chance23", "Vá para a prisão sem receber nada. (talvez eu lhe faça uma visita...)", prisonSpace));
        this.deck.add(new PayCard("chance24", 30, "Você estacionou seu carro em lugar proibido e entrou na contra mão. Pague 30."));
        this.deck.add(new PayCard("chance25", 50, "Você acaba de receber comunicação do Imposto de Renda. Pague 50."));
        this.deck.add(new PayCard("chance26", 25, "Seu clube está ampliando as piscinas. os sócios devem contribuir. Pague 25."));
        this.deck.add(new PayCard("chance27", 30, "Renove a tempo a licença do seu automóvel. Pague 30."));
        this.deck.add(new PayCard("chance28", 45, "Seus parentes do interior vieram passar umas \"férias\" na sua casa. Pague 45."));
        this.deck.add(new PayCard("chance29", 50, "Seus filhos já vão para a escola. Pague a primeira mensalidade. Pague 50."));
        this.deck.add(new PayCard("chance30", 50, "A geada prejudicou a sua safra de café. Pague 50."));
        
        shuffle();
    }
    
    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(this.deck);
    }
    
    /**
     * Draws and removes the next card from the deck.
     * 
     * @return The next luck card, or null if deck is empty.
     */
    public LuckCard drawCard() {
        if (deck.isEmpty()) {
            return null;
        }
        
        // Remove and return the first card
        return deck.remove(0);
    }
    
    /**
     * Adds a card to the deck.
     * 
     * @param card The card to add.
     */
    public void addCard(LuckCard card) {
        this.deck.add(card);
    }
    
    /**
     * Returns the number of cards in the deck.
     * 
     * @return The deck size.
     */
    public int size() {
        return this.deck.size();
    }
    
    /**
     * Checks if the deck is empty.
     * 
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.deck.isEmpty();
    }
    
    /**
     * Resets the deck to its initial state with all default cards.
     * Useful when the deck becomes empty and needs to be replenished.
     */
    public void reset() {
        this.deck.clear();
        initializeDeck();
    }
}

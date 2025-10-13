package model.core.entities;

class Prison extends Space {
    
    public Prison(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public void event(Player player) {
        // Se o jogador não está na prisão, não faz nada
        if (!player.isInPrison()) {
            return;
        }
        
        // Incrementa o contador de turnos na prisão
        player.incrementTurnsInPrison();
    }
    
    /**
     * Processa a tentativa de saída da prisão usando dados duplos.
     * 
     * @param player O jogador tentando sair.
     * @param dice1 O valor do primeiro dado.
     * @param dice2 O valor do segundo dado.
     * @return true se o jogador conseguiu sair (dados duplos), false caso contrário.
     */
    public boolean tryDoubleDice(Player player, int dice1, int dice2) {
        if (!player.canTryDoubleDice()) {
            return false;
        }
        
        if (dice1 == dice2) {
            player.releaseFromPrison();
            return true;
        }
        
        return false;
    }
    
    /**
     * Processa o turno de um jogador na prisão, incluindo tentativas de saída.
     * 
     * @param player O jogador na prisão.
     * @param dice1 O valor do primeiro dado.
     * @param dice2 O valor do segundo dado.
     * @return true se o jogador conseguiu sair da prisão, false caso contrário.
     */
    public boolean processPrisonTurn(Player player, int dice1, int dice2) {
        if (!player.isInPrison()) {
            return false;
        }
        
        // Incrementa o contador de turnos na prisão
        player.incrementTurnsInPrison();
        
        // Tenta sair com dados duplos
        if (tryDoubleDice(player, dice1, dice2)) {
            return true; // Conseguiu sair
        }
        
        return false; // Ainda está na prisão
    }
    
    
    /**
     * Processa o uso de uma carta "Saia da Prisão".
     * 
     * @param player O jogador tentando usar a carta.
     * @return true se a carta foi usada com sucesso, false caso contrário.
     */
    public boolean useGetOutPrisonCard(Player player) {
        if (player.hasGetOutPrisonCard() && player.isInPrison()) {
            player.useGetOutPrisonCard();
            player.releaseFromPrison();
            return true;
        }
        return false;
    }
    
}

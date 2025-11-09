package model.core.entities;


class GoToPrison extends Space {
    private Prison prisonSpace;
    
    public GoToPrison(Space next) {
        super("Go to Prison", next);
    }
    
    public GoToPrison(String name, Space next) {
        super(name, next);
    }
    
    /**
     * Sets the prison space reference.
     * 
     * @param prisonSpace The prison space to send players to.
     */
    public void setPrisonSpace(Prison prisonSpace) {
        this.prisonSpace = prisonSpace;
    }
    
    @Override
    public String event(Player player) {
        if (prisonSpace == null) {
            System.err.println("ERRO: Prisão não foi configurada em GoToPrison");
            return "Erro ao enviar para prisão";
        }
        
        player.sendToPrison(prisonSpace);
        return "Você foi enviado para a PRISÃO!";
    }
}

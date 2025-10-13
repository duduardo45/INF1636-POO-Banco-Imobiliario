package model.core.entities.spaces;

public class Prison extends Space {
    
    public Prison() {
        super("Prison");
    }
    
    @Override
    public void event() {
        // A lógica específica da prisão será tratada pelo GameModel
        // Este método é chamado quando um jogador para na casa prisão
    }
}

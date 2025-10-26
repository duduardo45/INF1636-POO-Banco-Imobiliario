package model.core.entities;

public class Start extends Space {
    private final int honorarium = 200;
    
    public Start(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public void event(Player player) {
        // Recebe $200 ao passar ou parar no Start
        player.credit(honorarium);
    }
    
    public int getHonorarium() {
        return honorarium;
    }
}

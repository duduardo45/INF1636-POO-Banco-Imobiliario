package model.core.entities;


class GoToPrison extends Space {
    
    public GoToPrison(Space next) {
        super("Go to Prison", next);
    }
    
    public GoToPrison(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public void event(Player player) {
        player.sendToPrison();
    }
}

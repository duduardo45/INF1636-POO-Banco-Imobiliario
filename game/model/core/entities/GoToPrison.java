package model.core.entities;


class GoToPrison extends Space {
    
    public GoToPrison(Space next) {
        super("Go to Prison", next);
    }
    
    @Override
    public void event(Player player) {
        // TODO: Implement logic to send player to prison
    }
}

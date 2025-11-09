package model.core.entities;

class LuckSpace extends Space {
    LuckSpace(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public void event(Player player) {
        // Sortear carta - implementar na 3ª iteração
    }
}
